package edu.uci.ics.binghal.service.api_gateway.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.binghal.service.api_gateway.GatewayService;
import edu.uci.ics.binghal.service.api_gateway.core.ResultCodeToStatus;
import edu.uci.ics.binghal.service.api_gateway.core.VerifySession;
import edu.uci.ics.binghal.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.binghal.service.api_gateway.models.GeneralResponseModel;
import edu.uci.ics.binghal.service.api_gateway.models.billing.*;
import edu.uci.ics.binghal.service.api_gateway.models.idm.SessionPageResponseModel;
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

@Path("billing")
public class BillingEndpoints {
    @Path("cart/insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertToCartRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received a request to insert into cart.");
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
        CartInsertUpdateRequestModel requestModel;
        GeneralResponseModel responseModel;

        try{
            requestModel =  mapper.readValue(jsonText, CartInsertUpdateRequestModel.class);

            String transactionID = TransactionIDGenerator.generateTransactionID();

            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
            cr.setEndpoint(GatewayService.getBillingConfigs().getEPCartInsert());
            cr.setRequest(requestModel);
            cr.setTransactionID(transactionID);
            cr.setMethod("post");
            cr.setEmail(email);
            if (sessionID.equals(verifyResponseModel.getSessionID())){
                cr.setSessionID(sessionID);
            }
            else{
                sessionID = verifyResponseModel.getSessionID();
                cr.setSessionID(sessionID);
            }
            GatewayService.getThreadPool().add(cr);
            int requestDelay = GatewayService.getGatewayConfigs().getRequestDelay();
            return Response.status(Status.NO_CONTENT).header("requestDelay", requestDelay).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();

        } catch (IOException e){
            if (e instanceof JsonMappingException){
                ServiceLogger.LOGGER.warning("JSON Mapping Exception.");
                responseModel = new GeneralResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException){
                ServiceLogger.LOGGER.warning("JSON Parse Exceptions");
                responseModel = new GeneralResponseModel(-3, "JSON Parse Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).entity(responseModel).build();
            }
            ServiceLogger.LOGGER.warning("Internal server error.");
            return Response.status(Status.INTERNAL_SERVER_ERROR).header("email", email).header("sessionID", sessionID).build();
        }
    }

    @Path("cart/update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCartRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received a request to update cart.");
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
        CartInsertUpdateRequestModel requestModel;
        GeneralResponseModel responseModel;

        try{
            requestModel =  mapper.readValue(jsonText, CartInsertUpdateRequestModel.class);

            String transactionID = TransactionIDGenerator.generateTransactionID();

            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
            cr.setEndpoint(GatewayService.getBillingConfigs().getEPCartUpdate());
            cr.setRequest(requestModel);
            cr.setTransactionID(transactionID);
            cr.setMethod("post");
            cr.setEmail(email);
            if (sessionID.equals(verifyResponseModel.getSessionID())){
                cr.setSessionID(sessionID);
            }
            else{
                sessionID = verifyResponseModel.getSessionID();
                cr.setSessionID(sessionID);
            }
            GatewayService.getThreadPool().add(cr);
            int requestDelay = GatewayService.getGatewayConfigs().getRequestDelay();
            return Response.status(Status.NO_CONTENT).header("requestDelay", requestDelay).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();

        } catch (IOException e){
            if (e instanceof JsonMappingException){
                ServiceLogger.LOGGER.warning("JSON Mapping Exception.");
                responseModel = new GeneralResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException){
                ServiceLogger.LOGGER.warning("JSON Parse Exceptions");
                responseModel = new GeneralResponseModel(-3, "JSON Parse Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).entity(responseModel).build();
            }
            ServiceLogger.LOGGER.warning("Internal server error.");
            return Response.status(Status.INTERNAL_SERVER_ERROR).header("email", email).header("sessionID", sessionID).build();
        }
    }

    @Path("cart/delete")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCartRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received a request to delete item from cart.");
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
        CartDeleteRequestModel requestModel;
        GeneralResponseModel responseModel;

        try{
            requestModel =  mapper.readValue(jsonText, CartDeleteRequestModel.class);

            String transactionID = TransactionIDGenerator.generateTransactionID();

            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
            cr.setEndpoint(GatewayService.getBillingConfigs().getEPCartDelete());
            cr.setRequest(requestModel);
            cr.setTransactionID(transactionID);
            cr.setMethod("post");
            cr.setEmail(email);
            if (sessionID.equals(verifyResponseModel.getSessionID())){
                cr.setSessionID(sessionID);
            }
            else{
                sessionID = verifyResponseModel.getSessionID();
                cr.setSessionID(sessionID);
            }
            GatewayService.getThreadPool().add(cr);
            int requestDelay = GatewayService.getGatewayConfigs().getRequestDelay();
            return Response.status(Status.NO_CONTENT).header("requestDelay", requestDelay).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();

        } catch (IOException e){
            if (e instanceof JsonMappingException){
                ServiceLogger.LOGGER.warning("JSON Mapping Exception.");
                responseModel = new GeneralResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException){
                ServiceLogger.LOGGER.warning("JSON Parse Exceptions");
                responseModel = new GeneralResponseModel(-3, "JSON Parse Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).entity(responseModel).build();
            }
            ServiceLogger.LOGGER.warning("Internal server error.");
            return Response.status(Status.INTERNAL_SERVER_ERROR).header("email", email).header("sessionID", sessionID).build();
        }
    }

    @Path("cart/retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCartRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received a request to retrieve items from cart.");
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
        EmailRequestModel requestModel;
        GeneralResponseModel responseModel;

        try{
            requestModel =  mapper.readValue(jsonText, EmailRequestModel.class);

            String transactionID = TransactionIDGenerator.generateTransactionID();

            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
            cr.setEndpoint(GatewayService.getBillingConfigs().getEPCartRetrieve());
            cr.setRequest(requestModel);
            cr.setTransactionID(transactionID);
            cr.setMethod("post");
            cr.setEmail(email);
            if (sessionID.equals(verifyResponseModel.getSessionID())){
                cr.setSessionID(sessionID);
            }
            else{
                sessionID = verifyResponseModel.getSessionID();
                cr.setSessionID(sessionID);
            }
            GatewayService.getThreadPool().add(cr);
            int requestDelay = GatewayService.getGatewayConfigs().getRequestDelay();
            return Response.status(Status.NO_CONTENT).header("requestDelay", requestDelay).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();

        } catch (IOException e){
            if (e instanceof JsonMappingException){
                ServiceLogger.LOGGER.warning("JSON Mapping Exception.");
                responseModel = new GeneralResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException){
                ServiceLogger.LOGGER.warning("JSON Parse Exceptions");
                responseModel = new GeneralResponseModel(-3, "JSON Parse Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).entity(responseModel).build();
            }
            ServiceLogger.LOGGER.warning("Internal server error.");
            return Response.status(Status.INTERNAL_SERVER_ERROR).header("email", email).header("sessionID", sessionID).build();
        }
    }

    @Path("cart/clear")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response clearCartRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received a request to clear cart.");
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
        EmailRequestModel requestModel;
        GeneralResponseModel responseModel;

        try{
            requestModel =  mapper.readValue(jsonText, EmailRequestModel.class);

            String transactionID = TransactionIDGenerator.generateTransactionID();

            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
            cr.setEndpoint(GatewayService.getBillingConfigs().getEPCartClear());
            cr.setRequest(requestModel);
            cr.setTransactionID(transactionID);
            cr.setMethod("post");
            cr.setEmail(email);
            if (sessionID.equals(verifyResponseModel.getSessionID())){
                cr.setSessionID(sessionID);
            }
            else{
                sessionID = verifyResponseModel.getSessionID();
                cr.setSessionID(sessionID);
            }
            GatewayService.getThreadPool().add(cr);
            int requestDelay = GatewayService.getGatewayConfigs().getRequestDelay();
            return Response.status(Status.NO_CONTENT).header("requestDelay", requestDelay).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();

        } catch (IOException e){
            if (e instanceof JsonMappingException){
                ServiceLogger.LOGGER.warning("JSON Mapping Exception.");
                responseModel = new GeneralResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException){
                ServiceLogger.LOGGER.warning("JSON Parse Exceptions");
                responseModel = new GeneralResponseModel(-3, "JSON Parse Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).entity(responseModel).build();
            }
            ServiceLogger.LOGGER.warning("Internal server error.");
            return Response.status(Status.INTERNAL_SERVER_ERROR).header("email", email).header("sessionID", sessionID).build();
        }
    }

    @Path("creditcard/insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertCreditCardRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received a request to insert credit card.");
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
        CreditCardInsertUpdateRequestModel requestModel;
        GeneralResponseModel responseModel;

        try{
            requestModel =  mapper.readValue(jsonText, CreditCardInsertUpdateRequestModel.class);

            String transactionID = TransactionIDGenerator.generateTransactionID();

            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
            cr.setEndpoint(GatewayService.getBillingConfigs().getEPCcInsert());
            cr.setRequest(requestModel);
            cr.setTransactionID(transactionID);
            cr.setMethod("post");
            cr.setEmail(email);
            if (sessionID.equals(verifyResponseModel.getSessionID())){
                cr.setSessionID(sessionID);
            }
            else{
                sessionID = verifyResponseModel.getSessionID();
                cr.setSessionID(sessionID);
            }
            GatewayService.getThreadPool().add(cr);
            int requestDelay = GatewayService.getGatewayConfigs().getRequestDelay();
            return Response.status(Status.NO_CONTENT).header("requestDelay", requestDelay).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();

        } catch (IOException e){
            if (e instanceof JsonMappingException){
                ServiceLogger.LOGGER.warning("JSON Mapping Exception.");
                responseModel = new GeneralResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException){
                ServiceLogger.LOGGER.warning("JSON Parse Exceptions");
                responseModel = new GeneralResponseModel(-3, "JSON Parse Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).entity(responseModel).build();
            }
            ServiceLogger.LOGGER.warning("Internal server error.");
            return Response.status(Status.INTERNAL_SERVER_ERROR).header("email", email).header("sessionID", sessionID).build();
        }
    }

    @Path("creditcard/update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCreditCardRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received a request to update credit card.");
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
        CreditCardInsertUpdateRequestModel requestModel;
        GeneralResponseModel responseModel;

        try{
            requestModel =  mapper.readValue(jsonText, CreditCardInsertUpdateRequestModel.class);

            String transactionID = TransactionIDGenerator.generateTransactionID();

            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
            cr.setEndpoint(GatewayService.getBillingConfigs().getEPCcUpdate());
            cr.setRequest(requestModel);
            cr.setTransactionID(transactionID);
            cr.setMethod("post");
            cr.setEmail(email);
            if (sessionID.equals(verifyResponseModel.getSessionID())){
                cr.setSessionID(sessionID);
            }
            else{
                sessionID = verifyResponseModel.getSessionID();
                cr.setSessionID(sessionID);
            }
            GatewayService.getThreadPool().add(cr);
            int requestDelay = GatewayService.getGatewayConfigs().getRequestDelay();
            return Response.status(Status.NO_CONTENT).header("requestDelay", requestDelay).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();

        } catch (IOException e){
            if (e instanceof JsonMappingException){
                ServiceLogger.LOGGER.warning("JSON Mapping Exception.");
                responseModel = new GeneralResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException){
                ServiceLogger.LOGGER.warning("JSON Parse Exceptions");
                responseModel = new GeneralResponseModel(-3, "JSON Parse Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).entity(responseModel).build();
            }
            ServiceLogger.LOGGER.warning("Internal server error.");
            return Response.status(Status.INTERNAL_SERVER_ERROR).header("email", email).header("sessionID", sessionID).build();
        }
    }

    @Path("creditcard/delete")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCreditCardRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received a request to delete credit card.");
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
        IdRequestModel requestModel;
        GeneralResponseModel responseModel;

        try{
            requestModel =  mapper.readValue(jsonText, IdRequestModel.class);

            String transactionID = TransactionIDGenerator.generateTransactionID();

            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
            cr.setEndpoint(GatewayService.getBillingConfigs().getEPCcDelete());
            cr.setRequest(requestModel);
            cr.setTransactionID(transactionID);
            cr.setMethod("post");
            cr.setEmail(email);
            if (sessionID.equals(verifyResponseModel.getSessionID())){
                cr.setSessionID(sessionID);
            }
            else{
                sessionID = verifyResponseModel.getSessionID();
                cr.setSessionID(sessionID);
            }
            GatewayService.getThreadPool().add(cr);
            int requestDelay = GatewayService.getGatewayConfigs().getRequestDelay();
            return Response.status(Status.NO_CONTENT).header("requestDelay", requestDelay).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();

        } catch (IOException e){
            if (e instanceof JsonMappingException){
                ServiceLogger.LOGGER.warning("JSON Mapping Exception.");
                responseModel = new GeneralResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException){
                ServiceLogger.LOGGER.warning("JSON Parse Exceptions");
                responseModel = new GeneralResponseModel(-3, "JSON Parse Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).entity(responseModel).build();
            }
            ServiceLogger.LOGGER.warning("Internal server error.");
            return Response.status(Status.INTERNAL_SERVER_ERROR).header("email", email).header("sessionID", sessionID).build();
        }
    }

    @Path("creditcard/retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCreditCardRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received a request to retrieve credit card.");
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
        IdRequestModel requestModel;
        GeneralResponseModel responseModel;

        try{
            requestModel =  mapper.readValue(jsonText, IdRequestModel.class);

            String transactionID = TransactionIDGenerator.generateTransactionID();

            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
            cr.setEndpoint(GatewayService.getBillingConfigs().getEPCcRetrieve());
            cr.setRequest(requestModel);
            cr.setTransactionID(transactionID);
            cr.setMethod("post");
            cr.setEmail(email);
            if (sessionID.equals(verifyResponseModel.getSessionID())){
                cr.setSessionID(sessionID);
            }
            else{
                sessionID = verifyResponseModel.getSessionID();
                cr.setSessionID(sessionID);
            }
            GatewayService.getThreadPool().add(cr);
            int requestDelay = GatewayService.getGatewayConfigs().getRequestDelay();
            return Response.status(Status.NO_CONTENT).header("requestDelay", requestDelay).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();

        } catch (IOException e){
            if (e instanceof JsonMappingException){
                ServiceLogger.LOGGER.warning("JSON Mapping Exception.");
                responseModel = new GeneralResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException){
                ServiceLogger.LOGGER.warning("JSON Parse Exceptions");
                responseModel = new GeneralResponseModel(-3, "JSON Parse Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).entity(responseModel).build();
            }
            ServiceLogger.LOGGER.warning("Internal server error.");
            return Response.status(Status.INTERNAL_SERVER_ERROR).header("email", email).header("sessionID", sessionID).build();
        }
    }

    @Path("customer/insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertCustomerRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received a request to insert customer.");
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
        CustomerInsertUpdateRequestModel requestModel;
        GeneralResponseModel responseModel;

        try{
            requestModel =  mapper.readValue(jsonText, CustomerInsertUpdateRequestModel.class);

            String transactionID = TransactionIDGenerator.generateTransactionID();

            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
            cr.setEndpoint(GatewayService.getBillingConfigs().getEPCustomerInsert());
            cr.setRequest(requestModel);
            cr.setTransactionID(transactionID);
            cr.setMethod("post");
            cr.setEmail(email);
            if (sessionID.equals(verifyResponseModel.getSessionID())){
                cr.setSessionID(sessionID);
            }
            else{
                sessionID = verifyResponseModel.getSessionID();
                cr.setSessionID(sessionID);
            }
            GatewayService.getThreadPool().add(cr);
            int requestDelay = GatewayService.getGatewayConfigs().getRequestDelay();
            return Response.status(Status.NO_CONTENT).header("requestDelay", requestDelay).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();

        } catch (IOException e){
            if (e instanceof JsonMappingException){
                ServiceLogger.LOGGER.warning("JSON Mapping Exception.");
                responseModel = new GeneralResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException){
                ServiceLogger.LOGGER.warning("JSON Parse Exceptions");
                responseModel = new GeneralResponseModel(-3, "JSON Parse Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).entity(responseModel).build();
            }
            ServiceLogger.LOGGER.warning("Internal server error.");
            return Response.status(Status.INTERNAL_SERVER_ERROR).header("email", email).header("sessionID", sessionID).build();
        }
    }

    @Path("customer/update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCustomerRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received a request to update customer.");
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
        CustomerInsertUpdateRequestModel requestModel;
        GeneralResponseModel responseModel;

        try{
            requestModel =  mapper.readValue(jsonText, CustomerInsertUpdateRequestModel.class);

            String transactionID = TransactionIDGenerator.generateTransactionID();

            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
            cr.setEndpoint(GatewayService.getBillingConfigs().getEPCustomerUpdate());
            cr.setRequest(requestModel);
            cr.setTransactionID(transactionID);
            cr.setMethod("post");
            cr.setEmail(email);
            if (sessionID.equals(verifyResponseModel.getSessionID())){
                cr.setSessionID(sessionID);
            }
            else{
                sessionID = verifyResponseModel.getSessionID();
                cr.setSessionID(sessionID);
            }
            GatewayService.getThreadPool().add(cr);
            int requestDelay = GatewayService.getGatewayConfigs().getRequestDelay();
            return Response.status(Status.NO_CONTENT).header("requestDelay", requestDelay).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();

        } catch (IOException e){
            if (e instanceof JsonMappingException){
                ServiceLogger.LOGGER.warning("JSON Mapping Exception.");
                responseModel = new GeneralResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException){
                ServiceLogger.LOGGER.warning("JSON Parse Exceptions");
                responseModel = new GeneralResponseModel(-3, "JSON Parse Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).entity(responseModel).build();
            }
            ServiceLogger.LOGGER.warning("Internal server error.");
            return Response.status(Status.INTERNAL_SERVER_ERROR).header("email", email).header("sessionID", sessionID).build();
        }
    }

    @Path("customer/retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCustomerRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received a request to retrieve customer.");
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
        EmailRequestModel requestModel;
        GeneralResponseModel responseModel;

        try{
            requestModel =  mapper.readValue(jsonText, EmailRequestModel.class);

            String transactionID = TransactionIDGenerator.generateTransactionID();

            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
            cr.setEndpoint(GatewayService.getBillingConfigs().getEPCustomerRetrieve());
            cr.setRequest(requestModel);
            cr.setTransactionID(transactionID);
            cr.setMethod("post");
            cr.setEmail(email);
            if (sessionID.equals(verifyResponseModel.getSessionID())){
                cr.setSessionID(sessionID);
            }
            else{
                sessionID = verifyResponseModel.getSessionID();
                cr.setSessionID(sessionID);
            }
            GatewayService.getThreadPool().add(cr);
            int requestDelay = GatewayService.getGatewayConfigs().getRequestDelay();
            return Response.status(Status.NO_CONTENT).header("requestDelay", requestDelay).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();

        } catch (IOException e){
            if (e instanceof JsonMappingException){
                ServiceLogger.LOGGER.warning("JSON Mapping Exception.");
                responseModel = new GeneralResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException){
                ServiceLogger.LOGGER.warning("JSON Parse Exceptions");
                responseModel = new GeneralResponseModel(-3, "JSON Parse Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).entity(responseModel).build();
            }
            ServiceLogger.LOGGER.warning("Internal server error.");
            return Response.status(Status.INTERNAL_SERVER_ERROR).header("email", email).header("sessionID", sessionID).build();
        }
    }

    @Path("order/place")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response placeOrderRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received a request to place order.");
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
        EmailRequestModel requestModel;
        GeneralResponseModel responseModel;

        try{
            requestModel =  mapper.readValue(jsonText, EmailRequestModel.class);

            String transactionID = TransactionIDGenerator.generateTransactionID();

            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
            cr.setEndpoint(GatewayService.getBillingConfigs().getEPOrderPlace());
            cr.setRequest(requestModel);
            cr.setTransactionID(transactionID);
            cr.setMethod("post");
            cr.setEmail(email);
            if (sessionID.equals(verifyResponseModel.getSessionID())){
                cr.setSessionID(sessionID);
            }
            else{
                sessionID = verifyResponseModel.getSessionID();
                cr.setSessionID(sessionID);
            }
            GatewayService.getThreadPool().add(cr);
            int requestDelay = GatewayService.getGatewayConfigs().getRequestDelay();
            return Response.status(Status.NO_CONTENT).header("requestDelay", requestDelay).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();

        } catch (IOException e){
            if (e instanceof JsonMappingException){
                ServiceLogger.LOGGER.warning("JSON Mapping Exception.");
                responseModel = new GeneralResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException){
                ServiceLogger.LOGGER.warning("JSON Parse Exceptions");
                responseModel = new GeneralResponseModel(-3, "JSON Parse Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).entity(responseModel).build();
            }
            ServiceLogger.LOGGER.warning("Internal server error.");
            return Response.status(Status.INTERNAL_SERVER_ERROR).header("email", email).header("sessionID", sessionID).build();
        }
    }

    @Path("order/retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveOrderRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received a request to retrieve order.");
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
        EmailRequestModel requestModel;
        GeneralResponseModel responseModel;

        try{
            requestModel =  mapper.readValue(jsonText, EmailRequestModel.class);

            String transactionID = TransactionIDGenerator.generateTransactionID();

            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
            cr.setEndpoint(GatewayService.getBillingConfigs().getEPOrderRetrieve());
            cr.setRequest(requestModel);
            cr.setTransactionID(transactionID);
            cr.setMethod("post");
            cr.setEmail(email);
            if (sessionID.equals(verifyResponseModel.getSessionID())){
                cr.setSessionID(sessionID);
            }
            else{
                sessionID = verifyResponseModel.getSessionID();
                cr.setSessionID(sessionID);
            }
            GatewayService.getThreadPool().add(cr);
            int requestDelay = GatewayService.getGatewayConfigs().getRequestDelay();
            return Response.status(Status.NO_CONTENT).header("requestDelay", requestDelay).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();

        } catch (IOException e){
            if (e instanceof JsonMappingException){
                ServiceLogger.LOGGER.warning("JSON Mapping Exception.");
                responseModel = new GeneralResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException){
                ServiceLogger.LOGGER.warning("JSON Parse Exceptions");
                responseModel = new GeneralResponseModel(-3, "JSON Parse Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).entity(responseModel).build();
            }
            ServiceLogger.LOGGER.warning("Internal server error.");
            return Response.status(Status.INTERNAL_SERVER_ERROR).header("email", email).header("sessionID", sessionID).build();
        }
    }
}
