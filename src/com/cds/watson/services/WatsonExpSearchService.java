package com.cds.watson.services;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import velocity.VelocityPort;
import velocity.objects.Content;
import velocity.objects.Document;
import velocity.objects.VseIndexStatus;
import velocity.objects.VseStatus;
import velocity.soap.Authentication;
import velocity.types.QuerySearch;
import velocity.types.QuerySearchResponse;
import velocity.types.SearchCollectionStatus;
import velocity.types.SearchCollectionStatusResponse;

import com.cds.box.util.WatsonExplorerConnection;
import com.cds.watson.util.WatsonSearchDocument;


/**
 * @author Kiru V
 *
 */
@Path(value="/wex")
public class WatsonExpSearchService {
	
	final Logger logger = Logger.getLogger(WatsonExpSearchService.class.getName());
	private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("com/cds/props/applicationProperties");
	
	@GET	
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	public Response search(@QueryParam("query")String query)
	{
		
		VseStatus vses=null;	        
	    VseIndexStatus vseis=null;
		SearchCollectionStatusResponse searchCollectionStatusResponse = null;
		List<WatsonSearchDocument> wexResult = null;
		WatsonSearchDocument watsonSearchDocument = null;
		String searchParams = null;
		SearchCollectionStatus scs = null;
		Authentication authentication = null;
		VelocityPort velocityPort = null;
		try {
			
			scs = new SearchCollectionStatus();
			
		    authentication = WatsonExplorerConnection.getAuthenticationForWatson();
		    velocityPort = WatsonExplorerConnection.getVelocityPort();
	        //set authentication
	        scs.setAuthentication(authentication);
	        

	        //set my searchcollection name
	        scs.setCollection(resourceBundle.getString("SearchCollectionName"));	        
	        logger.info("the searchCollection status:"+scs);

	        //get the search collection sattus
	        searchCollectionStatusResponse = velocityPort.searchCollectionStatus(scs);	        
	        logger.info("the status response is:"+searchCollectionStatusResponse);
	        
	       
		
		if("".equals(query)|| query == null)
		{
			return Response.status(400).entity("Requested Uri is incorrect-------- ").build();
		}		
		try {
		
			searchParams = resourceBundle.getString(query);
		 
		}catch (Exception e) {
			searchParams = query;
		}
		
		logger.info("searchParams -------------- "+searchParams);
		try {			
	
		
		if(searchCollectionStatusResponse == null)
		{
			return Response.status(404).entity("Search collection status is not found please try agin ").build();
		}
		
		vses=searchCollectionStatusResponse.getVseStatus();
    	//get th indexObj
    	vseis = vses.getVseIndexStatus();
    	logger.info("the vseindex is "+vseis);
    	
        QuerySearch querySearch = new QuerySearch();
	        querySearch.setAuthentication(authentication); 
	  	    querySearch.setQuery(searchParams);
	  	    querySearch.setSources(resourceBundle.getString("SearchCollectionName"));
	        querySearch.setBrowseClustersNum(20);
	        querySearch.setNum(20);
	        querySearch.setCluster(true);
	        int no= querySearch.getNum();
	        logger.info("querySearch number is:"+no);  	
    	
    	String appendTheQueryParams = searchParams.replaceAll(" ", "%20");
    	System.out.println("appending params ------------ "+ appendTheQueryParams);
    	
	        QuerySearchResponse response= velocityPort.querySearch(querySearch);
   	  	    
	    //    logger.info("get QueryResultSet is:"+response.getQueryResults().getList());   	  	    
	       
   	  	    
	       if (response.getQueryResults().getList() != null) {
	    	   wexResult = new ArrayList<WatsonSearchDocument>();
	    	   
	     for(Document document : response.getQueryResults().getList().getDocument()){
   		 
	    		 String vseKey=document.getVseKey();	  	    
	    		 String document_key=vseKey.substring(1, vseKey.length()-1);
	  	
	    		 watsonSearchDocument = new WatsonSearchDocument();
	    		 logger.info("Enter into the get content for loop vse key "+ document_key);
	  	  
	    		 watsonSearchDocument.setUrl(resourceBundle.getString("WatsonDocViewUrl")+"sources="+resourceBundle.getString("SearchCollectionName")+"&query="+appendTheQueryParams+"%20DOCUMENT_KEY%3a%22%7b"+document_key+"%7d%22&v%3aframe=cache&search-vfile=viv_XEH6O5&search-state=%28root%29%7croot&");
	    		 System.out.println("watson ----- "+ watsonSearchDocument);	
	    		 
	  	 for(Content content : document.getContent()){  	    		
	  	    		System.out.println("with in for each of content -----");
	  	    		
	  	    		if("name".equals(content.getName()))
	  	    		{
	  	    			watsonSearchDocument.setName(content.getValue());
	  	    		}if("mimetype".equals(content.getName()))
	  	    		{
	  	    			watsonSearchDocument.setMimetype(content.getValue());
	  	    		}if("title".equals(content.getName()))
	  	    		{
	  	    			watsonSearchDocument.setTitle(content.getValue());	  	    			
	  	    		}
	  	    	} 
	  	    	wexResult.add(watsonSearchDocument);
	  	    }   	  	    	  	  	    	
	        
   	 
   	 if("".equals(wexResult))
   	 {
   		return Response.status(200).entity("No Documents Found ").build();
   	 }
   	  	    }
   	  
   	  else 
   	  {
   		  return Response.status(404).entity("The requested search collection is empty --------- ").build();   		  
   	  }
   	 	} catch (Exception e) {
				e.printStackTrace();
	}
	}catch (Exception e) {
			e.printStackTrace();
		}
		return Response.status(200).entity(wexResult).build();
	}
	
}

