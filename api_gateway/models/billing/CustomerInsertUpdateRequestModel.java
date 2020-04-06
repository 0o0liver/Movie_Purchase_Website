package edu.uci.ics.binghal.service.api_gateway.models.billing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.binghal.service.api_gateway.models.RequestModel;

public class CustomerInsertUpdateRequestModel extends RequestModel {
    @JsonProperty(value = "email", required = true)
    private String email;
    @JsonProperty(value = "firstName", required = true)
    private String firstName;
    @JsonProperty(value = "lastName", required = true)
    private String lastName;
    @JsonProperty(value = "ccId", required = true)
    private String ccId;
    @JsonProperty(value = "address", required = true)
    private String address;

    @JsonCreator
    public CustomerInsertUpdateRequestModel(){}

    @JsonCreator
    public CustomerInsertUpdateRequestModel(
            @JsonProperty(value = "email", required = true) String email,
            @JsonProperty(value = "firstName", required = true) String firstName,
            @JsonProperty(value = "lastName", required = true) String lastName,
            @JsonProperty(value = "ccId", required = true) String ccId,
            @JsonProperty(value = "address", required = true) String address) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.ccId = ccId;
        this.address = address;
    }

    @JsonProperty(value = "email")
    public String getEmail() {
        return email;
    }

    @JsonProperty(value = "firstName")
    public String getFirstName() {
        return firstName;
    }

    @JsonProperty(value = "lastName")
    public String getLastName() {
        return lastName;
    }

    @JsonProperty(value = "ccId")
    public String getCcId() {
        return ccId;
    }

    @JsonProperty(value = "address")
    public String getAddress() {
        return address;
    }
}
