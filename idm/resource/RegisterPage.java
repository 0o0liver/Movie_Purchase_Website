package edu.uci.ics.binghal.service.idm.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import edu.uci.ics.binghal.service.idm.models.RegisterPageRequestModel;
import edu.uci.ics.binghal.service.idm.models.RegisterPageResponseModel;
import edu.uci.ics.binghal.service.idm.core.Register;
import edu.uci.ics.binghal.service.idm.core.ResultCodeToStatus;
import edu.uci.ics.binghal.service.idm.logger.ServiceLogger;



@Path("register")
public class RegisterPage{
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(String jsonText){
        ServiceLogger.LOGGER.info("Received register request.");
        ObjectMapper mapper = new ObjectMapper();
        RegisterPageRequestModel requestModel;
        RegisterPageResponseModel responseModel;
        try{
            ServiceLogger.LOGGER.info("Mapping json to requestModel.");
            requestModel = mapper.readValue(jsonText, RegisterPageRequestModel.class);
            ServiceLogger.LOGGER.info("Mapping successfully.");
            ServiceLogger.LOGGER.info("Registering new user.");
            responseModel = Register.registerUser(requestModel);
            ServiceLogger.LOGGER.info("Successfully registered a new user.");
            return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).entity(responseModel).build();
        } catch (IOException e){
            if (e instanceof JsonMappingException){
                ServiceLogger.LOGGER.warning("JSON Mapping Exception.");
                responseModel = new RegisterPageResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException){
                ServiceLogger.LOGGER.warning("JSON Parse Exceptions");
                responseModel = new RegisterPageResponseModel(-3, "JSON Parse Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).entity(responseModel).build();
            }
            ServiceLogger.LOGGER.warning("Internal server error.");
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}