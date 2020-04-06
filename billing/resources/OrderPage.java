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
import com.paypal.api.payments.Payer;
import edu.uci.ics.binghal.service.billing.logger.ServiceLogger;
import edu.uci.ics.binghal.service.billing.models.GeneralResponseModel;
import edu.uci.ics.binghal.service.billing.core.ResultCodeToStatus;
import edu.uci.ics.binghal.service.billing.models.EmailRequestModel;
import edu.uci.ics.binghal.service.billing.core.Order;
import edu.uci.ics.binghal.service.billing.models.OrderPlaceResponseModel;
import edu.uci.ics.binghal.service.billing.models.OrderRetrieveResponseModel;

@Path("order")
public class OrderPage {
    @Path("place")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public static Response place(@Context HttpHeaders headers, String jsonText){
        ServiceLogger.LOGGER.info("Received request to place an order.");
        String email = headers.getHeaderString("email");
        String sessionId = headers.getHeaderString("sessionId");
        EmailRequestModel requestModel;
        OrderPlaceResponseModel responseModel;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(dateFormat);
        try{
            requestModel = mapper.readValue(jsonText, EmailRequestModel.class);
            responseModel = Order.place(requestModel);
            return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionId).entity(responseModel).build();
        } catch (IOException e){
            if (e instanceof JsonMappingException){
                ServiceLogger.LOGGER.warning("Json Mapping Exception.");
                responseModel = new OrderPlaceResponseModel(-2, "JSON Mapping Exception.", null, null);
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionId).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException){
                ServiceLogger.LOGGER.warning("Json Parse Exception.");
                responseModel = new OrderPlaceResponseModel(-3, "JSON Parse Exception.", null,null);
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionId).entity(responseModel).build();
            }
            else {
                ServiceLogger.LOGGER.warning("Internal Server Error.");
                return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @Path("complete")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public static Response complete(@Context HttpHeaders headers,
                                    @QueryParam("paymentId") String paymentId,
                                    @QueryParam("token") String token,
                                    @QueryParam("PayerID") String PayerID){
        ServiceLogger.LOGGER.info("Received request to complete an order.");
        String email = headers.getHeaderString("email");
        String sessionId = headers.getHeaderString("sessionId");
        GeneralResponseModel responseModel;
        responseModel = Order.complete(paymentId, token, PayerID);
        return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionId).entity(responseModel).build();
    }


    @Path("retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public static Response retrieve(@Context HttpHeaders headers, String jsonText){
        ServiceLogger.LOGGER.info("Received request to retrieve an order.");
        String email = headers.getHeaderString("email");
        String sessionId = headers.getHeaderString("sessionId");
        EmailRequestModel requestModel;
        OrderRetrieveResponseModel responseModel;
        //DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        ObjectMapper mapper = new ObjectMapper();
        //mapper.setDateFormat(dateFormat);
        try{
            requestModel = mapper.readValue(jsonText, EmailRequestModel.class);
            responseModel = Order.retrieve(requestModel);
            return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionId).entity(responseModel).build();
        } catch (IOException e){
            if (e instanceof JsonMappingException){
                ServiceLogger.LOGGER.warning("Json Mapping Exception.");
                responseModel = new OrderRetrieveResponseModel(-2, "JSON Mapping Exception.", null);
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionId).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException){
                ServiceLogger.LOGGER.warning("Json Parse Exception.");
                responseModel = new OrderRetrieveResponseModel(-3, "JSON Parse Exception.", null);
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionId).entity(responseModel).build();
            }
            else {
                ServiceLogger.LOGGER.warning("Internal Server Error.");
                return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }
}
