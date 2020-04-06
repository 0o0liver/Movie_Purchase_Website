package edu.uci.ics.binghal.service.api_gateway.models.idm;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.binghal.service.api_gateway.models.RequestModel;

public class RegisterPageRequestModel extends RequestModel {
    private String email;
    private char[] password;

    public RegisterPageRequestModel(){}

    @JsonCreator
    public RegisterPageRequestModel(
            @JsonProperty(value = "email", required = true) String email,
            @JsonProperty(value = "password", required = true) char[] password){
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }
}
