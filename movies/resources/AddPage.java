package edu.uci.ics.binghal.service.movies.resources;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.binghal.service.movies.logger.ServiceLogger;
import edu.uci.ics.binghal.service.movies.core.CheckEmailFormat;
import edu.uci.ics.binghal.service.movies.core.ResultCodeToStatus;
import edu.uci.ics.binghal.service.movies.core.VerifyPrivilege;
import edu.uci.ics.binghal.service.movies.models.AddPageResponseModel;
import edu.uci.ics.binghal.service.movies.models.AddPageRequestModel;
import edu.uci.ics.binghal.service.movies.core.Add;

@Path("add")
public class AddPage{
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public static Response AddPage(@Context HttpHeaders headers, String jsonText){

        ServiceLogger.LOGGER.info("Received add movie request.");

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");
        AddPageRequestModel requestModel;
        AddPageResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        if (!VerifyPrivilege.isUserAllowedToMakeRequest(email, 3)){
            ServiceLogger.LOGGER.warning("User has insufficient privilege.");
            responseModel = new AddPageResponseModel(141, "User has insufficient privilege.");
            return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).entity(responseModel).build();
        }
        try {
            ServiceLogger.LOGGER.info("Mapping jsonText to request model.");
            requestModel = mapper.readValue(jsonText, AddPageRequestModel.class);
            ServiceLogger.LOGGER.info("Map successfully.");
            responseModel = Add.addMovie(requestModel);
            return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).entity(responseModel).build();
        } catch (IOException e){
            if (e instanceof JsonMappingException){
                e.printStackTrace();
                ServiceLogger.LOGGER.warning("Json Mapping Exception.");
                responseModel = new AddPageResponseModel(-2, "JSON mapping exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException){
                e.printStackTrace();
                ServiceLogger.LOGGER.warning("Json Parse Exception.");
                responseModel = new AddPageResponseModel(-3, "JSON parse exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).entity(responseModel).build();
            }
            else {
                e.printStackTrace();
                ServiceLogger.LOGGER.warning("Internal server error.");
                return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }
}
