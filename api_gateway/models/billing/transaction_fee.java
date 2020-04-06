package edu.uci.ics.binghal.service.api_gateway.models.billing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class transaction_fee {
    @JsonProperty(value = "value", required = true)
    private String value;
    @JsonProperty(value = "currency", required = true)
    private String currency;

    @JsonCreator
    public transaction_fee(){}

    @JsonCreator
    public transaction_fee(
            @JsonProperty(value = "value", required = true) String value,
            @JsonProperty(value = "currency", required = true) String currency) {
        this.value = value;
        this.currency = currency;
    }

    @JsonProperty(value = "value")
    public String getValue() {
        return value;
    }

    @JsonProperty(value = "currency")
    public String getCurrency() {
        return currency;
    }
}
