package edu.uci.ics.binghal.service.billing.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.binghal.service.billing.logger.ServiceLogger;
import edu.uci.ics.binghal.service.billing.models.GeneralResponseModel;
import edu.uci.ics.binghal.service.billing.core.ResultCodeToStatus;
import edu.uci.ics.binghal.service.billing.models.CreditCardInsertUpdateRequestModel;
import edu.uci.ics.binghal.service.billing.core.CreditCard;
import edu.uci.ics.binghal.service.billing.models.IdRequestModel;
import edu.uci.ics.binghal.service.billing.models.CreditCardRetrieveResponseModel;


@Path("creditcard")
public class CreditCardPage {
    @Path("insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public static Response insert(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to insert a credit card.");
        String email = headers.getHeaderString("email");
        String sessionId = headers.getHeaderString("sessionId");
        CreditCardInsertUpdateRequestModel requestModel;
        GeneralResponseModel responseModel;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(dateFormat);
        try{
            ServiceLogger.LOGGER.info("Mapping jsonText to request model.");
            requestModel = mapper.readValue(jsonText, CreditCardInsertUpdateRequestModel.class);
            responseModel = CreditCard.insert(requestModel);
            return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionId).entity(responseModel).build();
        } catch (IOException e){
            if (e instanceof JsonMappingException){
                ServiceLogger.LOGGER.warning("Json Mapping Exception.");
                responseModel = new GeneralResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionId).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException){
                ServiceLogger.LOGGER.warning("Json Parse Exception.");
                responseModel = new GeneralResponseModel(-3, "JSON Parse Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionId).entity(responseModel).build();
            }
            else {
                ServiceLogger.LOGGER.warning("Internal Server Error.");
                return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @Path("update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public static Response update(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to update a credit card.");
        String email = headers.getHeaderString("email");
        String sessionId = headers.getHeaderString("sessionId");
        CreditCardInsertUpdateRequestModel requestModel;
        GeneralResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();
        try{
            ServiceLogger.LOGGER.info("Mapping jsonText to request model.");
            requestModel = mapper.readValue(jsonText, CreditCardInsertUpdateRequestModel.class);
            responseModel = CreditCard.update(requestModel);
            return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionId).entity(responseModel).build();
        } catch (IOException e){
            if (e instanceof JsonMappingException){
                ServiceLogger.LOGGER.warning("Json Mapping Exception.");
                responseModel = new GeneralResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionId).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException){
                ServiceLogger.LOGGER.warning("Json Parse Exception.");
                responseModel = new GeneralResponseModel(-3, "JSON Parse Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionId).entity(responseModel).build();
            }
            else {
                ServiceLogger.LOGGER.warning("Internal Server Error.");
                return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @Path("delete")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public static Response delete(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to delete a credit card.");
        String email = headers.getHeaderString("email");
        String sessionId = headers.getHeaderString("sessionId");
        IdRequestModel requestModel;
        GeneralResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();
        try{
            ServiceLogger.LOGGER.info("Mapping jsonText to request model.");
            requestModel = mapper.readValue(jsonText, IdRequestModel.class);
            responseModel = CreditCard.delete(requestModel);
            return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionId).entity(responseModel).build();
        } catch (IOException e){
            if (e instanceof JsonMappingException){
                ServiceLogger.LOGGER.warning("Json Mapping Exception.");
                responseModel = new GeneralResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionId).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException){
                ServiceLogger.LOGGER.warning("Json Parse Exception.");
                responseModel = new GeneralResponseModel(-3, "JSON Parse Exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionId).entity(responseModel).build();
            }
            else {
                ServiceLogger.LOGGER.warning("Internal Server Error.");
                return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @Path("retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public static Response retrieve(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to retrieve a credit card.");
        String email = headers.getHeaderString("email");
        String sessionId = headers.getHeaderString("sessionId");
        IdRequestModel requestModel;
        CreditCardRetrieveResponseModel responseModel;
        //DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //dateFormat.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        ObjectMapper mapper = new ObjectMapper();
        //mapper.setDateFormat(dateFormat);
        try{
            ServiceLogger.LOGGER.info("Mapping jsonText to request model.");
            requestModel = mapper.readValue(jsonText, IdRequestModel.class);
            responseModel = CreditCard.retrieve(requestModel);
            return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionId).entity(responseModel).build();
        } catch (IOException e){
            if (e instanceof JsonMappingException){
                ServiceLogger.LOGGER.warning("Json Mapping Exception.");
                responseModel = new CreditCardRetrieveResponseModel(-2, "JSON Mapping Exception.", null);
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionId).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException){
                ServiceLogger.LOGGER.warning("Json Parse Exception.");
                responseModel = new CreditCardRetrieveResponseModel(-3, "JSON Parse Exception.", null);
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionId).entity(responseModel).build();
            }
            else {
                ServiceLogger.LOGGER.warning("Internal Server Error.");
                return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }
}


