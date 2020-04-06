package edu.uci.ics.binghal.service.movies.core;

import javax.ws.rs.client.*;
import javax.xml.ws.Service;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.binghal.service.movies.MovieService;
import edu.uci.ics.binghal.service.movies.logger.ServiceLogger;
import edu.uci.ics.binghal.service.movies.models.VerifyPrivilegeRequestModel;
import edu.uci.ics.binghal.service.movies.models.VerifyPrivilegeResponseModel;
import org.glassfish.jersey.jackson.JacksonFeature;

public class VerifyPrivilege{

    public static boolean isUserAllowedToMakeRequest(String email, int plevel){
        ServiceLogger.LOGGER.info("Checking user privilege: " + email);

        ServiceLogger.LOGGER.info("Building client.");
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);

        ServiceLogger.LOGGER.info("Building URL.");
        String IDM_URI = MovieService.getMovieConfigs().getIdmConfigs().getIdmUri();

        ServiceLogger.LOGGER.info("Setting path to endpoint.");
        String IDM_ENDPOINT_PATH = MovieService.getMovieConfigs().getIdmConfigs().getPrivilegePath();

        ServiceLogger.LOGGER.info("Building WebTarget.");
        WebTarget webTarget = client.target(IDM_URI).path(IDM_ENDPOINT_PATH);

        ServiceLogger.LOGGER.info("Starting invocation builder.");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        ServiceLogger.LOGGER.info("Setting payload of the request.");
        VerifyPrivilegeRequestModel requestModel = new VerifyPrivilegeRequestModel(email, plevel);

        ServiceLogger.LOGGER.info("Sending request...");
        Response response = invocationBuilder.post(Entity.entity(requestModel, MediaType.APPLICATION_JSON));
        ServiceLogger.LOGGER.info("Sent!");

        if (response.getStatus() == 200) {
            ServiceLogger.LOGGER.info("Received Status 200");
            ObjectMapper mapper = new ObjectMapper();
            String jsonText = response.readEntity(String.class);
            try {
                VerifyPrivilegeResponseModel responseModel = mapper.readValue(jsonText, VerifyPrivilegeResponseModel.class);
                if (responseModel.getResultCode() == 140) {
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            ServiceLogger.LOGGER.info("Received Status " + response.getStatus() + ".");
        }
        return false;
    }
}