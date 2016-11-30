package com.cds.accopen.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

/**
 * @author Kiru V
 *
 */
@Path(value="/track")
public class AccountOpeningStatusTrackingService {

	final Logger logger = Logger.getLogger(AccountOpeningStatusTrackingService.class);
	
	
	/**
	 * @param refNo
	 * @return
	 */
	@GET
	@Path("/status")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCaseStatus(@QueryParam("refNo")String refNo)
	{
		
		if(("".equals(refNo) || refNo == null)){
			return Response.status(400).entity("Please provide valid values ").build();
		
		}
		
		
		
		
		
		
		 return Response.status(200).entity("").build();
	}
}
