package edu.uci.ics.binghal.service.api_gateway.core;


import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.binghal.service.api_gateway.GatewayService;
import edu.uci.ics.binghal.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.binghal.service.api_gateway.models.idm.SessionPageRequestModel;
import edu.uci.ics.binghal.service.api_gateway.models.idm.SessionPageResponseModel;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

public class VerifySession {
    public static SessionPageResponseModel verify(String email, String sessionID){
        ServiceLogger.LOGGER.info("Verifying session status for user: " + email + " sessionID: " + sessionID);

        ServiceLogger.LOGGER.info("Building client.");
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);

        ServiceLogger.LOGGER.info("Building URL.");
        String IDM_URI = GatewayService.getIdmConfigs().getIdmUri();

        ServiceLogger.LOGGER.info("Setting path to endpoint.");
        String IDM_ENDPOINT_PATH = GatewayService.getIdmConfigs().getEPSessionVerify();

        ServiceLogger.LOGGER.info("Building WebTarget.");
        WebTarget webTarget = client.target(IDM_URI).path(IDM_ENDPOINT_PATH);

        ServiceLogger.LOGGER.info("Starting invocation builder.");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        ServiceLogger.LOGGER.info("Setting payload of the request.");
        SessionPageRequestModel requestModel = new SessionPageRequestModel(email, sessionID);

        ServiceLogger.LOGGER.info("Sending request...");
        Response response = invocationBuilder.post(Entity.entity(requestModel, MediaType.APPLICATION_JSON));
        ServiceLogger.LOGGER.info("Sent!");

        ObjectMapper mapper = new ObjectMapper();
        SessionPageResponseModel responseModel;

        try{
            String jsonText = response.readEntity(String.class);
            responseModel = mapper.readValue(jsonText, SessionPageResponseModel.class);
            return responseModel;
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
}
