package edu.uci.ics.binghal.service.idm.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import edu.uci.ics.binghal.service.idm.models.PrivilegePageRequestModel;
import edu.uci.ics.binghal.service.idm.models.PrivilegePageResponseModel;
import edu.uci.ics.binghal.service.idm.core.Privilege;
import edu.uci.ics.binghal.service.idm.core.ResultCodeToStatus;
import edu.uci.ics.binghal.service.idm.logger.ServiceLogger;


@Path("privilege")
public class PrivilegePage{
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPrivilege(String jsonText){
        ServiceLogger.LOGGER.info("Received a privlege request.");
        ObjectMapper mapper = new ObjectMapper();
        PrivilegePageRequestModel requestModel;
        PrivilegePageResponseModel responseModel;
        try{
            ServiceLogger.LOGGER.info("Mapping json to requestModel.");
            requestModel = mapper.readValue(jsonText, PrivilegePageRequestModel.class);
            ServiceLogger.LOGGER.info("Mapping successfully.");
            ServiceLogger.LOGGER.info("Verifying user privilege.");
            responseModel = Privilege.process(requestModel);
            ServiceLogger.LOGGER.info("User privilege verified.");
            return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).entity(responseModel).build();
        } catch (IOException e){
            if (e instanceof JsonMappingException){
                ServiceLogger.LOGGER.warning("Json mapping exception.");
                responseModel = new PrivilegePageResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException){
                ServiceLogger.LOGGER.warning("Json parse exception.");
                responseModel = new PrivilegePageResponseModel(-3, "JSON Parse Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).entity(responseModel).build();
            }
            ServiceLogger.LOGGER.warning("Internal server error.");
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
