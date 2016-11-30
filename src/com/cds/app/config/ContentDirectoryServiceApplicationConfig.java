package com.cds.app.config;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.cds.accopen.services.AccountOpeningRefNo;
import com.cds.accopen.services.AccountOpeningSearchService;
import com.cds.accopen.services.BoxWebHook;
import com.cds.edlore.services.DownloadService;
import com.cds.edlore.services.GetManualUrl;
import com.cds.edlore.services.PingService;
import com.cds.edlore.services.UploadService;
import com.cds.watson.services.WatsonExpSearchService;



/**
 * @author Kiru V
 *
 * This configuration class will used by runtime rest servlet as long as project is
 * deployed the runtime rest servlet will read this class make available all resources
 * which are configured in this class otherwise rest servlet doesn't know about the 
 * resource classes and will not make it accessble
 * 
 */
public class ContentDirectoryServiceApplicationConfig extends Application{

	 /** The default life cycle for resource class instances is per-request. 
	   *  The default life cycle for providers is singleton.
	   * */
	/* (non-Javadoc)
	 * @see javax.ws.rs.core.Application#getClasses()
	 */
	private Set<Object> singletons;
	
	
	// Default constructor 
	public ContentDirectoryServiceApplicationConfig() {
		
		singletons = new HashSet<Object>();
		singletons.add(new UploadService());
		singletons.add(new PingService());
		singletons.add(new DownloadService());
		singletons.add(new GetManualUrl());
		singletons.add(new BoxWebHook());
		singletons.add(new AccountOpeningRefNo());
		singletons.add(new AccountOpeningSearchService());
		singletons.add(new WatsonExpSearchService());
	}


	/** Fields and properties of returned instances are injected with their declared
	  * dependencies (see Context) by the runtime prior to use.
	  * */ 
	/* (non-Javadoc)
	 * @see javax.ws.rs.core.Application#getSingletons()
	 */
	
	/*This make sure the the object of resources are one object for application*/
	@Override
	public Set<Object> getSingletons() {
		
		return singletons;
	}
	
}
