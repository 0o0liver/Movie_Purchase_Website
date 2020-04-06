package edu.uci.ics.binghal.service.api_gateway.models.billing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.binghal.service.api_gateway.models.RequestModel;


public class IdRequestModel extends RequestModel {
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
