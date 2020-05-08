package com.example.newproject.serviceRecyclerView;

public class ServiceObject {
    private String user;
    private String phone;
    private String service;
    private String serviceDescription;
    private String id;


    public ServiceObject(){

    }
    public ServiceObject(String user,String service,String phone,String id, String serviceDescription){
        this.user = user;
       this.phone=phone;
        this.service = service;
        this.id=id;
        this.serviceDescription=serviceDescription;

    }

    public String getUser(){
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
    public  String getPhone(){return phone;}

    public void setPhone(String phone){
      this.phone = phone;
    }

    public String getService(){return service;}

    public void setService(String service){
        this.service =service;
    }

    public String getId(){return id;}

    public void setId(String id){this.id= id;}

    public String getServiceDescription(){return serviceDescription;}

    public void setServiceDescription(String serviceDescription){this.serviceDescription=serviceDescription;}


}
