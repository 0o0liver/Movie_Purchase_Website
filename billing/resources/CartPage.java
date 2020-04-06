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
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.binghal.service.billing.logger.ServiceLogger;
import edu.uci.ics.binghal.service.billing.models.CartDeleteRequestModel;
import edu.uci.ics.binghal.service.billing.models.CartInsertUpdateRequestModel;
import edu.uci.ics.binghal.service.billing.models.EmailRequestModel;
import edu.uci.ics.binghal.service.billing.models.GeneralResponseModel;
import edu.uci.ics.binghal.service.billing.models.CartRetrieveResponseModel;
import edu.uci.ics.binghal.service.billing.core.CheckEmail;
import edu.uci.ics.binghal.service.billing.core.ResultCodeToStatus;
import edu.uci.ics.binghal.service.billing.core.Cart;


@Path("cart")
public class CartPage {
    @Path("insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public static Response insert(@Context HttpHeaders headers, String jsonText){
        ServiceLogger.LOGGER.info("Received cart insertion request.");
        String email = headers.getHeaderString("email");
        String sessionId = headers.getHeaderString("sessionId");
        CartInsertUpdateRequestModel requestModel;
        GeneralResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try{
            ServiceLogger.LOGGER.info("Mapping jsonText to request model.");
            requestModel = mapper.readValue(jsonText, CartInsertUpdateRequestModel.class);
            if (!CheckEmail.checkFormat(requestModel.getEmail())){
                ServiceLogger.LOGGER.warning("Email has invalid format.");
                responseModel = new GeneralResponseModel(-11, "Email address has invalid format.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionId).entity(responseModel).build();
            }
            else if (!CheckEmail.checkLength(requestModel.getEmail())){
                ServiceLogger.LOGGER.warning("Email has invalid length.");
                responseModel = new GeneralResponseModel(-10, "Email address has invalid length.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionId).entity(responseModel).build();
            }
            responseModel = Cart.insert(requestModel);
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
    public static Response update(@Context HttpHeaders headers, String jsonText){
        ServiceLogger.LOGGER.info("Received cart update request.");
        String email = headers.getHeaderString("email");
        String sessionId = headers.getHeaderString("sessionId");
        CartInsertUpdateRequestModel requestModel;
        GeneralResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try{
            ServiceLogger.LOGGER.info("Mapping jsonText to request model.");
            requestModel = mapper.readValue(jsonText, CartInsertUpdateRequestModel.class);
            if (!CheckEmail.checkFormat(requestModel.getEmail())){
                ServiceLogger.LOGGER.warning("Email has invalid format.");
                responseModel = new GeneralResponseModel(-11, "Email address has invalid format.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionId).entity(responseModel).build();
            }
            else if (!CheckEmail.checkLength(requestModel.getEmail())){
                ServiceLogger.LOGGER.warning("Email has invalid length.");
                responseModel = new GeneralResponseModel(-10, "Email address has invalid length.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionId).entity(responseModel).build();
            }
            responseModel = Cart.update(requestModel);
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
    public static Response delete(@Context HttpHeaders headers, String jsonText){
        ServiceLogger.LOGGER.info("Received cart delete request.");
        String email = headers.getHeaderString("email");
        String sessionId = headers.getHeaderString("sessionId");
        CartDeleteRequestModel requestModel;
        GeneralResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try{
            ServiceLogger.LOGGER.info("Mapping jsonText to request model.");
            requestModel = mapper.readValue(jsonText, CartDeleteRequestModel.class);
            if (!CheckEmail.checkFormat(requestModel.getEmail())){
                ServiceLogger.LOGGER.warning("Email has invalid format.");
                responseModel = new GeneralResponseModel(-11, "Email address has invalid format.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionId).entity(responseModel).build();
            }
            else if (!CheckEmail.checkLength(requestModel.getEmail())){
                ServiceLogger.LOGGER.warning("Email has invalid length.");
                responseModel = new GeneralResponseModel(-10, "Email address has invalid length.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionId).entity(responseModel).build();
            }
            responseModel = Cart.delete(requestModel);
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
    public static Response retrieve(@Context HttpHeaders headers, String jsonText){
        ServiceLogger.LOGGER.info("Received cart retrieve request.");
        String email = headers.getHeaderString("email");
        String sessionId = headers.getHeaderString("sessionId");
        EmailRequestModel requestModel;
        CartRetrieveResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try{
            ServiceLogger.LOGGER.info("Mapping jsonText to request model.");
            requestModel = mapper.readValue(jsonText, EmailRequestModel.class);
            if (!CheckEmail.checkFormat(requestModel.getEmail())){
                ServiceLogger.LOGGER.warning("Email has invalid format.");
                responseModel = new CartRetrieveResponseModel(-11, "Email address has invalid format.", null);
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionId).entity(responseModel).build();
            }
            else if (!CheckEmail.checkLength(requestModel.getEmail())){
                ServiceLogger.LOGGER.warning("Email has invalid length.");
                responseModel = new CartRetrieveResponseModel(-10, "Email address has invalid length.", null);
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionId).entity(responseModel).build();
            }
            responseModel = Cart.retrieve(requestModel);
            return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionId).entity(responseModel).build();
        } catch (IOException e){
            if (e instanceof JsonMappingException){
                ServiceLogger.LOGGER.warning("Json Mapping Exception.");
                responseModel = new CartRetrieveResponseModel(-2, "JSON Mapping Exception.", null);
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionId).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException){
                ServiceLogger.LOGGER.warning("Json Parse Exception.");
                responseModel = new CartRetrieveResponseModel(-3, "JSON Parse Exception.", null);
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionId).entity(responseModel).build();
            }
            else {
                ServiceLogger.LOGGER.warning("Internal Server Error.");
                return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @Path("clear")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public static Response clear(@Context HttpHeaders headers, String jsonText){
        ServiceLogger.LOGGER.info("Received cart clear request.");
        String email = headers.getHeaderString("email");
        String sessionId = headers.getHeaderString("sessionId");
        EmailRequestModel requestModel;
        GeneralResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try{
            ServiceLogger.LOGGER.info("Mapping jsonText to request model.");
            requestModel = mapper.readValue(jsonText, EmailRequestModel.class);
            if (!CheckEmail.checkFormat(requestModel.getEmail())){
                ServiceLogger.LOGGER.warning("Email has invalid format.");
                responseModel = new GeneralResponseModel(-11, "Email address has invalid format.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionId).entity(responseModel).build();
            }
            else if (!CheckEmail.checkLength(requestModel.getEmail())){
                ServiceLogger.LOGGER.warning("Email has invalid length.");
                responseModel = new GeneralResponseModel(-10, "Email address has invalid length.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionId).entity(responseModel).build();
            }
            responseModel = Cart.clear(requestModel);
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

}
