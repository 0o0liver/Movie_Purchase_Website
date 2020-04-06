package edu.uci.ics.binghal.service.movies.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.binghal.service.movies.logger.ServiceLogger;
import edu.uci.ics.binghal.service.movies.core.CheckEmailFormat;
import edu.uci.ics.binghal.service.movies.core.ResultCodeToStatus;
import edu.uci.ics.binghal.service.movies.core.VerifyPrivilege;
import edu.uci.ics.binghal.service.movies.core.Remove;
import edu.uci.ics.binghal.service.movies.models.VerifyPrivilegeResponseModel;

@Path("delete")
public class RemovePage{
    @Path("{movieid}")
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public static Response RemovePage(@Context HttpHeaders headers, @PathParam("movieid") String movieid){
        ServiceLogger.LOGGER.info("Received remove movie request.");

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");
        VerifyPrivilegeResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        if (!VerifyPrivilege.isUserAllowedToMakeRequest(email, 3)){
            ServiceLogger.LOGGER.warning("User has insufficient privilege.");
            responseModel = new VerifyPrivilegeResponseModel(141, "User has insufficient privilege.");
            return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).entity(responseModel).build();
        }
        try{
            responseModel = Remove.removeMovie(movieid);
            return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).entity(responseModel).build();
        } catch (Exception e){
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Internal server error.");
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}