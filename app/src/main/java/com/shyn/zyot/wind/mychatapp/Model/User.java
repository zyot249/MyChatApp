package com.shyn.zyot.wind.mychatapp.Model;

import java.util.HashMap;

public class User {
    private String id;
    private String username;
    private String imageUrl;
    private String status;
    private String search;
//    private HashMap<String, String> chatedUser;

    public User(String id, String username, String imageUrl, String status, String search) {
        this.id = id;
        this.username = username;
        this.imageUrl = imageUrl;
        this.status = status;
        this.search = search;
    }


//    public User(String id, String username, String imageUrl, String status, String search, HashMap<String, String> chatedUser) {
//        this.id = id;
//        this.username = username;
//        this.imageUrl = imageUrl;
//        this.status = status;
//        this.search = search;
//        this.chatedUser = chatedUser;
//    }

    public User() {
//        chatedUser = new HashMap<>();
    }

//    public HashMap<String, String> getChatedUser() {
//        return chatedUser;
//    }
//
//    public void setChatedUser(HashMap<String, String> chatedUser) {
//        this.chatedUser = chatedUser;
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
