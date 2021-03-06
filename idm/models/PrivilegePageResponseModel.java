package edu.uci.ics.binghal.service.idm.models;

public class PrivilegePageResponseModel {
    private int resultCode;
    private String message;

    public PrivilegePageResponseModel(){}

    public PrivilegePageResponseModel(int resultCode, String message) {
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