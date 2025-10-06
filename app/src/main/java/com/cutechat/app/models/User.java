package com.cutechat.app.models;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String uid;
    private String email;
    private String name;
    private String profileImageUrl;
    private String status;
    private long lastSeen;
    private boolean isOnline;
    private String fcmToken;

    public User() {
        // Default constructor required for Firestore
    }

    public User(String uid, String email, String name, String profileImageUrl) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.status = "Hey there! I'm using CuteChat 💕";
        this.lastSeen = System.currentTimeMillis();
        this.isOnline = false;
    }

    // Getters and Setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("uid", uid);
        map.put("email", email);
        map.put("name", name);
        map.put("profileImageUrl", profileImageUrl);
        map.put("status", status);
        map.put("lastSeen", lastSeen);
        map.put("isOnline", isOnline);
        map.put("fcmToken", fcmToken);
        return map;
    }

    public static User fromMap(Map<String, Object> map) {
        User user = new User();
        user.setUid((String) map.get("uid"));
        user.setEmail((String) map.get("email"));
        user.setName((String) map.get("name"));
        user.setProfileImageUrl((String) map.get("profileImageUrl"));
        user.setStatus((String) map.get("status"));
        user.setLastSeen((Long) map.get("lastSeen"));
        user.setOnline((Boolean) map.get("isOnline"));
        user.setFcmToken((String) map.get("fcmToken"));
        return user;
    }
}