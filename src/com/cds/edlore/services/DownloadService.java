package com.cds.edlore.services;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.box.sdk.BoxAPIException;
import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.box.sdk.BoxFile;
import com.cds.box.util.CDSBoxAPIConnection;

/*import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;*/

/**
 * @author Kiru V
 * 
 */
@Path(value = "/download")
public class DownloadService {
	/**
	 * @param manualId
	 * @return response
	 * 
	 *         By taking input as manualId it connects to box and then returns
	 *         the manual based on manual Id
	 * 
	 *         this service used to download the document based on given id and
	 *         requested mime type
	 */
	final Logger logger = Logger.getLogger(DownloadService.class);

	/**
	 * @param manualId
	 * @return
	 */
	@GET
	@Path("{manualId}")
	@Produces({ "application/pdf", "image/png", "image/tif", "image/jpeg",
			"video/mp4", "image/tiff" })
	public Response getFile(@PathParam("manualId") String manualId) {

		// declare all the parameters
		BoxFile file = null;
		BoxDeveloperEditionAPIConnection api = null;
		BoxFile.Info info = null;
		OutputStream outputStream = null;
		FileInputStream fis = null;

		logger.info("Enter get file method manualId is ::" + manualId);

		// check input manualId is null and empty string
		if (manualId == null || "".equals(manualId)) {
			return Response.status(404)
					.entity("requested manual id unavilable").build();
		}

		try {
			// If box return no connection return this response
			api = CDSBoxAPIConnection.getAppUserConnection();
			if (api == null) {
				return Response.status(400).entity("Unable to connect to box ")
						.build();
			}
			
			//create the box file object for given manualId
			file = new BoxFile(api, manualId);
			
			//get the file information
			info = file.getInfo();
			
			String filePath = info.getName();
			logger.info("file path or file name is :" + filePath);

			outputStream = new FileOutputStream(filePath);
			fis = new FileInputStream(filePath);
			logger.info("before downloading");

			file.download(outputStream);
			logger.info("download succesfully");

		} catch (Exception e) {
			
			// log the box api exception details clearly
			if (e instanceof BoxAPIException) {
				logger.info("In download service box api exception -------" + e);

				BoxAPIException exception = (BoxAPIException) e;

				logger.info("Box stack trace is---------" + exception);
				logger.info("with in exception block of get box connection response----------"
						+ exception.getResponse());
				logger.info("with in exception block of get box connection locationzation message ----------"
						+ exception.getLocalizedMessage());
				logger.info("with in exception block of get box connection message ----------"
						+ exception.getMessage());
				logger.info("with in exception block of get box connection response code ----------"
						+ exception.getResponseCode());
				logger.info("with in exception block of get box connection ----------"
						+ exception.toString());
				logger.info("with in exception block of get box connection----------"
						+ exception.getCause());

				return Response.status(exception.getResponseCode())
						.entity(exception.getMessage()).build();
			}
			logger.info("In download service exception is -------" + e);

			e.printStackTrace();

		} finally {

		}

		/**
		 * Return the reponse with status 200 and set the reponse header as
		 * content-disposition as inline so that browser client can able to
		 * disply the document or loads the document on browser
		 * 
		 * If content-disposition is attachment then it forces the browser to
		 * save document in local system or it gets dowloaded
		 * */

		return Response
				.ok(fis)
				.header("Content-Disposition",
						"inline; filename=" + info.getName()).build();
	}
}
