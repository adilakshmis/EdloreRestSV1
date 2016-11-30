package com.cds.accopen.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxItem;
import com.cds.box.util.CDSBoxAPIConnection;

/**
 * @author Kiru V
 *
 */
@Path(value="/check")
public class AccountOpeningRefNo {

	final Logger logger = Logger.getLogger(AccountOpeningRefNo.class);
	
	/**
	 * @param ref
	 * @return
	 */
	@GET
	@Path("{ref}")	
	public Response checkRef(@PathParam("ref") String ref)
	{
		boolean isRefNoExists = false;
		BoxDeveloperEditionAPIConnection api = null;
		
		String refNo = ref;
		if(refNo == null || "".equals(refNo))
		{
			return Response.status(404).entity("please provide valid ref no").build();
		}
		
		
		api = CDSBoxAPIConnection.getAppUserConnection();
		
		System.out.println("ref no is ---------------------- "+ref);
		 BoxFolder accOpeningFolder = new BoxFolder(api, "11523321485");
		 // iterating the items avalilable inside the boxfolder
	       for (BoxItem.Info itemInfo : accOpeningFolder) {
	    	   logger.info("itemInfo.getName():::"+itemInfo.getName());
	    	   
	    	   
	    	   if(refNo.equalsIgnoreCase(itemInfo.getName()))
	    	   {
	    		   isRefNoExists = true;
	    		   return Response.status(200).entity(String.valueOf(isRefNoExists)).build();
	    	   }
	       }
	       return Response.status(200).entity(String.valueOf(isRefNoExists)).build();
	}
	
}
