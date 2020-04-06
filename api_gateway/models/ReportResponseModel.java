package edu.uci.ics.binghal.service.api_gateway.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class ReportResponseModel {
    @JsonProperty(value = "response", required = true)
    private String response;

    @JsonCreator
    ReportResponseModel(){}

    @JsonCreator
    public ReportResponseModel(
            @JsonProperty(value = "response", required = true) String response) {
        this.response = response;
    }

    @JsonProperty(value = "response")
    public String getResponse() {
        return response;
    }
}
