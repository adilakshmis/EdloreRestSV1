package com.cds.accopen.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;

import javax.security.auth.Subject;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.DocumentSet;
import com.filenet.api.collection.FolderSet;
import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Connection;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenet.api.engine.EventActionHandler;
import com.filenet.api.events.ObjectChangeEvent;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.filenet.api.util.Id;
import com.filenet.api.util.UserContext;
import com.ibm.casemgmt.api.Case;
import com.ibm.casemgmt.api.CaseType;
import com.ibm.casemgmt.api.constants.ModificationIntent;
import com.ibm.casemgmt.api.context.CaseMgmtContext;
import com.ibm.casemgmt.api.context.P8ConnectionCache;
import com.ibm.casemgmt.api.context.SimpleP8ConnectionCache;
import com.ibm.casemgmt.api.context.SimpleVWSessionCache;
import com.ibm.casemgmt.api.objectref.ObjectStoreReference;
import com.ibm.casemgmt.api.properties.CaseMgmtProperties;
import com.ibm.json.java.JSONObject;

//com.mits.account.AccountOpeningSubScription
public class AccountOpeningSubScription implements EventActionHandler{
	
	public static String propeFilePath="C:/PropertyFile/ACOPEvent.properties";
	static Properties properties = null;
	ObjectStore ceObjectStore = null;
	CaseMgmtContext localCaseMgmtContext1 =null;
	
	static {
		properties=new Properties();
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(new File("C://PropertyFile/ACOPEvent.properties"));
			properties.load(fileInputStream);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//implement onEvent method
	@Override
	public void onEvent(ObjectChangeEvent event, Id arg1)throws EngineRuntimeException {
		Document document = (Document) event.get_SourceObject();
		ceObjectStore = document.getObjectStore();
		JSONObject jsonObject = getJsonData(ceObjectStore,document);
		System.out.println("DocVersion "+document.get_MajorVersionNumber());
		
	 /* get the version of Document. 
		if it is "0",that's means case not exist so create a case. 
	    if it is not equal "0",that's means case exist,update that case */
		if(0 == (document.get_MajorVersionNumber()))
		{
			 caseCreation(jsonObject,document);
		}else
		{
			caseUpdateEvent(jsonObject,document);
		}
	}
	
	 // get the content(json) from committed document
	@SuppressWarnings("static-access")
	public JSONObject getJsonData(ObjectStore objectStore , Document document){
            System.out.println("EventCreation.accountOpening()");		
            JSONObject jsonObject  = null;
            
            try{
            //fetch the document and get ContentElementList
			 Document jsonDocument = Factory.Document.fetchInstance(objectStore, document.get_Id(),null);
			 ContentElementList contentElementList =  jsonDocument.get_ContentElements();
			    System.out.println("contentElementList.size()"+contentElementList.size());
			    Iterator iterator = contentElementList.iterator();
			    
			    //get the content(json) as inputStream,and parse it in to jsonObject
			    while(iterator.hasNext()){
			    	ContentTransfer contentTransfer = (ContentTransfer)iterator.next();
			    	System.out.println("get_ContentType  :: "+contentTransfer.get_ContentType());
			    	InputStream inputstream=contentTransfer.accessContentStream();
			    	if(contentTransfer.get_ContentType().equals("application/json")||contentTransfer.get_ContentType().equals(".json")){
			    		System.out.println("contenttype is application/json");
			    		jsonObject = jsonObject.parse(inputstream);
			    	}else{
			    		System.out.println("contenttype is NOT application/json");
			    	}
			    }
			    System.out.println(jsonObject);
		     return jsonObject;
            }catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error in getJsonData ::"+e);
			}
			return null;
	}
   
	/* method define CaseCreation
	 * get the reference no from all existed cases, read json file and get the reference from json file.
	 * if both the reference numbers are not equal(which we can get from cases and json) then create case.
	 */
	public void caseCreation(JSONObject jsonObject,Document document){
		
		System.out.println("AccountOpeningSubScription.CaseCreation()");
	try{
		//get the reference no from jsonObject
		String refno  = (String) jsonObject.get("BoxID");
	    System.out.println("referenceNo  :::   "+refno);
		
	    //get all the existed cases(CaseFolders) from caseType
		SearchSQL searchSQL = new SearchSQL("SELECT * from AMT_ACOP");
		SearchScope searchScope = new SearchScope(ceObjectStore);
		System.out.println("After searchscope");
		searchScope.fetchObjects(searchSQL,50, null,Boolean.valueOf(true));
		FolderSet caseFolderSet = (FolderSet) searchScope.fetchObjects(searchSQL,50, null,Boolean.valueOf(true));
		Iterator <Document>iterator = caseFolderSet.iterator();
		System.out.println("After FolderSet");
		Folder	folder = null;
		boolean flag = false;
		
		//iterate caseFolderSet and get property(AMT_ReferenceNumber) 
		//compare that two reference numbers
		while (iterator.hasNext())
		{
			folder =(Folder) iterator.next();
			String caseref = folder.getProperties().getStringValue("AMT_ReferenceNumber");
			
			 if(refno.equalsIgnoreCase(caseref)){
		    	  System.out.println("in if");
					flag = false;
					break;
		      }else{
		    	  System.out.println("in else ");
					flag = true;
		      }
		 }
	    
		if(flag){
            System.out.println("createcase");
			
	    	P8ConnectionCache connectionCache = new SimpleP8ConnectionCache();
			Connection connection =  connectionCache.getP8Connection(properties.getProperty("uri"));
			Subject subject  = UserContext.createSubject(connection, properties.getProperty("uname"),properties.getProperty("pwd"),null);
			UserContext uc = UserContext.get();
		    uc.pushSubject(subject);
		    Locale origLocale = uc.getLocale();
		    uc.setLocale(origLocale);
			System.out.println("-------------------------------------");
			SimpleVWSessionCache localSimpleVWSessionCache = new SimpleVWSessionCache();
	        CaseMgmtContext localCaseMgmtContext2 = new CaseMgmtContext(localSimpleVWSessionCache, new SimpleP8ConnectionCache());
	        localCaseMgmtContext1 = CaseMgmtContext.set(localCaseMgmtContext2);
	        
	        System.out.println("========================================");
		    Domain _fnDomain = Factory.Domain.fetchInstance(connection, null, null);
		    System.out.println("Domain  :::"+_fnDomain.get_Name());
			ObjectStore objectStore = Factory.ObjectStore.fetchInstance(_fnDomain,properties.getProperty("objStore"), null);
	    	System.out.println("objectStore  ::::"+objectStore.get_Name());
	    	
	    	ObjectStoreReference objStoreRef = new ObjectStoreReference(objectStore);
	    	System.out.println("objStoreRef   ::::"+objStoreRef);
	    	//get the CaseType
	    	CaseType caseType = CaseType.fetchInstance(objStoreRef, "AMT_ACOP");
	    	System.out.println("caseType  ::::"+caseType.getName());
	    
	    	//create case
	    	Case newCase = Case.createPendingInstance(caseType); 
	    	CaseMgmtProperties properties = newCase.getProperties();
	    	System.out.println("-------"+properties);
	    	
	    	//set the case properties
	    	properties.putObjectValue("AMT_ReferenceNumber",refno);
	      	properties.putObjectValue("AMT_NRIC",(String) jsonObject.get("NRIC_NRIC"));
	    	properties.putObjectValue("AMT_NameasinNRIC",(String) jsonObject.get("NameAsNRIC"));
	      	properties.putObjectValue("AMT_CountryofBirth",(String) jsonObject.get("Countyof_Birth"));
	      	properties.putObjectValue("AMT_Gender",(String) jsonObject.get("Gennder"));
	      	properties.putObjectValue("AMT_DateofBirth",(String) jsonObject.get("DOB"));
	      	properties.putObjectValue("AMT_CitizenPRIndicator",(String) jsonObject.get("CitizenPRIndicator"));
	      	properties.putObjectValue("AMT_CountryofResidency",(String) jsonObject.get("CountryOf_Residency"));
	      	properties.putObjectValue("AMT_Nationality",(String) jsonObject.get("Nationallity"));
	      	properties.putObjectValue("AMT_MaritalStatus",(String) jsonObject.get("Marital_Status"));
	      	properties.putObjectValue("AMT_NumberofDependents",(String) jsonObject.get("NumberOf_Dependents"));
	      	properties.putObjectValue("AMT_HomeNumber",(String) jsonObject.get("Home_Number"));
	      	properties.putObjectValue("AMT_MobileNumber",(String) jsonObject.get("MobileNo"));
	      	properties.putObjectValue("AMT_OfficeNumber",(String) jsonObject.get("OfficeNO"));
	      	properties.putObjectValue("AMT_EmailAddress",(String) jsonObject.get("Email_Address"));
	      	properties.putObjectValue("AMT_Address",(String) jsonObject.get("Addresss"));
	      	properties.putObjectValue("AMT_FloorNo",(String) jsonObject.get("Flor_No"));
	      	properties.putObjectValue("AMT_UnitNo",(String) jsonObject.get("Unit_No"));
	      	properties.putObjectValue("AMT_StreetName",(String) jsonObject.get("Street_Name"));
	      	properties.putObjectValue("AMT_PostalCode",(String) jsonObject.get("Postal_Code"));
	      	properties.putObjectValue("AMT_Occupation",(String) jsonObject.get("Occupationn"));
	      	properties.putObjectValue("AMT_CompanyName",(String) jsonObject.get("Company_Name"));
	      	properties.putObjectValue("AMT_LengthofService",(String) jsonObject.get("LengthOfService"));
	      	properties.putObjectValue("AMT_MonthlyIncome",(String) jsonObject.get("Monthly_Income"));
	      	properties.putObjectValue("AMT_IDCountry","IC");
	      	properties.putObjectValue("AMT_IDType","IT");
	      	properties.putObjectValue("AMT_PropertyTriggerTask","PP");

	    	newCase.save(RefreshMode.REFRESH, null, ModificationIntent.MODIFY);
	    	System.out.println("after case creation");
	    	
	    	System.out.println("case name "+newCase.getCaseTitle());
	      	System.out.println("case ID"+newCase.getId());
	      	System.out.println("case name "+newCase.getFolderReference().toString());
	      	
	      	 //fetch the meta data document & get the contentElementList
			 Document metaDataDocument=Factory.Document.fetchInstance(ceObjectStore,document.get_Id(), null);
			 FolderSet folderSet = metaDataDocument.get_FoldersFiledIn();
			
			Iterator folderSetIterator = folderSet.iterator();
			while(folderSetIterator.hasNext()){
				System.out.println("inside folderSetIterator");
				Folder existFolder = (Folder)folderSetIterator.next();
				System.out.println(existFolder.get_Name());
			    DocumentSet documentSet = existFolder.get_ContainedDocuments();
			
			      Iterator documentSetIterator = documentSet.iterator();
			      while(documentSetIterator.hasNext()){
			    	  System.out.println("inside documentSetIterator ");
				        Document documentInFolder = (Document)documentSetIterator.next();
				        System.out.println(documentInFolder.get_Name());
				        
				      //fetch the document and get ContentElementList
						 ContentElementList contentElementList =  documentInFolder.get_ContentElements();
						    System.out.println("contentElementList.size()"+contentElementList.size());
						    Iterator contentElementListIterator = contentElementList.iterator();
						    
						    //get the content(json) as inputStream,and parse it in to jsonObject
						    while(contentElementListIterator.hasNext()){
						    	System.out.println("inside contentElementListIterator");
						    	ContentTransfer contentTransfer = (ContentTransfer)contentElementListIterator.next();
						    	System.out.println("get_ContentType  :: "+contentTransfer.get_ContentType());
						    	InputStream inputstream=contentTransfer.accessContentStream();
						    	if(contentTransfer.get_ContentType().equals("application/json")||contentTransfer.get_ContentType().equals(".json")){
						    		System.out.println("contenttype is application/json");
						    	}else{
						    		System.out.println("contenttype is NOT application/json");
						    	    Folder caseFolder = Factory.Folder.fetchInstance(ceObjectStore,newCase.getId(),null);
								  //filing a document into folder by using ReferentialContainmentRelationship(rcr) and save the rcr
									ReferentialContainmentRelationship rcr = caseFolder.file(documentInFolder, AutoUniqueName.AUTO_UNIQUE,documentInFolder.get_Name(),DefineSecurityParentage.DEFINE_SECURITY_PARENTAGE);
								    rcr.save(RefreshMode.REFRESH);
									System.out.println("After filing document in folder");
						    		
						    	}
						    }
			        }
			   }
	      	
		}else{
			System.out.println("case exist");
		}
	      	
	    }catch (Exception e) {
	    	e.printStackTrace();
	    	System.out.println("Error is :::::::: "+e.getMessage());
		}	    
	}
	/* method define Updating case
	 * read json file and get the reference from json file. get the reference no from all existed cases, 
	 * if both the reference numbers are equal,get the folder(caseFolder) name and Fetch the folder(caseFolder)
	 *  * if both the reference numbers are equal,get the parent folder of that case and get all the documents from that folder 
	 * get documents from that folder 
	 */
	public void caseUpdateEvent(JSONObject jsonObject,Document document){
		System.out.println("AccountOpeningSubScription.caseUpdateEvent()");
		//get the referenceNo from json file
		String refno  = (String) jsonObject.get("BoxID");
	    System.out.println("referenceNo  :::   "+refno);
	    try{
	    	System.out.println("in try");
	       //get all the existed cases(CaseFolders) from caseType
	    	SearchSQL searchSQL = new SearchSQL("SELECT * from "+properties.getProperty("casetype"));
			SearchScope searchScope = new SearchScope(ceObjectStore);
			System.out.println("After searchscope");
			searchScope.fetchObjects(searchSQL,50, null,Boolean.valueOf(true));
			FolderSet caseFolderset = (FolderSet) searchScope.fetchObjects(searchSQL,50, null,Boolean.valueOf(true));
			Iterator caseFoldersetiterator = caseFolderset.iterator();
			System.out.println("After FolderSet");
			Folder	folder = null;
			String caseFolderName = null;
			Id caseFolderID = null;
			
			//iterate caseFolderSet and get property(AMT_ReferenceNumber) 
			//compare that two reference numbers
			while (caseFoldersetiterator.hasNext())
			{
				System.out.println("inside caseFolderSet ");
				folder =(Folder) caseFoldersetiterator.next();
				String caseref = folder.getProperties().getStringValue("AMT_ReferenceNumber");
			      
				 if(refno.equalsIgnoreCase(caseref)){
			    	  System.out.println("in if");
			    	  caseFolderName = folder.get_Name();
			    	  caseFolderID = folder.get_Id();
						break;
			      }else{
			    	  System.out.println("in else ");
			      }
			 }
			System.out.println("  caseFolderName   ::::: "+caseFolderName);
			System.out.println(" before document filing in Casefolder");
			if(!caseFolderName.equals("")&&!caseFolderName.equals(null)){
				
				System.out.println("inside document filing in Casefolder");
				//fetch the CaseFolder and get documents
				Folder caseFolder = Factory.Folder.fetchInstance(ceObjectStore,caseFolderID,null);
				 DocumentSet documentSet =	caseFolder.get_ContainedDocuments();
				    Iterator documentSetIterator = documentSet.iterator();
				     while(documentSetIterator.hasNext()){
				    	 
				       System.out.println("inside documentSetIterator ");
					   Document documentInFolder = (Document)documentSetIterator.next();
					   System.out.println("docInCaseFolder  ::::: "+documentInFolder.get_Name());
					   String docInCaseFolder = documentInFolder.get_Name();
					   
					    System.out.println("before ce Document get");
					   //fetch the meta data document & get the contentElementList
						 Document existDocument=Factory.Document.fetchInstance(ceObjectStore,document.get_Id(), null);
						 FolderSet folderSet = existDocument.get_FoldersFiledIn();
						
						Iterator folderSetIterator = folderSet.iterator();
						while(folderSetIterator.hasNext()){
							
							System.out.println("inside folderSetIterator");
							Folder existFolder = (Folder)folderSetIterator.next();
							System.out.println(existFolder.get_Name());
						    DocumentSet ceDocumentSet = existFolder.get_ContainedDocuments();
						
						      Iterator ceDocumentSetIterator = ceDocumentSet.iterator();
						      while(ceDocumentSetIterator.hasNext()){
						    	  System.out.println("inside documentSetIterator ");
							        Document ceDocumentInFolder = (Document)ceDocumentSetIterator.next();
							        System.out.println("docInCEfolder  ::::  "+ceDocumentInFolder.get_Name());
							        String docInCEfolder = ceDocumentInFolder.get_Name();
							        
							        if(docInCEfolder.equalsIgnoreCase(docInCaseFolder)){
							        	System.out.println("Document names are eual");
							        	
							        }else{
							        	System.out.println("Document names are not equal");
							        	
							        	//fetch the document and get ContentElementList
										 ContentElementList contentElementList =  ceDocumentInFolder.get_ContentElements();
										 System.out.println("contentElementList.size()"+contentElementList.size());
										 Iterator contentElementListIterator = contentElementList.iterator();
										 
										  //get the content(json) as inputStream,and parse it in to jsonObject
										    while(contentElementListIterator.hasNext()){
										    	
										    	System.out.println("inside contentElementListIterator");
										    	ContentTransfer contentTransfer = (ContentTransfer)contentElementListIterator.next();
										    	System.out.println("get_ContentType  :: "+contentTransfer.get_ContentType());
										    	
										    	if(contentTransfer.get_ContentType().equals("application/json")||contentTransfer.get_ContentType().equals(".json")){
										    		System.out.println("contenttype is application/json");
										    	}else{
										    		//filing a document into folder by using ReferentialContainmentRelationship(rcr) and save the rcr
													ReferentialContainmentRelationship rcr = caseFolder.file(ceDocumentInFolder, AutoUniqueName.AUTO_UNIQUE,docInCEfolder,DefineSecurityParentage.DEFINE_SECURITY_PARENTAGE);
												    rcr.save(RefreshMode.REFRESH);
													System.out.println("After filing document in folder");
										    	}
										    }
							         }
						      }
						}
				 }
			  }
	        }catch (Exception e) {
             e.printStackTrace();
             System.out.println("Error in caseUpdateEvent  ::::  "+e);
       }
	}
}
