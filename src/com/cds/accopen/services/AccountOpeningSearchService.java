package com.cds.accopen.services;

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

import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.box.sdk.BoxFile;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxItem;
import com.cds.accopen.util.AccountOpeninFileInfo;
import com.cds.box.util.CDSBoxAPIConnection;

/**
 * @author Kiru V
 *
 */
@Path(value="/account")
public class AccountOpeningSearchService {

	final Logger logger = Logger.getLogger(AccountOpeningSearchService.class.getName());
	private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("com/cds/props/applicationProperties");
	
	/**
	 * @param refNo
	 * @return
	 */
	@GET
	@Path("/open")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAccountDetails(@QueryParam("refNo") String refNo)
	{
		logger.info("Enter get account details refNo ="+refNo);
		BoxDeveloperEditionAPIConnection api = null;
		BoxFolder accOpnRootFolder = null;
	    boolean isrefNoFolderExists = false; 
	    BoxFolder.Info folderInfo = null;
	    List<AccountOpeninFileInfo> listAccountOpeninFileInfos = null;
	    
		try {
			
		if(("".equals(refNo) || refNo == null)){
			return Response.status(404).entity("Please provide valid refNo ").build();
		
		}
		api = CDSBoxAPIConnection.getAppUserConnection();
		
		// Creating the instance of BoxFolder i.e Root Folder folder
		accOpnRootFolder = new BoxFolder(api, "11523321485");
		
		
		// Iterate all child folders of account opening folder
		for (BoxItem.Info itemInfo : accOpnRootFolder) {
			
			if (itemInfo instanceof BoxFolder.Info) {
				folderInfo = (BoxFolder.Info) itemInfo;
		       
		        if(refNo.equalsIgnoreCase(folderInfo.getName()))
		        {
		        	isrefNoFolderExists = true;
		        	break;
		        }		        
		    }
			isrefNoFolderExists = false;
		     
		}
		
		if(!isrefNoFolderExists)
		{
			return Response.status(404).entity("Requested documents not found. ").build();
		}
		
		logger.info("ref no folder name "+folderInfo.getName());
		
		BoxFolder accFolder = folderInfo.getResource();
		logger.info("ref no folder id "+accFolder.getID());
		
		AccountOpeninFileInfo accountOpeninFileInfo = null;
		
		
		listAccountOpeninFileInfos = new ArrayList<AccountOpeninFileInfo>();
		
		
			for (BoxItem.Info itemInfo2 : accFolder) {
			
				if (itemInfo2 instanceof BoxFile.Info) {
					
					accountOpeninFileInfo = new AccountOpeninFileInfo();
					
			        BoxFile.Info accFileInfo = (BoxFile.Info) itemInfo2;
			         accountOpeninFileInfo.setFileName(accFileInfo.getName());
			         
			        
			         accountOpeninFileInfo.setUrl(resourceBundle.getString("downloadUrl")+accFileInfo.getID());
			    } 
				listAccountOpeninFileInfos.add(accountOpeninFileInfo);
		}
		
		
		}catch (Exception e) {
			return Response.status(400).entity(e.getMessage()).build();
		}
		return Response.status(200).entity(listAccountOpeninFileInfos).build();
}
}