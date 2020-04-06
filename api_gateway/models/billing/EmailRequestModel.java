package edu.uci.ics.binghal.service.api_gateway.models.billing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.binghal.service.api_gateway.models.RequestModel;


public class EmailRequestModel extends RequestModel {
    @JsonProperty(value = "email", required = true)
    private String email;

    @JsonCreator
    public EmailRequestModel(){}

    @JsonCreator
    public EmailRequestModel(
            @JsonProperty(value = "email", required = true) String email) {
        this.email = email;
    }

    @JsonProperty(value = "email")
    public String getEmail() {
        return email;
    }
}
