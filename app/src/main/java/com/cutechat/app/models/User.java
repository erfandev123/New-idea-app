package com.cutechat.app.models;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String userId;
    private String username;
    private String email;
    private String profileImageUrl;
    private String status;
    private boolean online;
    private long lastSeen;
    private String fcmToken;

    public User() {
        // Required empty constructor for Firebase
    }

    public User(String userId, String username, String email, String profileImageUrl) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.status = "Hey there! I'm using CuteChat";
        this.online = false;
        this.lastSeen = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("username", username);
        result.put("email", email);
        result.put("profileImageUrl", profileImageUrl);
        result.put("status", status);
        result.put("online", online);
        result.put("lastSeen", lastSeen);
        result.put("fcmToken", fcmToken);
        return result;
    }
}
