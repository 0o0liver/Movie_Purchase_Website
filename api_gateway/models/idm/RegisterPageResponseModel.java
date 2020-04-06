package edu.uci.ics.binghal.service.api_gateway.models.idm;

public class RegisterPageResponseModel{
    private int resultCode;
    private String message;

    public RegisterPageResponseModel(){}

    public RegisterPageResponseModel(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}