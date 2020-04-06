package edu.uci.ics.binghal.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class EmailRequestModel {
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
