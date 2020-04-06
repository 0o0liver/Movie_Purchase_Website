package edu.uci.ics.binghal.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import edu.uci.ics.binghal.service.billing.models.CartItemModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartRetrieveResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    @JsonProperty(value = "items", required = false)
    private CartItemModel[] items;

    @JsonCreator
    public CartRetrieveResponseModel(){}

    @JsonCreator
    public CartRetrieveResponseModel(
            @JsonProperty(value = "resultCode", required = true) int resultCode,
            @JsonProperty(value = "message", required = true) String message,
            @JsonProperty(value = "items", required = false) CartItemModel[] items) {
        this.resultCode = resultCode;
        this.message = message;
        this.items = items;
    }

    @JsonProperty(value = "resultCode")
    public int getResultCode() {
        return resultCode;
    }

    @JsonProperty(value = "message")
    public String getMessage() {
        return message;
    }

    @JsonProperty(value = "items")
    public CartItemModel[] getItems() {
        return items;
    }
}
