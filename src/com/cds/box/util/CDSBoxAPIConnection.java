package com.cds.box.util;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.box.sdk.BoxAPIException;
import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.box.sdk.EncryptionAlgorithm;
import com.box.sdk.IAccessTokenCache;
import com.box.sdk.InMemoryLRUAccessTokenCache;
import com.box.sdk.JWTEncryptionPreferences;



/**
 * @author Kiru V
 *
 */
/**
 * @author Mits
 *
 */
public class CDSBoxAPIConnection {

	// Declaring the attributes 	
		private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("com/cds/props/applicationProperties");
	    private static final int MAX_DEPTH = 1;
	    private static final int MAX_CACHE_ENTRIES = 100;
	    private static CDSBoxAPIConnection cdsBoxAPIConnection = null;
	    private static String privateKey = null;
	    private static JWTEncryptionPreferences encryptionPref = null;
	    private static IAccessTokenCache accessTokenCache = null;
	    private static BoxDeveloperEditionAPIConnection api = null;
	    
	   static final Logger logger = Logger.getLogger(CDSBoxAPIConnection.class.getName());
	    
	   
	    private CDSBoxAPIConnection(){
	    	// Default constructor
	    }
	    
	    static {
	    	try
	    	{
	    	logger.info("with in static block-------------------");
	    	cdsBoxAPIConnection = new CDSBoxAPIConnection();
	    	privateKey = new String(Files.readAllBytes(Paths.get(resourceBundle.getString("PRIVATE_KEY_FILE"))));
	    	encryptionPref = new JWTEncryptionPreferences();
	    	encryptionPref.setPublicKeyID(resourceBundle.getString("PUBLIC_KEY_ID"));
		    encryptionPref.setPrivateKey(privateKey);
		    encryptionPref.setPrivateKeyPassword(resourceBundle.getString("PRIVATE_KEY_PASSWORD"));
		    encryptionPref.setEncryptionAlgorithm(EncryptionAlgorithm.RSA_SHA_256);
		        
		    logger.info("Private Key :::"+privateKey);
		    
		    accessTokenCache = new InMemoryLRUAccessTokenCache(MAX_CACHE_ENTRIES);
		    logger.info(":::resourceBundle.getString(USER_ID)::"+resourceBundle.getString("USER_ID"));
		    logger.info(":::resourceBundle.getString(CLIENT_ID)::"+resourceBundle.getString("CLIENT_ID"));
		    logger.info(":::resourceBundle.getString(CLIENT_SECRET)::"+resourceBundle.getString("CLIENT_SECRET"));
	        logger.info("End of static block -------------");
	        
	    	}catch (Exception e) {
				e.printStackTrace();
				
			}
	    }
	    
	        //It is a best practice to use an access token cache to prevent unneeded requests to Box for access tokens.
	        //For production applications it is recommended to use a distributed cache like Memcached or Redis, and to
	        //implement IAccessTokenCache to store and retrieve access tokens appropriately for your environment.
	        
	       /**
	     * @return
	     */
	    public static  BoxDeveloperEditionAPIConnection getAppUserConnection()
	       {
	    	   
	    	   logger.info("with in get app user connection for box-----------"+ api);
	    	   try {
	    		   
	    	   if(api == null)
	    	   {
	    		   synchronized(CDSBoxAPIConnection.class)
	    		   {
	    			   if(api == null)
		    			   {
		    		   logger.info("with in if condition----------- "+api);
		    		   logger.info("user id -------- "+resourceBundle.getString("USER_ID"));
		    		   logger.info("client id ------------------------------ "+ resourceBundle.getString("CLIENT_ID"));
		    		   logger.info("client secret -------------------- "+resourceBundle.getString("CLIENT_SECRET"));
		    		   logger.info("encryptionPref -------------- "+encryptionPref);
		    		   logger.info("accessTokenCache ---------------- "+accessTokenCache);
		    		   
		    		  api = BoxDeveloperEditionAPIConnection.getAppUserConnection(resourceBundle.getString("USER_ID"), resourceBundle.getString("CLIENT_ID"),
		    				   resourceBundle.getString("CLIENT_SECRET"), encryptionPref, accessTokenCache);
		    		   
		    		  /*api = BoxDeveloperEditionAPIConnection.getAppEnterpriseConnection(resourceBundle.getString("ENTERPRISE_ID"), resourceBundle.getString("CLIENT_ID"),
		    				   resourceBundle.getString("CLIENT_SECRET"), encryptionPref, accessTokenCache);
		    		   */
		    		   
		    		   logger.info("expiration time ---------"+  api.getExpires());
		    		  logger.info("expiration time ---------"+  api.getExpires());
		    		  Date date2 = new Date(); 
		    		  Long time2 = (long) (((((date2.getHours() * 60) + date2.getMinutes())* 60 ) + date2.getSeconds()) * 1000);
		    		  logger.info("system time is ------"+time2);
		    		  
		    		  logger.info("calender date ----------- "+Calendar.getInstance().get(Calendar.MILLISECOND));
		    		  logger.info("system date ----------- "+System.currentTimeMillis());
		    		  
	    			   }
	    		   }
	    		   logger.info("end of if condition -----------"+api);
	    		   
	    		   return api;
	    	   }
	    	   
	    	  /* if(api.canRefresh())
	    	   {
	    		   logger.info("with in can refresh block -----------"+api);	
	    		   System.out.println("expiration time in can refresh---------"+  api.getExpires());
	    		   logger.info("expiration time in can refresh---------"+  api.getExpires());
	    		   Date date2 = new Date(); 
		    		  Long time2 = (long) (((((date2.getHours() * 60) + date2.getMinutes())* 60 ) + date2.getSeconds()) * 1000);
		    		  System.out.println("system time is ------"+time2);
		    		  
		    		  System.out.println("calender date ----------- "+Calendar.getInstance().get(Calendar.MILLISECOND));
		    		  System.out.println("system date ----------- "+System.currentTimeMillis());
		    		  
	    		  return api;
	    	   }*/
	    	   
	    	   System.out.println("box api connection is success ------");
	        
	         
	    	   }catch (Exception e) {
	    		   
	    		   logger.info("with in exception block of get box connection----------"+e);
	    		   logger.info("with in exception block of get box connection is box exception ----------"+(e instanceof BoxAPIException));
	    		   
	    		   if(e instanceof BoxAPIException)
	    		   {
	    			   BoxAPIException apiException = (BoxAPIException)e;
	    			   
	    			   logger.info("with in exception block of get box connection response----------"+apiException.getResponse()); 
	    			   logger.info("with in exception block of get box connection locationzation message ----------"+apiException.getLocalizedMessage());
	    			   logger.info("with in exception block of get box connection message ----------"+apiException.getMessage());
	    			   logger.info("with in exception block of get box connection response code ----------"+apiException.getResponseCode());
	    			   logger.info("with in exception block of get box connection ----------"+apiException.toString());
	    			   logger.info("with in exception block of get box connection----------"+apiException.getCause());
	    		   }
	    		 
	    		  logger.info(e.getCause(), e);
	    		  
	    		  /*api = BoxDeveloperEditionAPIConnection.getAppUserConnection(resourceBundle.getString("USER_ID"), resourceBundle.getString("CLIENT_ID"),
	    				   resourceBundle.getString("CLIENT_SECRET"), encryptionPref, accessTokenCache);*/
	    		   
	    		  return api;
	    		
			}
	    	   
	    	   logger.info("expiration time of box connection---------"+  api.getExpires());
	    		  
			return api;
	       }
	  
	      
	       
}
