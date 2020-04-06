package edu.uci.ics.binghal.service.movies.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.binghal.service.movies.logger.ServiceLogger;
import edu.uci.ics.binghal.service.movies.core.CheckEmailFormat;
import edu.uci.ics.binghal.service.movies.core.ResultCodeToStatus;
import edu.uci.ics.binghal.service.movies.core.VerifyPrivilege;
import edu.uci.ics.binghal.service.movies.models.VerifyPrivilegeResponseModel;
import edu.uci.ics.binghal.service.movies.models.MovieIDPageResponseModel;
import edu.uci.ics.binghal.service.movies.core.SearchMovieById;



@Path("get")
public class MovieIDPage{
    @Path("{movieid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMovieFromID(@Context HttpHeaders headers, @PathParam("movieid") String id){
        ServiceLogger.LOGGER.info("Received a movie id request.");

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");
        ServiceLogger.LOGGER.info("Email received: " + email);
        ServiceLogger.LOGGER.info("SessionID received: " + sessionID);
        VerifyPrivilegeResponseModel privilegeResponseModel;
        MovieIDPageResponseModel responseModel;

        if (!SearchMovieById.movieExist(id)){
            ServiceLogger.LOGGER.warning("No movies found with search parameters.");
            privilegeResponseModel = new VerifyPrivilegeResponseModel(211, "No movies found with search parameters.");
            return Response.status(ResultCodeToStatus.convert(privilegeResponseModel.getResultCode())).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).entity(privilegeResponseModel).build();
        }
        try {
            responseModel = SearchMovieById.search(id);
            if (!VerifyPrivilege.isUserAllowedToMakeRequest(email, 4)){
                ServiceLogger.LOGGER.info("User plevel below 4.");
                if (responseModel.getMovie().isHidden()){
                    ServiceLogger.LOGGER.info("Requested movie is hidden.");
                    privilegeResponseModel = new VerifyPrivilegeResponseModel(141, "User has insufficient privilege.");
                    return Response.status(ResultCodeToStatus.convert(privilegeResponseModel.getResultCode())).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).entity(privilegeResponseModel).build();
                }
                ServiceLogger.LOGGER.info("Hiding \"hidden\" field from user.");
            }
            responseModel.getMovie().setHidden(null);
            return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).entity(responseModel).build();
        } catch (Exception e){
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}