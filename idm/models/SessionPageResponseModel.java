package edu.uci.ics.binghal.service.idm.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionPageResponseModel{
    @JsonProperty(required = true)
    private int resultCode;
    @JsonProperty(required = true)
    private String message;
    private String sessionID;

    @JsonCreator
    public SessionPageResponseModel(int resultCode, String message, String sessionID){
        this.resultCode = resultCode;
        this.message = message;
        this.sessionID = sessionID;
    }

    @JsonCreator
    public SessionPageResponseModel(int resultCode, String message){
        this.resultCode = resultCode;
        this.message = message;
        this.sessionID = null;
    }

    @JsonProperty("resultCode")
    public int getResultCode() {
        return resultCode;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty(value = "sessionID")
    public String getSessionID() {
        return sessionID;
    }
}
