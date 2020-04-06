package edu.uci.ics.binghal.service.idm.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import edu.uci.ics.binghal.service.idm.models.LoginPageRequestModel;
import edu.uci.ics.binghal.service.idm.models.LoginPageResponseModel;
import edu.uci.ics.binghal.service.idm.core.Login;
import edu.uci.ics.binghal.service.idm.core.ResultCodeToStatus;
import edu.uci.ics.binghal.service.idm.logger.ServiceLogger;


@Path("login")
public class LoginPage{
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(String jsonText){
        ServiceLogger.LOGGER.info("Received login request.");
        ObjectMapper mapper = new ObjectMapper();
        LoginPageRequestModel requestModel;
        LoginPageResponseModel responseModel;
        try{
            ServiceLogger.LOGGER.info("Mapping json to requestModel.");
            requestModel = mapper.readValue(jsonText, LoginPageRequestModel.class);
            ServiceLogger.LOGGER.info("Mapping successfully.");
            ServiceLogger.LOGGER.info("Logging in a user.");
            responseModel  = Login.loginUser(requestModel);
            ServiceLogger.LOGGER.info("User is logged in.");
            return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).entity(responseModel).build();
        } catch (IOException e){
            if (e instanceof JsonMappingException){
                ServiceLogger.LOGGER.warning("Json Mapping Exception.");
                responseModel = new LoginPageResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException){
                ServiceLogger.LOGGER.warning("Json Parse Exception.");
                responseModel = new LoginPageResponseModel(-3, "JSON Parse Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).entity(responseModel).build();
            }
            ServiceLogger.LOGGER.warning("Internal server error.");
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}