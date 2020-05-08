package com.example.newproject;

public class ChatMessage {
    private String username;
    private String message;

    public ChatMessage(String username, String message) {
        this.username = username;
        this.message = message;
    }

    public ChatMessage() {} // you need an empty constructor to get your object from the DataSnapshot (cf. MainActivity)

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }
}