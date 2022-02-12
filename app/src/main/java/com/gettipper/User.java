package com.gettipper;

public class User {

    private String phoneNumber;
    private String fullName;
    private String mail;
    private Boolean serviceProvider;
    private String paypalName;

    public User() {
    }


    public String getPaypalName() {
        return paypalName;
    }

    public User setPaypalName(String paypalName) {
        this.paypalName = paypalName;
        return this;
    }



    public String getPhoneNumber() {
        return phoneNumber;
    }

    public User setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public User setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public String getMail() {
        return mail;
    }

    public User setMail(String mail) {
        this.mail = mail;
        return this;
    }

    public Boolean getServiceProvider() {
        return serviceProvider;
    }

    public User setServiceProvider(Boolean serviceProvider) {
        this.serviceProvider = serviceProvider;
        return this;
    }
}
