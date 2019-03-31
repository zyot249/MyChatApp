package com.shyn.zyot.wind.mychatapp.Model;

public class Message {
    private String message;
    private String senderID;
    private boolean isSeen;

    public Message(String message, String senderID, boolean isSeen) {
        this.message = message;
        this.senderID = senderID;
        this.isSeen = isSeen;
    }

    public Message() {
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
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
