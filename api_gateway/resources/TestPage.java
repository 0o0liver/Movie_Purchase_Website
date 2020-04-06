package edu.uci.ics.binghal.service.api_gateway.resources;

import edu.uci.ics.binghal.service.api_gateway.logger.ServiceLogger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("test")
public class TestPage {
    @Path("hello")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response hello() {
        ServiceLogger.LOGGER.info("Hello!");
        return Response.status(200).build();
    }
}
