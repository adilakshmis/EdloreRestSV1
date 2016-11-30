package com.cds.box.util;

import java.util.ResourceBundle;

import javax.security.auth.Subject;

import org.apache.log4j.Logger;

import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.util.UserContext;

/**
 * @author Kiru V
 *
 */
public class CDSFilenetCEConnection {

	private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("com/cds/props/applicationProperties");
	
	private static String CE_URL = null;
	private static String USER_NAME = null;
	private static String PASSWORD = null;
	private static String STANZA = null;
	private static CDSFilenetCEConnection cdsFilenetCEConnection= null;
	private static Connection connection = null;

	private static boolean isConnection;
	private static Domain domain = null;
	private static String osName = null;
	private static ObjectStore objectStore = null;
	
	static final Logger logger = Logger.getLogger(CDSFilenetCEConnection.class.getName());
	 static {
	    	try
	    	{
	    		
	    	logger.info("CE uri "+CE_URL+" user name  "+USER_NAME+" password "+PASSWORD+" stanza "+STANZA);
	    	cdsFilenetCEConnection = new CDSFilenetCEConnection();	    	
	    	CE_URL = resourceBundle.getString("URI");
		    USER_NAME = resourceBundle.getString("USERNAME");
		    PASSWORD = resourceBundle.getString("PASSWORD");
		    STANZA = resourceBundle.getString("STANZA");
		    osName = resourceBundle.getString("OS");
		   
		    
		    
		    logger.info("CE uri "+CE_URL+" user name  "+USER_NAME+" password "+PASSWORD+" stanza "+STANZA);
	    
	    	}catch (Exception e) {
				e.printStackTrace();
			}
	    }
	 
	 /**
	 * @return
	 */
	public static synchronized ObjectStore getObjectStore()	 
     {	 
		 logger.info("enter into get connection -- ");
		 	
		
			 logger.info("enter into if connection null get object store -- ");
			
				 Subject subject = null;
				 UserContext userContext = null;
				 logger.info("enter into get connection -- ");
				 connection = Factory.Connection.getConnection(CE_URL);
				 userContext = UserContext.get();
				 subject = UserContext.createSubject(connection, USER_NAME, PASSWORD, STANZA);
				 userContext.pushSubject(subject);
			
				 domain = Factory.Domain.fetchInstance(connection, null, null);
				 objectStore = Factory.ObjectStore.fetchInstance(domain, osName, null);
				 
		 			
		 return objectStore;
     	}
	
}
