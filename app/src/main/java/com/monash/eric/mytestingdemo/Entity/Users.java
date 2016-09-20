package com.monash.eric.mytestingdemo.Entity;

import java.io.Serializable;

/**
 * Created by Anthony on 6/09/2016.
 */
@SuppressWarnings("serial") //With this annotation we are going to hide compiler warnings
public class Users implements Serializable{


    private String user_id;
    private String username;
    private String gender;
    private String birthday;
    private String country;
    private String sports;

    public Users(){

    }

    public String displayAll(){

        return "Users{" +
                "username='" + username + '\'' +
                ", gender='" + gender + '\'' +
                ", birthday='" + birthday + '\'' +
                ", country='" + country + '\'' +
                ", user_id='" + user_id + '\'' +
                ", sports='" + sports + '\'' +
                '}';
    }

    public Users(String username, String gender, String birthday, String country, String sports, String user_id) {
        this.username = username;
        this.gender = gender;
        this.birthday = birthday;
        this.country = country;
        this.sports = sports;
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSports() {
        return sports;
    }

    public void setSports(String sports) {
        this.sports = sports;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "Users{" +
                "user_id='" + user_id + '\'' +
                ", username='" + username + '\'' +
                ", gender='" + gender + '\'' +
                ", birthday='" + birthday + '\'' +
                ", country='" + country + '\'' +
                ", sports='" + sports + '\'' +
                '}';
    }
}
