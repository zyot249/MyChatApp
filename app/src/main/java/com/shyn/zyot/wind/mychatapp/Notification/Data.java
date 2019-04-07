package com.shyn.zyot.wind.mychatapp.Notification;

public class Data {
    private String receiverID;
    private int icon;
    private String body;
    private String title;
    private String userID;

    public Data(String receiverID, int icon, String body, String title, String userID) {
        this.userID = userID;
        this.icon = icon;
        this.body = body;
        this.title = title;
        this.receiverID = receiverID;
    }

    public Data() {
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }
}
