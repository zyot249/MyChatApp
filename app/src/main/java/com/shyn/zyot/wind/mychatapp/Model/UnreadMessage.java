package com.shyn.zyot.wind.mychatapp.Model;

public class UnreadMessage {
    private int unread;

    public UnreadMessage(int unread) {
        this.unread = unread;
    }

    public UnreadMessage() {
    }

    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }
}
