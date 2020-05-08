package com.example.newproject.Model;

public class Category {
    private String customerRequestID;
    private String time;
    private String service;


    public Category() {

    }

    public Category(String customerRequestID, String time, String service) {
        this.customerRequestID = customerRequestID;
        this.time = time;
        this.service = service;
    }

    public String getCustomerRequestID() {
        return customerRequestID;
    }

    public void setCustomerRequestID(String customerRequestID) {
        this.customerRequestID = customerRequestID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
}



