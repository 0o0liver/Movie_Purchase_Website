package edu.uci.ics.binghal.service.api_gateway.models.billing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import edu.uci.ics.binghal.service.api_gateway.models.billing.OrderModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderRetrieveResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    @JsonProperty(value = "transactions", required = false)
    private transactions[] transactions;

    @JsonCreator
    public OrderRetrieveResponseModel(){}

    @JsonCreator
    public OrderRetrieveResponseModel(
            @JsonProperty(value = "resultCode", required = true) int resultCode,
            @JsonProperty(value = "message", required = true) String message,
            @JsonProperty(value = "transactions", required = false) edu.uci.ics.binghal.service.api_gateway.models.billing.transactions[] transactions) {
        this.resultCode = resultCode;
        this.message = message;
        this.transactions = transactions;
    }

    @JsonProperty(value = "resultCode")
    public int getResultCode() {
        return resultCode;
    }

    @JsonProperty(value = "message")
    public String getMessage() {
        return message;
    }

    @JsonProperty(value = "transactions")
    public edu.uci.ics.binghal.service.api_gateway.models.billing.transactions[] getTransactions() {
        return transactions;
    }
}
