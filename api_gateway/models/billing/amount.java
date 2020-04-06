package edu.uci.ics.binghal.service.api_gateway.models.billing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class amount {
    @JsonProperty(value = "total", required = true)
    private String total;
    @JsonProperty(value = "currency", required = true)
    private String currency;

    @JsonCreator
    public amount(){}

    @JsonCreator
    public amount(
            @JsonProperty(value = "total", required = true) String total,
            @JsonProperty(value = "currency", required = true) String currency) {
        this.total = total;
        this.currency = currency;
    }

    @JsonProperty(value = "total")
    public String getTotal() {
        return total;
    }

    @JsonProperty(value = "currency")
    public String getCurrency() {
        return currency;
    }
}
