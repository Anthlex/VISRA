package com.monash.eric.mytestingdemo.Entity;

/**
 * Created by IBM on 21/09/2016.
 */
public class FriendRow {


    private String friend_id;
    private String friend_name;
    private boolean selected;


    public FriendRow() {
    }

    public FriendRow(String friend_id, String friend_name, boolean selected) {
        this.friend_id = friend_id;
        this.friend_name = friend_name;
        this.selected = selected;
    }

    public String getFriend_id() {
        return friend_id;
    }

    public void setFriend_id(String friend_id) {
        this.friend_id = friend_id;
    }

    public String getFriend_name() {
        return friend_name;
    }

    public void setFriend_name(String friend_name) {
        this.friend_name = friend_name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return "FriendRow{" +
                "friend_id='" + friend_id + '\'' +
                ", friend_name='" + friend_name + '\'' +
                ", selected=" + selected +
                '}';
    }
}
