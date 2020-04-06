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
import edu.uci.ics.binghal.service.movies.core.Genre;
import edu.uci.ics.binghal.service.movies.core.SearchMovieById;
import edu.uci.ics.binghal.service.movies.logger.ServiceLogger;
import edu.uci.ics.binghal.service.movies.core.ResultCodeToStatus;
import edu.uci.ics.binghal.service.movies.core.VerifyPrivilege;
import edu.uci.ics.binghal.service.movies.models.AddGenreRequestModel;
import edu.uci.ics.binghal.service.movies.models.GetGenreResponseModel;
import edu.uci.ics.binghal.service.movies.models.VerifyPrivilegeResponseModel;

@Path("genre")
public class GenrePage {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public static Response getGenre(@Context HttpHeaders headers){
        ServiceLogger.LOGGER.info("Received a get genre request.");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");
        VerifyPrivilegeResponseModel privilegeResponseModel;
        GetGenreResponseModel responseModel;

        if (!VerifyPrivilege.isUserAllowedToMakeRequest(email, 5)){
            privilegeResponseModel = new VerifyPrivilegeResponseModel(141, "User has insufficient privilege.");
            return Response.status(ResultCodeToStatus.convert(privilegeResponseModel.getResultCode())).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).entity(privilegeResponseModel).build();
        }
        try {
            responseModel = Genre.retrieve();
            return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).entity(responseModel).build();
        } catch (Exception e){
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Path("add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public static Response addGenre(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received a add genre request.");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");

        AddGenreRequestModel requestModel;
        VerifyPrivilegeResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        if (!VerifyPrivilege.isUserAllowedToMakeRequest(email, 3)) {
            ServiceLogger.LOGGER.warning("User has insufficient privilege.");
            responseModel = new VerifyPrivilegeResponseModel(141, "User has insufficient privilege.");
            return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).entity(responseModel).build();
        }
        try {
            requestModel = mapper.readValue(jsonText, AddGenreRequestModel.class);
            responseModel = Genre.add(requestModel);
            return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).entity(responseModel).build();

        } catch (IOException e) {
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Json Mapping Exception.");
                responseModel = new VerifyPrivilegeResponseModel(-2, "JSON mapping exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).entity(responseModel).build();
            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Json Parse Exception.");
                responseModel = new VerifyPrivilegeResponseModel(-3, "JSON parse exception.");
                return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).entity(responseModel).build();
            } else {
                ServiceLogger.LOGGER.warning("Internal server error.");
                return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @Path("{movieid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public static Response getMovieGenre(@Context HttpHeaders headers, @PathParam("movieid") String movieid){
        ServiceLogger.LOGGER.info("Received a request to retrieve genres of a movie.");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");

        GetGenreResponseModel responseModel;

        if (!VerifyPrivilege.isUserAllowedToMakeRequest(email, 3)) {
            ServiceLogger.LOGGER.warning("User has insufficient privilege.");
            responseModel = new GetGenreResponseModel(141, "User has insufficient privilege.", null);
            return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).entity(responseModel).build();
        }
        else if (!SearchMovieById.movieExist(movieid)){
            ServiceLogger.LOGGER.warning("Movie doesn't exist.");
            responseModel = new GetGenreResponseModel(211, "No movies found with search parameters.", null);
            return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).entity(responseModel).build();
        }
        try {
            responseModel = Genre.getMovieGenre(movieid);
            return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).entity(responseModel).build();
        } catch (Exception e){
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }





    }
}
