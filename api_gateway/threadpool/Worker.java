package edu.uci.ics.binghal.service.api_gateway.threadpool;

import edu.uci.ics.binghal.service.api_gateway.GatewayService;
import edu.uci.ics.binghal.service.api_gateway.logger.ServiceLogger;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class Worker extends Thread {
    int id;
    ThreadPool threadPool;

    private Worker(int id, ThreadPool threadPool) {
        this.id = id;
        this.threadPool = threadPool;
    }

    public static Worker CreateWorker(int id, ThreadPool threadPool) {
        return new Worker(id, threadPool);
    }

    public void process() {

        ServiceLogger.LOGGER.info("Removing a client request from the queue.");
        ClientRequest request = this.threadPool.remove();


        ServiceLogger.LOGGER.info("Worker " + this.id + " is processing client request now.");

        // create client
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);
        ServiceLogger.LOGGER.info("created client.");

        // create url
        String uri = request.getURI();
        String path = request.getEndpoint();
        ServiceLogger.LOGGER.info("url: " + uri + path);

        // create webtarget
        WebTarget webTarget = client.target(uri).path(path);
        if (request.getMap() != null) {
            for (Map.Entry<String, Object> entry : request.getMap().entrySet()) {
                webTarget = webTarget.queryParam(entry.getKey(), entry.getValue());
            }
        }
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON).header("email", request.getEmail()).header("sessionID", request.getSessionID()).header("transactionID", request.getTransactionID());
        ServiceLogger.LOGGER.info("created webtarget: " + webTarget.getUri());

        // sending request;
        Response response;
        if (request.getMethod().equals("delete")){
            response = invocationBuilder.delete();
        } else if (request.getMethod().equals("get")) {
            response = invocationBuilder.get();
        } else {
            response = invocationBuilder.post(Entity.entity(request.getRequest(), MediaType.APPLICATION_JSON));
        }

        String jsonText = response.readEntity(String.class);

        Connection conn = GatewayService.getConPool().requestCon();
        try {
            String query = "insert into responses (transactionid, email, sessionid, response, httpstatus) values (?, ?, ?, ?, ?);";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, request.getTransactionID());
            ps.setString(2, request.getEmail());
            ps.setString(3, request.getSessionID());
            ps.setString(4, jsonText);
            ps.setInt(5, response.getStatus());
            ServiceLogger.LOGGER.info("Executing: " + ps.toString());
            ps.executeUpdate();
        } catch (Exception e){
            e.printStackTrace();
        }

        // release connection
        GatewayService.getConPool().releaseCon(conn);

        return;
    }

    @Override
    public void run() {
        ServiceLogger.LOGGER.info("Worker: " + this.id + " is running.");
        while (true) {
            process();
        }
    }
}
