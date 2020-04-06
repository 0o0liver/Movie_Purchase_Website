package edu.uci.ics.binghal.service.api_gateway.resources;


import edu.uci.ics.binghal.service.api_gateway.GatewayService;
import edu.uci.ics.binghal.service.api_gateway.core.ResultCodeToStatus;
import edu.uci.ics.binghal.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.binghal.service.api_gateway.models.GeneralResponseModel;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Path("report")
public class ReportPage {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response report(@Context HttpHeaders headers){
        ServiceLogger.LOGGER.info("Received report request.");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");
        ServiceLogger.LOGGER.info(transactionID);
        /*
        GeneralResponseModel verifyResponseModel;

        if (sessionID == null){
            verifyResponseModel = new GeneralResponseModel(-17, "SessionID not provided in request header.");
            return Response.status(ResultCodeToStatus.convert(verifyResponseModel.getResultCode())).entity(verifyResponseModel).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();
        }
        if (email == null){
            verifyResponseModel = new GeneralResponseModel(-16, "Email not provided in request header.");
            return Response.status(ResultCodeToStatus.convert(verifyResponseModel.getResultCode())).entity(verifyResponseModel).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();
        }
         */

        Connection conn = GatewayService.getConPool().requestCon();

        try {
            // Retrieving transactionId information from database.
            String query = "select * from responses where transactionid = ?;";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, transactionID);
            ResultSet rs = ps.executeQuery();
            if (rs.next() == false){
                return Response.status(Response.Status.NO_CONTENT).header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).build();
            }
            int status = rs.getInt("httpstatus");
            String response = rs.getString("response");
            String updatedSessionId = rs.getString("sessionId");

            // Deleting transactionID information from database.
            String deletingQuery = "delete from responses where transactionID = ?;";
            PreparedStatement deletingPs = conn.prepareStatement(deletingQuery);
            deletingPs.setString(1, transactionID);
            deletingPs.executeUpdate();

            // returning
            return Response.status(status).entity(response)
                    .header("email", email)
                    .header("sessionID", updatedSessionId)
                    .header("transactionID", transactionID)
                    .build();
        } catch (Exception e){
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }


}
