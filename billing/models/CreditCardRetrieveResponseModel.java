package edu.uci.ics.binghal.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import edu.uci.ics.binghal.service.billing.models.CreditCardModel;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditCardRetrieveResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    @JsonProperty(value = "creditcard", required = false)
    private CreditCardModel creditcard;

    @JsonCreator
    public CreditCardRetrieveResponseModel(){}

    @JsonCreator
    public CreditCardRetrieveResponseModel(
            @JsonProperty(value = "resultCode", required = true) int resultCode,
            @JsonProperty(value = "message", required = true) String message,
            @JsonProperty(value = "creditcard", required = false) CreditCardModel creditcard) {
        this.resultCode = resultCode;
        this.message = message;
        this.creditcard = creditcard;
    }

    @JsonProperty(value = "resultCode")
    public int getResultCode() {
        return resultCode;
    }
    @JsonProperty(value = "message")
    public String getMessage() {
        return message;
    }

    @JsonProperty(value = "creditcard")
    public CreditCardModel getCreditcard() {
        return creditcard;
    }
}
