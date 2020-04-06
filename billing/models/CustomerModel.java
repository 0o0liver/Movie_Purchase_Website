package edu.uci.ics.binghal.service.billing.models;

public class CustomerModel {
    private String email;
    private String firstName;
    private String lastName;
    private String ccId;
    private String address;

    public CustomerModel(){}

    public CustomerModel(String email, String firstName, String lastName, String ccId, String address) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.ccId = ccId;
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCcId() {
        return ccId;
    }

    public String getAddress() {
        return address;
    }
}
