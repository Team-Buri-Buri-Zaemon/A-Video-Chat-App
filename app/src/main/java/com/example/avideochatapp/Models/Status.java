package com.example.avideochatapp.Models;

public class Status {
    private String userId;
    private String userName;
    private String imageUrl;
    private long statusTime;

    public Status() {
        // Required empty constructor for Firebase
    }

    public Status(String userId, String userName, String imageUrl, long statusTime) {
        this.userId = userId;
        this.userName = userName;
        this.imageUrl = imageUrl;
        this.statusTime = statusTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getStatusTime() {
        return statusTime;
    }

    public void setStatusTime(long statusTime) {
        this.statusTime = statusTime;
    }
}


