package edu.uci.ics.binghal.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class IdRequestModel {
    @JsonProperty(value = "id", required = true)
    private String id;

    @JsonCreator
    public IdRequestModel(){}

    @JsonCreator
    public IdRequestModel(
            @JsonProperty(value = "id", required = true) String id) {
        this.id = id;
    }

    @JsonProperty(value = "id")
    public String getId() {
        return id;
    }
}
