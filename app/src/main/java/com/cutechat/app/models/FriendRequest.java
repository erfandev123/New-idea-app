package com.cutechat.app.models;

import java.util.HashMap;
import java.util.Map;

public class FriendRequest {
    private String id;
    private String senderId;
    private String receiverId;
    private String status; // pending, accepted, rejected
    private long timestamp;

    public FriendRequest() {
        // Default constructor required for Firestore
    }

    public FriendRequest(String id, String senderId, String receiverId) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.status = "pending";
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("senderId", senderId);
        map.put("receiverId", receiverId);
        map.put("status", status);
        map.put("timestamp", timestamp);
        return map;
    }

    public static FriendRequest fromMap(Map<String, Object> map) {
        FriendRequest request = new FriendRequest();
        request.setId((String) map.get("id"));
        request.setSenderId((String) map.get("senderId"));
        request.setReceiverId((String) map.get("receiverId"));
        request.setStatus((String) map.get("status"));
        request.setTimestamp((Long) map.get("timestamp"));
        return request;
    }
}