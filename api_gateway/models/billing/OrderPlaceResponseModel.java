package edu.uci.ics.binghal.service.api_gateway.models.billing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderPlaceResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    @JsonProperty(value = "redirectURL", required = false)
    private String redirectURL;
    @JsonProperty(value = "token", required = false)
    private String token;

    @JsonCreator
    public OrderPlaceResponseModel(){}

    @JsonCreator
    public OrderPlaceResponseModel(
            @JsonProperty(value = "resultCode", required = true) int resultCode,
            @JsonProperty(value = "message", required = true) String message,
            @JsonProperty(value = "redirectURL", required = false) String redirectURL,
            @JsonProperty(value = "token", required = false) String token) {
        this.resultCode = resultCode;
        this.message = message;
        this.redirectURL = redirectURL;
        this.token = token;
    }

    @JsonProperty(value = "resultCode")
    public int getResultCode() {
        return resultCode;
    }

    @JsonProperty(value = "message")
    public String getMessage() {
        return message;
    }

    @JsonProperty(value = "redirectURL")
    public String getRedirectURL() {
        return redirectURL;
    }

    @JsonProperty(value = "token")
    public String getToken() {
        return token;
    }
}
