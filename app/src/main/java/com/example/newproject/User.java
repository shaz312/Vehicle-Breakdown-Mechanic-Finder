package com.example.newproject;

public class User {

    private String customerRequestID;

    public User(){

    }
    public User(String customerRequestID){
        this.customerRequestID = customerRequestID;
    }

    public String getCustomerRequestID(){
        return customerRequestID;
    }

    public void setCustomerRequestID(String customerRequestID) {
        this.customerRequestID = customerRequestID;
    }

}
