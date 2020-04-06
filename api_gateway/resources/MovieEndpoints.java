package edu.uci.ics.binghal.service.api_gateway.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.binghal.service.api_gateway.GatewayService;
import edu.uci.ics.binghal.service.api_gateway.core.ResultCodeToStatus;
import edu.uci.ics.binghal.service.api_gateway.core.VerifySession;
import edu.uci.ics.binghal.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.binghal.service.api_gateway.models.GeneralResponseModel;
import edu.uci.ics.binghal.service.api_gateway.models.idm.SessionPageResponseModel;
import edu.uci.ics.binghal.service.api_gateway.models.movie.*;
import edu.uci.ics.binghal.service.api_gateway.threadpool.ClientRequest;
import edu.uci.ics.binghal.service.api_gateway.utilities.TransactionIDGenerator;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Path("movies")
public class MovieEndpoints {
    @Path("search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchMovieRequest(@Context HttpHeaders headers, @QueryParam("title") String title,
                                       @QueryParam("genre") String genre, @QueryParam("year") Integer year,
                                       @QueryParam("director") String director, @QueryParam("hidden") Boolean hidden,
                                       @QueryParam("limit") Integer limit, @QueryParam("offset") Integer offset,
                                       @QueryParam("orderby") String orderby, @QueryParam("direction") String direction) {

        ServiceLogger.LOGGER.info("Received request to search movies.");
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

        Map<String, Object> map = new HashMap<String, Object>();

        ServiceLogger.LOGGER.info("Inserting query params.");
        if (title != null){
            map.put("title", title);
        }
        if (genre != null){
            map.put("genre", genre);
        }
        if (year != null){
            map.put("year", year);
        }
        if (director != null){
            map.put("director", director);
        }
        if (hidden != null){
            map.put("hidden", hidden);
        }
        if (limit != null){
            map.put("limit", limit);
        }
        if (offset != null){
            map.put("offset", offset);
        }
        if (orderby != null){
            map.put("orderby", orderby);
        }
        if (direction != null){
            map.put("direction", direction);
        }

        ServiceLogger.LOGGER.info("Generating transactionID");
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ServiceLogger.LOGGER.info("Generating ClientRequest.");
        ClientRequest cr = new ClientRequest();
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPMovieSearch());
        cr.setEmail(email);
        cr.setTransactionID(transactionID);
        cr.setMap(map);
        cr.setMethod("get");
        if (sessionID.equals(verifyResponseModel.getSessionID())){
            cr.setSessionID(sessionID);
        }
        else{
            sessionID = verifyResponseModel.getSessionID();
            cr.setSessionID(sessionID);
        }

        ServiceLogger.LOGGER.info("Putting request to ThreadPool queue.");
        GatewayService.getThreadPool().add(cr);

        int requestDelay = GatewayService.getGatewayConfigs().getRequestDelay();
        return Response.status(Status.NO_CONTENT).header("requestDelay", requestDelay).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();
    }

    @Path("get/{movieid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMovieRequest(@Context HttpHeaders headers, @PathParam("movieid") String movieid) {
        ServiceLogger.LOGGER.info("Received a request to get info for movieid: " + movieid);
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

        ServiceLogger.LOGGER.info("Generating transactionID");
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ServiceLogger.LOGGER.info("Generating ClientRequest.");
        ClientRequest cr = new ClientRequest();
        cr.setEmail(email);
        cr.setTransactionID(transactionID);
        cr.setMethod("get");
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPMovieGet().replace("{movieid}", movieid));
        if (sessionID.equals(verifyResponseModel.getSessionID())){
            cr.setSessionID(sessionID);
        }
        else{
            sessionID = verifyResponseModel.getSessionID();
            cr.setSessionID(sessionID);
        }

        ServiceLogger.LOGGER.info("Putting request to ThreadPool queue.");
        GatewayService.getThreadPool().add(cr);

        int requestDelay = GatewayService.getGatewayConfigs().getRequestDelay();
        return Response.status(Status.NO_CONTENT).header("requestDelay", requestDelay).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();
    }

    @Path("add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addMovieRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received a request to add a movie.");
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
        AddPageRequestModel requestModel;
        GeneralResponseModel responseModel;

        try{
            requestModel =  mapper.readValue(jsonText, AddPageRequestModel.class);

            String transactionID = TransactionIDGenerator.generateTransactionID();

            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
            cr.setEndpoint(GatewayService.getMovieConfigs().getEPMovieAdd());
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

    @Path("delete/{movieid}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteMovieRequest(@Context HttpHeaders headers, @PathParam("movieid") String movieid) {
        ServiceLogger.LOGGER.info("Received a request to delete movie with movieid: " + movieid);
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

        ServiceLogger.LOGGER.info("Generating transactionID");
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ServiceLogger.LOGGER.info("Generating ClientRequest.");
        ClientRequest cr = new ClientRequest();
        cr.setEmail(email);
        cr.setTransactionID(transactionID);
        cr.setMethod("delete");
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPMovieDelete().replace("{movieid}", movieid));
        if (sessionID.equals(verifyResponseModel.getSessionID())){
            cr.setSessionID(sessionID);
        }
        else{
            sessionID = verifyResponseModel.getSessionID();
            cr.setSessionID(sessionID);
        }
        ServiceLogger.LOGGER.info("Putting request to ThreadPool queue.");
        GatewayService.getThreadPool().add(cr);

        int requestDelay = GatewayService.getGatewayConfigs().getRequestDelay();
        return Response.status(Status.NO_CONTENT).header("requestDelay", requestDelay).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();
    }

    @Path("genre")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGenresRequest(@Context HttpHeaders headers) {
        ServiceLogger.LOGGER.info("Received a request to retrieve all genres");
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

        ServiceLogger.LOGGER.info("Generating transactionID");
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ServiceLogger.LOGGER.info("Generating ClientRequest.");
        ClientRequest cr = new ClientRequest();
        cr.setEmail(email);
        cr.setTransactionID(transactionID);
        cr.setMethod("get");
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPGenreGet());
        if (sessionID.equals(verifyResponseModel.getSessionID())){
            cr.setSessionID(sessionID);
        }
        else{
            sessionID = verifyResponseModel.getSessionID();
            cr.setSessionID(sessionID);
        }
        ServiceLogger.LOGGER.info("Putting request to ThreadPool queue.");
        GatewayService.getThreadPool().add(cr);

        int requestDelay = GatewayService.getGatewayConfigs().getRequestDelay();
        return Response.status(Status.NO_CONTENT).header("requestDelay", requestDelay).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();
    }

    @Path("genre/add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addGenreRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received a request to add a new genre.");
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
        AddGenreRequestModel requestModel;
        GeneralResponseModel responseModel;

        try{
            requestModel =  mapper.readValue(jsonText, AddGenreRequestModel.class);

            String transactionID = TransactionIDGenerator.generateTransactionID();

            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
            cr.setEndpoint(GatewayService.getMovieConfigs().getEPGenreAdd());
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

    @Path("genre/{movieid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGenresForMovieRequest(@Context HttpHeaders headers, @PathParam("movieid") String movieid){
        ServiceLogger.LOGGER.info("Received a request to retrieve genre for movieid: " + movieid);
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

        ServiceLogger.LOGGER.info("Generating transactionID");
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ServiceLogger.LOGGER.info("Generating ClientRequest.");
        ClientRequest cr = new ClientRequest();
        cr.setEmail(email);
        cr.setTransactionID(transactionID);
        cr.setMethod("get");
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPGenreMovie().replace("{movieid}", movieid));
        if (sessionID.equals(verifyResponseModel.getSessionID())){
            cr.setSessionID(sessionID);
        }
        else{
            sessionID = verifyResponseModel.getSessionID();
            cr.setSessionID(sessionID);
        }
        ServiceLogger.LOGGER.info("Putting request to ThreadPool queue.");
        GatewayService.getThreadPool().add(cr);

        int requestDelay = GatewayService.getGatewayConfigs().getRequestDelay();
        return Response.status(Status.NO_CONTENT).header("requestDelay", requestDelay).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();
    }

    @Path("star/search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response starSearchRequest(@Context HttpHeaders headers,
                                      @QueryParam("name") String name, @QueryParam("birthYear") Integer birthYear,
                                      @QueryParam("movieTitle") String movieTitle, @QueryParam("limit") Integer limit,
                                      @QueryParam("offset") Integer offset, @QueryParam("orderby") String orderby,
                                      @QueryParam("direction") String direction) {
        ServiceLogger.LOGGER.info("Received a request to search a star.");
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

        Map<String, Object> map = new HashMap<String, Object>();

        ServiceLogger.LOGGER.info("Inserting query params.");
        if (name != null){
            map.put("name", name);
        }
        if (birthYear != null){
            map.put("birthYear", birthYear);
        }
        if (movieTitle != null){
            map.put("movieTitle", movieTitle);
        }
        if (limit != null){
            map.put("limit", limit);
        }
        if (offset != null){
            map.put("offset", offset);
        }
        if (orderby != null){
            map.put("orderby", orderby);
        }
        if (direction != null){
            map.put("direction", direction);
        }

        ServiceLogger.LOGGER.info("Generating transactionID");
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ServiceLogger.LOGGER.info("Generating ClientRequest.");
        ClientRequest cr = new ClientRequest();
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPStarSearch());
        cr.setEmail(email);
        cr.setTransactionID(transactionID);
        cr.setMap(map);
        cr.setMethod("get");
        if (sessionID.equals(verifyResponseModel.getSessionID())){
            cr.setSessionID(sessionID);
        }
        else{
            sessionID = verifyResponseModel.getSessionID();
            cr.setSessionID(sessionID);
        }

        ServiceLogger.LOGGER.info("Putting request to ThreadPool queue.");
        GatewayService.getThreadPool().add(cr);

        int requestDelay = GatewayService.getGatewayConfigs().getRequestDelay();
        return Response.status(Status.NO_CONTENT).header("requestDelay", requestDelay).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();
    }

    @Path("star/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStarRequest(@Context HttpHeaders headers, @PathParam("id") String id) {
        ServiceLogger.LOGGER.info("Received a request to retrieve info for star: " + id);
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

        ServiceLogger.LOGGER.info("Generating transactionID");
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ServiceLogger.LOGGER.info("Generating ClientRequest.");
        ClientRequest cr = new ClientRequest();
        cr.setEmail(email);
        cr.setTransactionID(transactionID);
        cr.setMethod("get");
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPStarGet().replace("{id}", id));
        if (sessionID.equals(verifyResponseModel.getSessionID())){
            cr.setSessionID(sessionID);
        }
        else{
            sessionID = verifyResponseModel.getSessionID();
            cr.setSessionID(sessionID);
        }
        ServiceLogger.LOGGER.info("Putting request to ThreadPool queue.");
        GatewayService.getThreadPool().add(cr);

        int requestDelay = GatewayService.getGatewayConfigs().getRequestDelay();
        return Response.status(Status.NO_CONTENT).header("requestDelay", requestDelay).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();
    }

    @Path("star/add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addStarRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received a request to add a new star.");
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
        AddStarRequestModel requestModel;
        GeneralResponseModel responseModel;

        try{
            requestModel =  mapper.readValue(jsonText, AddStarRequestModel.class);

            String transactionID = TransactionIDGenerator.generateTransactionID();

            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
            cr.setEndpoint(GatewayService.getMovieConfigs().getEPStarAdd());
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

    @Path("star/starsin")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addStarToMovieRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received a request to update star in movie.");
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
        StarinRequestModel requestModel;
        GeneralResponseModel responseModel;

        try{
            requestModel =  mapper.readValue(jsonText, StarinRequestModel.class);

            String transactionID = TransactionIDGenerator.generateTransactionID();

            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
            cr.setEndpoint(GatewayService.getMovieConfigs().getEPStarIn());
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

    @Path("rating")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRatingRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received a request to rate a movie.");
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
        RatingRequestModel requestModel;
        GeneralResponseModel responseModel;

        try{
            requestModel =  mapper.readValue(jsonText, RatingRequestModel.class);

            String transactionID = TransactionIDGenerator.generateTransactionID();

            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
            cr.setEndpoint(GatewayService.getMovieConfigs().getEPRating());
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
