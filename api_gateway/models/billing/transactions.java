package edu.uci.ics.binghal.service.api_gateway.models.billing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class transactions {
    @JsonProperty(value = "transactionId", required = true)
    private String transactionId;
    @JsonProperty(value = "state", required = true)
    private String state;
    @JsonProperty(value = "amount", required = true)
    private amount amount;
    @JsonProperty(value = "transaction_fee", required = true)
    private transaction_fee transaction_fee;
    @JsonProperty(value = "create_time", required = true)
    private String create_time;
    @JsonProperty(value = "update_time", required = true)
    private String update_time;
    @JsonProperty(value = "items", required = false)
    private OrderModel[] items;

    @JsonCreator
    public transactions(){}

    @JsonCreator
    public transactions(
            @JsonProperty(value = "transactionId", required = true) String transactionId,
            @JsonProperty(value = "state", required = true) String state,
            @JsonProperty(value = "amount", required = true) edu.uci.ics.binghal.service.api_gateway.models.billing.amount amount,
            @JsonProperty(value = "transaction_fee", required = true) edu.uci.ics.binghal.service.api_gateway.models.billing.transaction_fee transaction_fee,
            @JsonProperty(value = "create_time", required = true) String create_time,
            @JsonProperty(value = "update_time", required = true) String update_time,
            @JsonProperty(value = "items", required = false) OrderModel[] items) {
        this.transactionId = transactionId;
        this.state = state;
        this.amount = amount;
        this.transaction_fee = transaction_fee;
        this.create_time = create_time;
        this.update_time = update_time;
        this.items = items;
    }

    @JsonProperty(value = "transactionId")
    public String getTransactionId() {
        return transactionId;
    }

    @JsonProperty(value = "state")
    public String getState() {
        return state;
    }

    @JsonProperty(value = "amount")
    public edu.uci.ics.binghal.service.api_gateway.models.billing.amount getAmount() {
        return amount;
    }

    @JsonProperty(value = "transaction_fee")
    public edu.uci.ics.binghal.service.api_gateway.models.billing.transaction_fee getTransaction_fee() {
        return transaction_fee;
    }

    @JsonProperty(value = "create_time")
    public String getCreate_time() {
        return create_time;
    }

    @JsonProperty(value = "update_time")
    public String getUpdate_time() {
        return update_time;
    }

    @JsonProperty(value = "items")
    public OrderModel[] getItems() {
        return items;
    }
}
