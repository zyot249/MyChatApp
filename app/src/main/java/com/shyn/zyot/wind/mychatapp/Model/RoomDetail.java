package com.shyn.zyot.wind.mychatapp.Model;

import java.util.List;

public class RoomDetail {
    private String roomID;
    private List<String> memberIDs;
    private String lastMsgID;

    public RoomDetail() {
    }

    public RoomDetail(String roomID, List<String> memberIDs, String lastMsgID) {
        this.roomID = roomID;
        this.memberIDs = memberIDs;
        this.lastMsgID = lastMsgID;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public List<String> getMemberIDs() {
        return memberIDs;
    }

    public void setMemberIDs(List<String> memberIDs) {
        this.memberIDs = memberIDs;
    }

    public String getLastMsgID() {
        return lastMsgID;
    }

    public void setLastMsgID(String lastMsgID) {
        this.lastMsgID = lastMsgID;
    }
}
