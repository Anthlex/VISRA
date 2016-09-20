package com.monash.eric.mytestingdemo.Entity;

/**
 * Created by IBM on 15/09/2016.
 */
public class Friends {

    private String friendId;
    private String status;
    private String action_user;
    private String cretaedTime;

    public Friends() {
    }

    public Friends(String friendId, String status, String cretaedTime, String action_user) {
        this.friendId = friendId;
        this.status = status;
        this.cretaedTime = cretaedTime;
        this.action_user = action_user;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAction_user() {
        return action_user;
    }

    public void setAction_user(String action_user) {
        this.action_user = action_user;
    }

    public String getCretaedTime() {
        return cretaedTime;
    }

    public void setCretaedTime(String cretaedTime) {
        this.cretaedTime = cretaedTime;
    }
}
