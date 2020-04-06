package edu.uci.ics.binghal.service.api_gateway.threadpool;

import edu.uci.ics.binghal.service.api_gateway.models.RequestModel;

import java.util.Map;

public class ClientRequest {
    private String email;
    private String sessionID;
    private String transactionID;
    private RequestModel request;
    private String URI;
    private String endpoint;
    private Map<String, Object> map;
    private String method;

    public ClientRequest() {}

    @Override
    public String toString() {
        return "ClientRequest{" +
                "email='" + email + '\'' +
                ", sessionID='" + sessionID + '\'' +
                ", transactionID='" + transactionID + '\'' +
                ", request=" + request +
                ", URI='" + URI + '\'' +
                ", endpoint='" + endpoint + '\'' +
                '}';
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public RequestModel getRequest() {
        return request;
    }

    public void setRequest(RequestModel request) {
        this.request = request;
    }

    public String getURI() {
        return URI;
    }

    public void setURI(String URI) {
        this.URI = URI;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
