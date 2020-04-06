package edu.uci.ics.binghal.service.api_gateway.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.binghal.service.api_gateway.GatewayService;
import edu.uci.ics.binghal.service.api_gateway.core.ResultCodeToStatus;
import edu.uci.ics.binghal.service.api_gateway.core.VerifySession;
import edu.uci.ics.binghal.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.binghal.service.api_gateway.models.GeneralResponseModel;
import edu.uci.ics.binghal.service.api_gateway.models.idm.*;
import edu.uci.ics.binghal.service.api_gateway.threadpool.ClientRequest;
import edu.uci.ics.binghal.service.api_gateway.utilities.TransactionIDGenerator;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;

@Path("idm")
public class IDMEndpoints {
    @Path("register")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUserRequest(String jsonText) {
        ServiceLogger.LOGGER.info("Received request to register user.");

        ObjectMapper mapper = new ObjectMapper();
        RegisterPageRequestModel requestModel;
        GeneralResponseModel responseModel;

        try {
            requestModel = mapper.readValue(jsonText, RegisterPageRequestModel.class);

            // creating transaction id
            String transactionID = TransactionIDGenerator.generateTransactionID();

            // creating client request
            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getIdmConfigs().getIdmUri());
            cr.setEndpoint(GatewayService.getIdmConfigs().getEPUserRegister());
            cr.setRequest(requestModel);
            cr.setTransactionID(transactionID);
            cr.setMethod("post");

            // putting request in ThreadPool queue
            GatewayService.getThreadPool().add(cr);

            // returning no content back
            int requestDelay = GatewayService.getGatewayConfigs().getRequestDelay();
            return Response.status(Status.NO_CONTENT).header("requestDelay", requestDelay).header("transactionID", transactionID).build();

        } catch (IOException e){
            if (e instanceof JsonMappingException){
                ServiceLogger.LOGGER.warning("JSON Mapping Exception.");
                responseModel = new GeneralResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException){
                ServiceLogger.LOGGER.warning("JSON Parse Exceptions");
                responseModel = new GeneralResponseModel(-3, "JSON Parse Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).entity(responseModel).build();
            }
            ServiceLogger.LOGGER.warning("Internal server error.");
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Path("login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginUserRequest(String jsonText) {
        ServiceLogger.LOGGER.info("Received request to login a user.");

        ObjectMapper mapper = new ObjectMapper();
        LoginPageRequestModel requestModel;
        GeneralResponseModel responseModel;

        try {
            requestModel = mapper.readValue(jsonText, LoginPageRequestModel.class);

            // creating transaction id
            String transactionID = TransactionIDGenerator.generateTransactionID();

            // creating client request
            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getIdmConfigs().getIdmUri());
            cr.setEndpoint(GatewayService.getIdmConfigs().getEPUserLogin());
            cr.setRequest(requestModel);
            cr.setTransactionID(transactionID);
            cr.setMethod("post");

            // putting request in ThreadPool queue
            GatewayService.getThreadPool().add(cr);

            // returning no content back
            int requestDelay = GatewayService.getGatewayConfigs().getRequestDelay();
            return Response.status(Status.NO_CONTENT).header("requestDelay", requestDelay).header("transactionID", transactionID).build();

        } catch (IOException e){
            if (e instanceof JsonMappingException){
                ServiceLogger.LOGGER.warning("JSON Mapping Exception.");
                responseModel = new GeneralResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException){
                ServiceLogger.LOGGER.warning("JSON Parse Exceptions");
                responseModel = new GeneralResponseModel(-3, "JSON Parse Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).entity(responseModel).build();
            }
            ServiceLogger.LOGGER.warning("Internal server error.");
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Path("session")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifySessionRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to verify a session.");

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        SessionPageResponseModel verifyResponseModel;

        if (sessionID == null){
            verifyResponseModel = new SessionPageResponseModel(-17, "SessionID not provided in request header.", null);
            return Response.status(ResultCodeToStatus.convert(verifyResponseModel.getResultCode())).entity(verifyResponseModel).header("email", email).header("sessionID", sessionID).build();
        }
        if (email == null){
            verifyResponseModel = new SessionPageResponseModel(-16, "Email not provided in request header.", null);
            return Response.status(ResultCodeToStatus.convert(verifyResponseModel.getResultCode())).entity(verifyResponseModel).header("email", email).header("sessionID", sessionID).build();
        }

        ObjectMapper mapper = new ObjectMapper();
        SessionPageRequestModel requestModel;
        GeneralResponseModel responseModel;

        try {
            requestModel = mapper.readValue(jsonText, SessionPageRequestModel.class);

            // creating transaction id
            String transactionID = TransactionIDGenerator.generateTransactionID();

            // creating client request
            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getIdmConfigs().getIdmUri());
            cr.setEndpoint(GatewayService.getIdmConfigs().getEPSessionVerify());
            cr.setRequest(requestModel);
            cr.setTransactionID(transactionID);
            cr.setMethod("post");

            // putting request in ThreadPool queue
            GatewayService.getThreadPool().add(cr);

            // returning no content back
            int requestDelay = GatewayService.getGatewayConfigs().getRequestDelay();
            return Response.status(Status.NO_CONTENT).header("requestDelay", requestDelay).header("transactionID", transactionID).build();

        } catch (IOException e){
            if (e instanceof JsonMappingException){
                ServiceLogger.LOGGER.warning("JSON Mapping Exception.");
                responseModel = new GeneralResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException){
                ServiceLogger.LOGGER.warning("JSON Parse Exceptions");
                responseModel = new GeneralResponseModel(-3, "JSON Parse Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).entity(responseModel).build();
            }
            ServiceLogger.LOGGER.warning("Internal server error.");
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Path("privilege")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifyUserPrivilegeRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to verify the privilege of a user.");

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        SessionPageResponseModel verifyResponseModel;

        if (sessionID == null){
            verifyResponseModel = new SessionPageResponseModel(-17, "SessionID not provided in request header.", null);
            return Response.status(ResultCodeToStatus.convert(verifyResponseModel.getResultCode())).entity(verifyResponseModel).header("email", email).header("sessionID", sessionID).build();
        }
        if (email == null){
            verifyResponseModel = new SessionPageResponseModel(-16, "Email not provided in request header.", null);
            return Response.status(ResultCodeToStatus.convert(verifyResponseModel.getResultCode())).entity(verifyResponseModel).header("email", email).header("sessionID", sessionID).build();
        }
        verifyResponseModel = VerifySession.verify(email, sessionID);
        if (verifyResponseModel.getResultCode() != 130){
            return Response.status(ResultCodeToStatus.convert(verifyResponseModel.getResultCode())).entity(verifyResponseModel).header("email", email).header("sessionID", sessionID).build();
        }

        ObjectMapper mapper = new ObjectMapper();
        PrivilegePageRequestModel requestModel;
        GeneralResponseModel responseModel;

        try {
            requestModel = mapper.readValue(jsonText, PrivilegePageRequestModel.class);

            // creating transaction id
            String transactionID = TransactionIDGenerator.generateTransactionID();

            // creating client request
            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getIdmConfigs().getIdmUri());
            cr.setEndpoint(GatewayService.getIdmConfigs().getEPUserPrivilegeVerify());
            cr.setRequest(requestModel);
            cr.setTransactionID(transactionID);
            cr.setMethod("post");

            // putting request in ThreadPool queue
            GatewayService.getThreadPool().add(cr);

            // returning no content back
            int requestDelay = GatewayService.getGatewayConfigs().getRequestDelay();
            return Response.status(Status.NO_CONTENT).header("requestDelay", requestDelay).header("transactionID", transactionID).build();

        } catch (IOException e){
            if (e instanceof JsonMappingException){
                ServiceLogger.LOGGER.warning("JSON Mapping Exception.");
                responseModel = new GeneralResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException){
                ServiceLogger.LOGGER.warning("JSON Parse Exceptions");
                responseModel = new GeneralResponseModel(-3, "JSON Parse Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).entity(responseModel).build();
            }
            ServiceLogger.LOGGER.warning("Internal server error.");
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
