package com.cds.edlore.amazon.util;

import java.io.InputStream;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cds.edlore.util.UploadManualSatus;

/**
 * @author Kiru V
 *
 */
public class AmazonHttpClient {

	final Logger logger = LoggerFactory.getLogger(AmazonHttpClient.class);

	/**
	 * @param url
	 * @param list
	 * @param status
	 * @return
	 */
	public InputStream getFileFromAmazonws(String url,
		   List<UploadManualSatus> list, UploadManualSatus status) {

		logger.info("Enter into the getFileFromAmazonws:url is = " + url
				+ " list =" + list + " UploadManualSatus =" + status);

		DefaultHttpClient httpClient = null;
		HttpGet getRequest = null;
		InputStream inputStream = null;
		HttpResponse response = null;
		// Declare the local variables
		httpClient = new DefaultHttpClient();
		getRequest = new HttpGet(url);

		// Set the API media type in http accept header
		getRequest.addHeader("accept", "application/pdf");

		// Send the request; It will immediately return the response in
		// HttpResponse object
		try {
			logger.info("Before executing amazon request");
			response = httpClient.execute(getRequest);
			logger.info("After executing amazon request");
			if (response.getStatusLine().getStatusCode() != 200) {

				logger.info("Enter into if condition");
				status.setMessage("Amazon server unavilable");
				status.setStatus(String.valueOf(response.getStatusLine().getStatusCode()));
				list.add(status);

			} else {
				// Now pull back the response object
				HttpEntity httpEntity = response.getEntity();

				Header[] headers = response.getAllHeaders();
				System.out.println(headers);
				// get content from the http entity
				inputStream = httpEntity.getContent();

				logger.info("inputStream = " + inputStream);
			}
		} catch (Exception e) {
			status.setStatus("202");
			status.setMessage("problem in amazone service");
			list.add(status);
			logger.info("Amazon exception class is -------"
					+ e.getClass());
			logger.info("Amazon exception  is --------- " + e);

			e.printStackTrace();
		} finally {

		}
		return inputStream;
	}
}
