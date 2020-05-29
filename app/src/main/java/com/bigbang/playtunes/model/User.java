package com.bigbang.playtunes.model;

import com.google.firebase.database.Exclude;

public class User {

    private String userName;
    private String userPassword;
    private String userKey;

    public User() {
    }

    public User(String userName, String userPassword) {
        this.userName = userName;
        this.userPassword = userPassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    @Exclude
    public String getUserKey() {
        return userKey;
    }

    @Exclude
    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }
}
