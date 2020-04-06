package edu.uci.ics.binghal.service.movies.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.binghal.service.movies.core.ResultCodeToStatus;
import edu.uci.ics.binghal.service.movies.core.Star;
import edu.uci.ics.binghal.service.movies.core.VerifyPrivilege;
import edu.uci.ics.binghal.service.movies.logger.ServiceLogger;
import edu.uci.ics.binghal.service.movies.models.*;


@Path("star")
public class StarPage {

    @Path("search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchStar(@Context HttpHeaders headers,
                               @QueryParam("name") String name, @QueryParam("birthYear") Integer birthYear,
                               @QueryParam("movieTitle") String movieTitle, @QueryParam("limit") Integer limit,
                               @QueryParam("offset") Integer offset, @QueryParam("orderby") String orderby,
                               @QueryParam("direction") String direction){
        ServiceLogger.LOGGER.info("Received a search star request.");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");

        ServiceLogger.LOGGER.info("Name received: " + name);
        ServiceLogger.LOGGER.info("BirthYeat received: " +  birthYear);
        ServiceLogger.LOGGER.info("Movie title received: " + movieTitle);
        ServiceLogger.LOGGER.info("Limit received: " + limit);
        ServiceLogger.LOGGER.info("Offset received: " + offset);
        ServiceLogger.LOGGER.info("SortBy received: " + orderby);
        ServiceLogger.LOGGER.info("OrderBy received: " + direction);

        SearchStarRequestModel requestModel;
        SearchStarResponseModel responseModel;
        try{
            requestModel = new SearchStarRequestModel(name, birthYear, movieTitle, limit, offset, orderby, direction);
            ServiceLogger.LOGGER.info(requestModel.toString());
            responseModel = Star.search(requestModel);
            return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("SessionID", sessionID).header("transactionID", transactionID).entity(responseModel).build();
        } catch (Exception e){
            e.printStackTrace();
        }
        return Response.status(Status.INTERNAL_SERVER_ERROR).header("email", email).header("SessionID", sessionID).header("transactionID", transactionID).build();
    }


    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchStarById(@Context HttpHeaders headers, @PathParam("id") String id){
        ServiceLogger.LOGGER.info("Received a request to search star by id.");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");

        StarSearchIDResponseModel responseModel;
        try{
            responseModel = Star.searchByID(id);
            return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("SessionID", sessionID).header("transactionID", transactionID).entity(responseModel).build();
        } catch (Exception e){
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).header("email", email).header("SessionID", sessionID).header("transactionID", transactionID).build();
        }
    }


    @Path("add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addStar(@Context HttpHeaders headers, String jsonText){
        ServiceLogger.LOGGER.info("Received a request to add a star.");

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");
        AddStarRequestModel requestModel;
        VerifyPrivilegeResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        if (!VerifyPrivilege.isUserAllowedToMakeRequest(email, 3)){
            ServiceLogger.LOGGER.warning("User has insufficient privilege.");
            responseModel = new VerifyPrivilegeResponseModel(141, "User has insufficient privilege.");
            return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).entity(responseModel).build();
        }
        try{
            requestModel = mapper.readValue(jsonText, AddStarRequestModel.class);
            responseModel = Star.addStar(requestModel);
            return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).entity(responseModel).build();
        } catch (IOException e){
            if (e instanceof JsonMappingException){
                ServiceLogger.LOGGER.warning("Json Mapping Exception.");
                responseModel = new VerifyPrivilegeResponseModel(-2, "JSON mapping exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException){
                ServiceLogger.LOGGER.warning("Json Parse Exception.");
                responseModel = new VerifyPrivilegeResponseModel(-3, "JSON parse exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).entity(responseModel).build();
            }
            else {
                ServiceLogger.LOGGER.warning("Internal server error.");
                return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }


    @Path("starin")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response starin(@Context HttpHeaders headers, String jsonText){
        ServiceLogger.LOGGER.info("Received a starin request.");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");
        StarinRequestModel requestModel;
        VerifyPrivilegeResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        if (!VerifyPrivilege.isUserAllowedToMakeRequest(email, 3)){
            ServiceLogger.LOGGER.warning("User has insufficient privilege.");
            responseModel = new VerifyPrivilegeResponseModel(141, "User has insufficient privilege.");
            return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).entity(responseModel).build();
        }
        try{
            requestModel = mapper.readValue(jsonText, StarinRequestModel.class);
            responseModel = Star.starin(requestModel);
            return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).entity(responseModel).build();
        } catch (IOException e){
            if (e instanceof JsonMappingException){
                ServiceLogger.LOGGER.warning("Json Mapping Exception.");
                responseModel = new VerifyPrivilegeResponseModel(-2, "JSON mapping exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException){
                ServiceLogger.LOGGER.warning("Json Parse Exception.");
                responseModel = new VerifyPrivilegeResponseModel(-3, "JSON parse exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).entity(responseModel).build();
            }
            else {
                ServiceLogger.LOGGER.warning("Internal server error.");
                return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            }
        }


    }
}
