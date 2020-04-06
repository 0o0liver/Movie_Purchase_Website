package edu.uci.ics.binghal.service.idm.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import edu.uci.ics.binghal.service.idm.models.SessionPageRequestModel;
import edu.uci.ics.binghal.service.idm.models.SessionPageResponseModel;
import edu.uci.ics.binghal.service.idm.core.VerifySession;
import edu.uci.ics.binghal.service.idm.core.ResultCodeToStatus;
import edu.uci.ics.binghal.service.idm.logger.ServiceLogger;



@Path("session")
public class SessionPage{
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifySession(String jsonText){
        ServiceLogger.LOGGER.info("Received session request.");
        ObjectMapper mapper = new ObjectMapper();
        SessionPageRequestModel requestModel;
        SessionPageResponseModel responseModel;
        try{
            ServiceLogger.LOGGER.info("Mapping json to requestModel");
            requestModel = mapper.readValue(jsonText, SessionPageRequestModel.class);
            ServiceLogger.LOGGER.info("Mapping successfully.");
            ServiceLogger.LOGGER.info("Verifying session.");
            responseModel = VerifySession.verify(requestModel);
            ServiceLogger.LOGGER.info("Session verified.");
            return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).entity(responseModel).build();
        } catch (IOException e){
            if (e instanceof JsonMappingException){
                e.printStackTrace();
                ServiceLogger.LOGGER.warning("Json mapping exception.");
                responseModel = new SessionPageResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException){
                e.printStackTrace();
                ServiceLogger.LOGGER.warning("Json Parse Exceptions");
                responseModel = new SessionPageResponseModel(-3, "JSON Parse Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).entity(responseModel).build();
            }
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Internal server error.");
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}