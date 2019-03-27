package com.shyn.zyot.wind.mychatapp.Model;

public class Message {
    private String message;
    private String senderID;

    public Message(String message, String senderID) {
        this.message = message;
        this.senderID = senderID;
    }

    public Message() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }
}
