package com.cds.edlore.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.box.sdk.BoxFile;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxItem.Info;
import com.box.sdk.Metadata;
import com.cds.app.config.FileSystemConfig;
import com.cds.box.util.CDSBoxAPIConnection;
import com.cds.edlore.amazon.util.AmazonHttpClient;
import com.cds.edlore.util.UploadManual;
import com.cds.edlore.util.UploadManualSatus;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

/**
 * @author Kiru V
 * 
 *  Used to get request as json and make request to amazon ws for doc, 
 *  store the received doc into local system 
 *
 */

@Path(value="/upload")
public class UploadService {

	
	final Logger logger = Logger.getLogger(UploadService.class.getName());
	/* If the requested method signature is matches to method signature then method will 
	 * start to execute	and every request one service obj will creates*/
	/**
	 * @param deviceName
	 * @param modelName
	 * @param sectionName
	 * @param jsonArray
	 * @return
	 */
	@POST
	@Path("{deviceName}/{modelName}/{sectionName}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadManuals(@PathParam("deviceName")String deviceName, @PathParam("modelName")String modelName, @PathParam("sectionName")String sectionName, JSONArray jsonArray)
	{
		logger.info("Enter into upload manual method ::"+ jsonArray);
		
		// declare the input json mutators
		UploadManual uploadManual = null;
		List<UploadManual> manualsInputList = null;
		
		// declare the output json mutators
		UploadManualSatus uploadManualSatus = null;
		List<UploadManualSatus> manualsOutputList = null;
		BoxDeveloperEditionAPIConnection api = null;
		
		/*
		 *This Condition returns an error code 400, if the provided values are null 
		 */			
		if(("".equals(deviceName) || deviceName == null) && ("".equals("modelName") || modelName == null) && ("".equals(sectionName) || sectionName == null) ){
			return Response.status(400).entity("Please provide valid input value ").build();
		}		
		if(jsonArray == null)
		{
			return Response.status(400).entity("json file can't be null ").build();
		}
		manualsInputList = parseTheJson(jsonArray);
		
		logger.debug("after parseTheJson the json method :: upload input json "+ manualsInputList);
			
		Info itemInfo = null;
		String rootFolderId = "7728640417";
		api = CDSBoxAPIConnection.getAppUserConnection();
		
		itemInfo = createFolder(api, rootFolderId, deviceName);
		itemInfo = createFolder(api, itemInfo.getID(), modelName);
		itemInfo = createFolder(api, itemInfo.getID(), sectionName);
		
		manualsOutputList = new ArrayList<UploadManualSatus>();
			
			for(int i=0; i<manualsInputList.size(); i++)
			{
				String url = null;
				InputStream inputStream = null;
				UploadManualSatus status = null;
				
				status = new UploadManualSatus();
			
				uploadManual = manualsInputList.get(i);
				url = uploadManual.getManual_url();
				status.setFile_Id(uploadManual.getUpload_file_name());
				AmazonHttpClient amazonHttpClient = new AmazonHttpClient();
				inputStream = amazonHttpClient.getFileFromAmazonws(url, manualsOutputList, status);
				
				if(inputStream != null)
				{
					try
					{
					String modifiedFileName=writeIntoFileSystem(uploadManual.getId(), uploadManual.getUpload_file_name(), inputStream);
					
					createResourceFoldersAndUploadtoBox(itemInfo, api, uploadManual.getResource_type(), modifiedFileName, uploadManual.getId());
					
						status.setStatus("200");
						status.setMessage("success");
						status.setFile_Id(modifiedFileName);
						manualsOutputList.add(status);
						
					}catch (Exception e) {
						status.setStatus("400");
						status.setFile_Id(uploadManual.getUpload_file_name());
						status.setMessage("Failed");
						manualsOutputList.add(status);
					}
				}
			}
		// If request is successfully processed then the success response will return 
		return Response.status(200).entity(manualsOutputList).build();
	}


	/*
	 * method define upload file to a folder by calling 
	 * the uploadFile(InputStream, String) method.
	 */

	
	/**
	 * @param api
	 * @param folderId
	 * @param folderName
	 * @return
	 */
	private Info createFolder(BoxDeveloperEditionAPIConnection api, String folderId, String folderName)
	{
			boolean flagForFolder = false;
		
			Info finalFolderInfo = null;
		
		try {
			
			BoxFolder boxFolder = new BoxFolder(api, folderId);
			Iterable<Info> folderNames = boxFolder.getChildren();
			Iterator<Info> folNames =	folderNames.iterator();
		
				if(!folNames.hasNext())
				{
					logger.info("in side no folder ----"+ folderName);
					createFolderIfdoesntExist(boxFolder, folderName);
			
					boxFolder = new BoxFolder(api, folderId);
				
					folderNames = boxFolder.getChildren();
					
					folNames =	folderNames.iterator();
				}
				while (folNames.hasNext()) {
					Info folderInfo =	folNames.next();
						if(folderName.equalsIgnoreCase(folderInfo.getName()))
						{
							flagForFolder = true;
							finalFolderInfo = folderInfo;
							break;
						}
						flagForFolder = false;
				}
			
			if(flagForFolder == true)
			{
				logger.info("Inside asset folder exist---------"+ finalFolderInfo.getName());
				
			}else if(flagForFolder == false)
			{
				logger.info("Inside asset folder doesn't exit----");
				
				finalFolderInfo =  createFolderIfdoesntExist(boxFolder, folderName);
			}
			logger.info("total asset id info details------------"+ finalFolderInfo.getName());
			
			
		} catch (Exception e) {
			logger.info("inside exception block of if else-----");

			e.printStackTrace();
		}
		return finalFolderInfo;
	}
	
	/**
	 * @param sectionFolderInfo
	 * @param api
	 * @param resource
	 * @param fileName
	 * @param Id
	 */
	private void createResourceFoldersAndUploadtoBox(Info sectionFolderInfo, BoxDeveloperEditionAPIConnection api,String resource, String fileName, String Id)
	{

		logger.info("Enter into the createResourceFolders: sectionFolderInfo = "+sectionFolderInfo+"apiConnection = "+api.getUserAgent()+"resource= "+resource+"fileName= "+fileName+"Id = "+Id);
		boolean flagForResourceFolder = false;
		Info finalResourceFolderInfo = null;
		
		try
		{
		BoxFolder deviceIdFolder = new BoxFolder(api, sectionFolderInfo.getID());
		
		Iterable<Info> resourceFolderInfoIt = deviceIdFolder.getChildren();
		Iterator<Info> resourceFolderIt =	resourceFolderInfoIt.iterator();
		
			if(!resourceFolderIt.hasNext()){
			    createFolderIfdoesntExist(deviceIdFolder, resource);
				logger.info("inside new assetid folder create---------");
				deviceIdFolder = new BoxFolder(api, sectionFolderInfo.getID());
				resourceFolderInfoIt = deviceIdFolder.getChildren();				
				resourceFolderIt =	resourceFolderInfoIt.iterator();
			}
		
			while(resourceFolderIt.hasNext())
			{
				Info resFolderInfo = resourceFolderIt.next();
				logger.info("inside while  of resource folder--------------"+ resFolderInfo.getName());

				if(resource.equalsIgnoreCase(resFolderInfo.getName()))
				{
					flagForResourceFolder = true;
					finalResourceFolderInfo = resFolderInfo;
					logger.info("breaking while loop------------");
					break;
				}
					flagForResourceFolder = false;
			}
			
			if(flagForResourceFolder== true)
			{
				logger.info("Inside resource folder exist---------");

				
			}else if(flagForResourceFolder == false)
			{
				logger.info("Inside resource folder doesn't exit---------");

				finalResourceFolderInfo =  createFolderIfdoesntExist(deviceIdFolder, resource);
			}
			logger.info("final resource folder info is ----------"+finalResourceFolderInfo.getName());

		BoxFolder resourceFolder = new BoxFolder(api, finalResourceFolderInfo.getID());
		
		InputStream fileInputStream = new FileInputStream("C:\\edlorefolder\\"+fileName);
		
		
		BoxFile.Info uplodedFolderInfo = resourceFolder.uploadFile(fileInputStream, fileName);
		
		BoxFile boxFile  = uplodedFolderInfo.getResource();
		
		// To add additional properties to file like asset id as property
		Metadata metadata = new Metadata();
		metadata.add("/SectionName", sectionFolderInfo.getName());
		metadata.add("/ID", Id);
		boxFile.createMetadata(metadata);
		logger.info("meta data successfully created------------");
		
		}catch (Exception e) {
			logger.info("inside exception of create resource folder ");

			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	} 
	
	// Returns the information Box
	/**
	 * @param rootFolder
	 * @param folderName
	 * @return
	 */
	private Info createFolderIfdoesntExist(BoxFolder rootFolder, String folderName)
	{

		Info folderInfo = null;
		folderInfo = rootFolder.createFolder(folderName);
		logger.info("assetid folder created--------"+ folderName);

		return folderInfo;
	}
	
	/**
	 * @param jsonArray
	 * @return
	 */
	private List<UploadManual> parseTheJson(JSONArray jsonArray)
	{

		logger.info("Enter the parseTheJson method");
		JSONObject jsonObject = null;
		List<UploadManual> listOfUploadManuals = null;
		UploadManual uploadManual = null;
		
		listOfUploadManuals = new ArrayList<UploadManual>();
		
			// iterate the json array
			for(int i=0; i < jsonArray.size(); i++)
			{
				uploadManual = new UploadManual();
				jsonObject =  (JSONObject)jsonArray.get(i);
				
				// set the all values to upload manuals
				uploadManual.setId(String.valueOf(jsonObject.get("id")));
				uploadManual.setUpload_file_name(String.valueOf(jsonObject.get("upload_file_name")));
				uploadManual.setResource_type(String.valueOf(jsonObject.get("resource_type")));
				uploadManual.setManual_url(String.valueOf(jsonObject.get("manual_url")));
				// add the upload manual to list
				listOfUploadManuals.add(uploadManual);
			}
			
			logger.info("before return the listOfUploadManals");
		return listOfUploadManuals;
	}
	
	/**
	 * @param appender
	 * @param fileName
	 * @param inputStream
	 * @return
	 */
	private String writeIntoFileSystem(String appender, String fileName, InputStream inputStream)
	{

		logger.info("Enter into writeIntoFileSystem method:appender= "+appender +"fileName= "+fileName);
		FileOutputStream fileOutputStream = null;
		String filePath = FileSystemConfig.FILE_PATH;
		
		
		String modifiedFileName = appender+"_"+String.valueOf(Calendar.getInstance().getTimeInMillis())+"_"+fileName;
		logger.info("modifiedFileName");
		File file = new File(filePath+modifiedFileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			fileOutputStream = new FileOutputStream(file);
			 byte[] buffer = new byte[1024];
	           int bytesRead;
	           //read from is to buffer
	           while((bytesRead = inputStream.read(buffer)) !=-1){
	               fileOutputStream.write(buffer, 0, bytesRead);
	           }
	           inputStream.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally
		{
			try {
				fileOutputStream.close();
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		return modifiedFileName;
	}
	
	
	/**
	 * @param folderId
	 * @param fileName
	 * @return
	 */
	@POST
	@Path("/manual")
	public Response uploadToManual(@QueryParam("folderId")String folderId, @QueryParam("fileName") String fileName)
	{
		logger.info("enter into upload manual---------"+ fileName + " folder id is "+ folderId);
		String rootFolderId = folderId;
		BoxDeveloperEditionAPIConnection api = null;
		api = CDSBoxAPIConnection.getAppUserConnection();
		Info itemInfo = null;
		
		BoxFolder sectionFoler = new BoxFolder(api, folderId);
		createResourceFoldersAndUploadtoBox(sectionFoler.getInfo(), api, "Manual", fileName, sectionFoler.getInfo().getName());
		
		return Response.status(200).entity("success").build();
		
	}
	
	/**
	 * @param folderId
	 * @param fileName
	 * @return
	 */
	@POST
	@Path("/drawing")
	public Response uploadToDrawing(@QueryParam("folderId")String folderId, @QueryParam("fileName") String fileName)
	{
		logger.info("enter into upload manual---------"+ fileName + " folder id is "+ folderId);
		String rootFolderId = folderId;
		BoxDeveloperEditionAPIConnection api = null;
		api = CDSBoxAPIConnection.getAppUserConnection();
		Info itemInfo = null;
		
		BoxFolder sectionFoler = new BoxFolder(api, folderId);
		createResourceFoldersAndUploadtoBox(sectionFoler.getInfo(), api, "Drawing", fileName, sectionFoler.getInfo().getName());
		
		return Response.status(200).entity("success").build();
		
	}
}
