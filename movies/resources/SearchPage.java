package edu.uci.ics.binghal.service.movies.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.binghal.service.movies.core.CheckEmailFormat;
import edu.uci.ics.binghal.service.movies.core.ResultCodeToStatus;
import edu.uci.ics.binghal.service.movies.core.Search;
import edu.uci.ics.binghal.service.movies.core.VerifyPrivilege;
import edu.uci.ics.binghal.service.movies.logger.ServiceLogger;
import edu.uci.ics.binghal.service.movies.models.SearchPageRequestModel;
import edu.uci.ics.binghal.service.movies.models.SearchPageResponseModel;
import edu.uci.ics.binghal.service.movies.models.VerifyPrivilegeResponseModel;

@Path("search")
public class SearchPage {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public static Response search(@Context HttpHeaders headers, @QueryParam("title") String title,
                                  @QueryParam("genre") String genre, @QueryParam("year") Integer year,
                                  @QueryParam("director") String director, @QueryParam("hidden") Boolean hidden,
                                  @QueryParam("limit") Integer limit, @QueryParam("offset") Integer offset,
                                  @QueryParam("orderby") String orderby, @QueryParam("direction") String direction){
        ServiceLogger.LOGGER.info("Received a search request.");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");
        ServiceLogger.LOGGER.info(email);
        ServiceLogger.LOGGER.info(sessionID);
        ServiceLogger.LOGGER.info(transactionID);


        ServiceLogger.LOGGER.info("Ttile received: " + title);
        ServiceLogger.LOGGER.info("Genre received: " + genre);
        ServiceLogger.LOGGER.info("Year received: " + year);
        ServiceLogger.LOGGER.info("Director reveived: " + director);
        ServiceLogger.LOGGER.info("Hidden reveiced: " + hidden);
        ServiceLogger.LOGGER.info("Limit received: " + limit);
        ServiceLogger.LOGGER.info("Offset received: " + offset);
        ServiceLogger.LOGGER.info("OrderBy received: " + orderby);
        ServiceLogger.LOGGER.info("Direction received: " + direction);

        SearchPageRequestModel requestModel;
        SearchPageResponseModel responseModel;
        try {
            requestModel = new SearchPageRequestModel(title, genre, year, director, hidden, limit, offset, orderby, direction, email);
            ServiceLogger.LOGGER.info(requestModel.toString());
            responseModel = Search.searchMovie(requestModel);
            return Response.status(ResultCodeToStatus.convert(responseModel.getResultCode())).header("email", email).header("SessionID", sessionID).header("transactionID", transactionID).entity(responseModel).build();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return Response.status(Status.INTERNAL_SERVER_ERROR).header("email", email).header("SessionID", sessionID).header("transactionID", transactionID).build();
    }
}
