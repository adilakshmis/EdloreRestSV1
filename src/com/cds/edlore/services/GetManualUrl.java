package com.cds.edlore.services;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.box.sdk.BoxAPIException;
import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxItem;
import com.box.sdk.BoxItem.Info;
import com.cds.box.util.CDSBoxAPIConnection;
import com.cds.box.util.GetFolderInfo;
import com.cds.edlore.util.Asset;
import com.cds.edlore.util.BrowseUrl;
import com.cds.edlore.util.DetectUrl;
import com.cds.edlore.util.ListOfAsset;

/**
 * @author Kiru V
 *
 */
/**
 * @author Mits
 *
 */
@Path(value="/getmanuallink")
public class GetManualUrl {
	
	
	final Logger logger = Logger.getLogger(GetManualUrl.class);

	/**
	 * @return
	 */
	@GET
	@Path("/browse")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllFolders()
	{
		logger.info("Enter into browse call-------");
		
		List<BrowseUrl> listOfBrowseUrls = null;
		BrowseUrl browseUrl = null;
		List<String> listOfModelNames = null;
		
		try {
		listOfBrowseUrls = new ArrayList<BrowseUrl>();
		logger.info("before box api connection--------------");
		BoxDeveloperEditionAPIConnection api = CDSBoxAPIConnection.getAppUserConnection();
		
		logger.info("before box api after box api --------------"+api);
		// Creating the instance of BoxFolder i.e Root Folder folder
	    BoxFolder rootFolder = new BoxFolder(api, "7728640417");
		
	    // iterating the items avalilable inside the boxfolder
	       for (BoxItem.Info itemInfo : rootFolder) {
	    	   logger.info("itemInfo.getName():::"+itemInfo.getName());
	    	   
	    	   // This condition is used to check whether the given assetid is valid or not. If not valid throws an exception
	    	   browseUrl = new BrowseUrl();
	       		if (itemInfo instanceof BoxFolder.Info) {
	       			BoxFolder folder1 = (BoxFolder) itemInfo.getResource();
	       			logger.info("section folder info is ----------"+ folder1.getInfo().getName());
	       			browseUrl.setDeviceName(folder1.getInfo().getName());
	       			
	       			listOfModelNames = new ArrayList<String>();
					for(BoxItem.Info boxItem : folder1)
	       			{
						BoxFolder folder2 = (BoxFolder) boxItem.getResource();
						listOfModelNames.add(folder2.getInfo().getName());
	       			}
					browseUrl.setListOfModelNames(listOfModelNames);
	       		}
	       		listOfBrowseUrls.add(browseUrl);
	       }
		}catch (Exception e) {
			
			logger.info("with in exception block of get all folders ------------"+e);
			if(e instanceof BoxAPIException)
			{
				BoxAPIException exception = (BoxAPIException) e;
				e.printStackTrace();
				return Response.status(exception.getResponseCode()).entity(exception.getMessage()).build();
			}
			logger.info("Exception is ------------------ "+e);
			
			return Response.status(404).entity(e.getMessage()).build();
			}
	       return Response.status(200).entity(listOfBrowseUrls).build();
	}
	
	
	/**
	 * @param deviceName
	 * @param modelName
	 * @return
	 */
	@GET
	@Path("{deviceName}/{modelName}/detect")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllSectionDetails(@PathParam("deviceName") String deviceName, @PathParam("modelName")String modelName)
	{
		logger.info("Enter into detect urls=== device name ="+ deviceName +" model name ="+ modelName );
		
		DetectUrl detectUrl = null;
		List<String> sections = null;
		try {
		detectUrl = new DetectUrl();
		
		if(("".equals(deviceName) || deviceName == null) && ("".equals(modelName) || modelName == null)){
			return Response.status(400).entity("Please provide valid values ").build();
		}
		
		BoxDeveloperEditionAPIConnection api = CDSBoxAPIConnection.getAppUserConnection();
		
		// Creating the instance of BoxFolder i.e Root Folder folder
	    BoxFolder rootFolder = new BoxFolder(api, "7728640417");
		BoxFolder retunFolder = null;
		
		retunFolder = GetFolderInfo.getFolderInfo(api, rootFolder, deviceName);
		
		if(retunFolder == null)
		{
			return Response.status(404).entity("Given deviceName is "+ deviceName +" not found.").build();
		}
		
		retunFolder  = GetFolderInfo.getFolderInfo(api, retunFolder, modelName);
		if(retunFolder == null)
		{
			return Response.status(404).entity("Given modelName is "+modelName +" not found.").build();
		}
		
		sections = retriveAllSectionFolderInfo(retunFolder);
		
		detectUrl.setDeviceName(deviceName);
		detectUrl.setModelName(modelName);
		detectUrl.setListOfSections(sections);
		
	     logger.info("returning the detect url ------- "+ detectUrl);
	     
		}catch (Exception e) {
		
		if(e instanceof BoxAPIException)
		{
			BoxAPIException exception = (BoxAPIException) e;
			e.printStackTrace();
			return Response.status(exception.getResponseCode()).entity(exception.getMessage()).build();
		}
		logger.info(e.getClass());
		
		return Response.status(404).entity(e.getMessage()).build();
		}
		return Response.status(200).entity(detectUrl).build();
		
		
	}	
	
	
	
	/**
	 * @param deviceName
	 * @param modelName
	 * @param sectionName
	 * @return
	 */
	@GET
	@Path("{deviceName}/{modelName}/{sectionName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get_manual_url(@PathParam("deviceName") String deviceName, @PathParam("modelName")String modelName, 
								@PathParam("sectionName")String sectionName)
	{
		logger.info("Enter into get manual url=== device name ="+ deviceName +" model name ="+ modelName + " sectionName "+ sectionName);
		List<ListOfAsset> assetList=null;
		try{
			/*
			 *This Condition returns an error code 400, if the provided values are null 
			 */			
			if(("".equals(deviceName) || deviceName == null) && ("".equals("modelName") || modelName == null) && ("".equals(sectionName) || sectionName == null) ){
				return Response.status(400).entity("Please provide valid values ").build();
			}
			
			//Creating the object for EdloreBoxApiConnection to get the App user Connection.
			//this connection is required to perform content API operations
			
			
			// This message is used to retrieve the files inside the Asset Id folder 
			//which takes parameters of BoxApIConnection and AssetId and returns a list of files
			BoxDeveloperEditionAPIConnection api = CDSBoxAPIConnection.getAppUserConnection();
			
			// Creating the instance of BoxFolder i.e Root Folder folder
		    BoxFolder rootFolder = new BoxFolder(api, "7728640417");
			BoxFolder retunFolder = null;
			
			retunFolder = GetFolderInfo.getFolderInfo(api, rootFolder, deviceName);
			
			if(retunFolder == null)
			{
				return Response.status(404).entity("Given deviceName is "+ deviceName +" not found.").build();
			}
			
			retunFolder  = GetFolderInfo.getFolderInfo(api, retunFolder, modelName);
			if(retunFolder == null)
			{
				return Response.status(404).entity("Given modelName is "+modelName +" not found.").build();
			}
			
			assetList =  retrieveAllFiles(api, retunFolder, sectionName);
			logger.info("asset list is :: "+assetList);
			
			if(assetList == null)
			{
				return Response.status(404).entity("Given section name is  "+sectionName +" not found.").build();
			}
			
		}catch (Exception e) {
			
			if(e instanceof BoxAPIException)
			{
				BoxAPIException exception = (BoxAPIException) e;
				e.printStackTrace();
				return Response.status(exception.getResponseCode()).entity(exception.getMessage()).build();
			}
			logger.info(e.getClass());
			logger.debug(e.getClass());
			
			return Response.status(404).entity(e.getMessage()).build();
		}
		// this will return a code of 200 and the list of AssetFolder info. i.e the files inside the assetid folder
		return Response.status(200).entity(assetList).build();
	}
	
	/**
	 * @param modelFolder
	 * @return
	 */
	private List<String> retriveAllSectionFolderInfo(BoxFolder modelFolder)
	{
		
		List<String> sectionFolderNames = new  ArrayList<String>();
		

	   	 // iterating the items avalilable inside the boxfolder
	       for (BoxItem.Info itemInfo : modelFolder) {
	    	   logger.info("itemInfo.getName():::"+itemInfo.getName());
	    	   
	    	   // This condition is used to check whether the given assetid is valid or not. If not valid throws an exception
	       		
	       		if (itemInfo instanceof BoxFolder.Info) {
	       			BoxFolder rootFolder = (BoxFolder) itemInfo.getResource();
	       			logger.info("section folder info is ----------"+ rootFolder.getInfo().getName());
	       			sectionFolderNames.add(rootFolder.getInfo().getName());
	       		}
		logger.info("end of retrive all sections "+sectionFolderNames);
	}
		return sectionFolderNames;
	
}
	
	
   /**
 * @param api
 * @param modelFolder
 * @param folderName
 * @return
 */
public List<ListOfAsset> retrieveAllFiles(BoxDeveloperEditionAPIConnection api,BoxFolder modelFolder, String folderName){
   
	   
	   logger.info("inside copy folder method");
	   List<ListOfAsset> assetList = null;
   
	   BoxFolder boxFolder = modelFolder;
	 
   	
	 
   	 // iterating the items avalilable inside the boxfolder
       for (BoxItem.Info itemInfo : boxFolder) {
    	   logger.info("itemInfo.getName():::"+itemInfo.getName()+"::assetId::"+folderName);
    	   
    	   // This condition is used to check whether the given assetid is valid or not. If not valid throws an exception
       	if(folderName.equalsIgnoreCase(itemInfo.getName())){
       		// Checking whether the item is of instance BoxFolder.Info
       		assetList =new ArrayList<ListOfAsset>();
       		
       		ListOfAsset listOfAsset = null;
       		logger.info("Enter into if equals method.........");
       		
       		if (itemInfo instanceof BoxFolder.Info) {
       			BoxFolder rootFolder = (BoxFolder) itemInfo.getResource();
       			logger.info("Child root folder info is :: "+ rootFolder.getInfo().getName());

       			
       			Iterable<Info> itr =	rootFolder.getChildren();
       			Iterator<Info> info =  itr.iterator();
       			
       			while (info.hasNext()) {
				   Info in =	info.next();
				   logger.info(in.getName());
					logger.debug(in.getName());
				  listOfAsset = getListAsset(api, in);
				  assetList.add(listOfAsset);
				}
       		 return assetList;
       		}
       	
       	}
       }
       logger.info("AssetList:::"+assetList);
       logger.debug("AssetList:::"+assetList);
       
	return assetList;
   }

   /**
 * @param api
 * @param info
 * @return
 */
private ListOfAsset getListAsset(BoxDeveloperEditionAPIConnection api, Info info)
   {
	   ListOfAsset listOfAsset = null;
	  
	   List<Asset> assetList = null;
	   
	   listOfAsset = new ListOfAsset();
	   assetList = new ArrayList<Asset>();
	   
	   listOfAsset.setFolderName(info.getName());
	   logger.info("folder info is ----"+info);
	  
		logger.debug("folder debug is ----"+info);
		   
	   
	   BoxFolder folder = new BoxFolder(api, info.getID());
	   System.out.println("box folder is -------"+ folder.getInfo().getName());
	   Iterable<Info> itr =	folder.getChildren();
			Iterator<Info> fileInfo =  itr.iterator();
			Asset asset = null;
			while (fileInfo.hasNext()) {
				Info in =	fileInfo.next();
				asset = new Asset();
				
				asset.setUpload_file_name(in.getName());
				//asset.setDownload_url("http://172.16.11.125:9080/cds/rest/download/"+in.getID());
				asset.setDownload_url("http://ibmdemo.mitsind.com/cds/rest/download/"+in.getID());
				//asset.setDownload_url("http://172.16.11.125:9080/cds/rest/download/manual?manualId="+in.getID());
				
				logger.info(in.getName());
				logger.debug(in.getName());
				
				assetList.add(asset);
			}
	   
			listOfAsset.setListAsset(assetList);
			logger.info("list of asset :: "+ listOfAsset);
	   
	   return listOfAsset;
   }
  
  
	
}
