package com.shyn.zyot.wind.mychatapp.Model;

public class Message {
    private String content;
    private String sentBy;
    private boolean isSeen;

    public Message(String content, String sentBy, boolean isSeen) {
        this.content = content;
        this.sentBy = sentBy;
        this.isSeen = isSeen;
    }

    public Message() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }
}
