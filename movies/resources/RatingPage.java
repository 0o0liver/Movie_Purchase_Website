package edu.uci.ics.binghal.service.movies.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.binghal.service.movies.core.Rating;
import edu.uci.ics.binghal.service.movies.core.ResultCodeToStatus;
import edu.uci.ics.binghal.service.movies.core.Star;
import edu.uci.ics.binghal.service.movies.core.VerifyPrivilege;
import edu.uci.ics.binghal.service.movies.logger.ServiceLogger;
import edu.uci.ics.binghal.service.movies.models.*;

@Path("rating")
public class RatingPage {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response rating(@Context HttpHeaders headers, String jsonText){
        ServiceLogger.LOGGER.info("Received a rating request.");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");
        RatingRequestModel requestModel;
        VerifyPrivilegeResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        if (!VerifyPrivilege.isUserAllowedToMakeRequest(email, 3)){
            ServiceLogger.LOGGER.warning("User has insufficient privilege.");
            responseModel = new VerifyPrivilegeResponseModel(141, "User has insufficient privilege.");
            return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).entity(responseModel).build();
        }
        try{
            requestModel = mapper.readValue(jsonText, RatingRequestModel.class);
            responseModel = Rating.rate(requestModel);
            return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).entity(responseModel).build();
        } catch (IOException e){
            if (e instanceof JsonMappingException){
                ServiceLogger.LOGGER.warning("Json Mapping Exception.");
                responseModel = new VerifyPrivilegeResponseModel(-2, "JSON mapping exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException){
                ServiceLogger.LOGGER.warning("Json Parse Exception.");
                responseModel = new VerifyPrivilegeResponseModel(-3, "JSON parse exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).entity(responseModel).build();
            }
            else {
                ServiceLogger.LOGGER.warning("Internal server error.");
                return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            }
        }



    }
}
