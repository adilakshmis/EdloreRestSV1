package com.cds.watson.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Kiru V
 *
 *   
 */
public class ContentTypeMap {
	
	// 
	public static Map<String, String> values = new ConcurrentHashMap<String, String>();
	
	static {	
		
		values.put("jpg", "image/jpg");
		values.put("pdf", "application/pdf");
		values.put("pdf", "application/json");
		values.put("png", "image/png");
		values.put("png", "image/tif");
		values.put("png", "image/tiff");
		values.put("png", "image/jpeg");
		
	}
	
	/**
	 * @param extension
	 * @return
	 * 
	 *  ContentTypeMap will help to resolve the mimetype for given extension
	 *  in case of box we no need to set the mime type it takes base on file extension
	 *  in case of filenet we need to set the mime type for document
	 */
	public static String getMimeType(String extension)
	{		
		return values.get(extension);
	}
}
