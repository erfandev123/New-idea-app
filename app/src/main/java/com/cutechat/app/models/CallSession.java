package com.cutechat.app.models;

import java.util.HashMap;
import java.util.Map;

public class CallSession {
    private String id;
    private String callerId;
    private String receiverId;
    private String type; // audio, video
    private String status; // calling, ringing, connected, ended, missed, rejected
    private long startTime;
    private long endTime;
    private long duration;

    public CallSession() {
        // Default constructor required for Firestore
    }

    public CallSession(String id, String callerId, String receiverId, String type) {
        this.id = id;
        this.callerId = callerId;
        this.receiverId = receiverId;
        this.type = type;
        this.status = "calling";
        this.startTime = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCallerId() {
        return callerId;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("callerId", callerId);
        map.put("receiverId", receiverId);
        map.put("type", type);
        map.put("status", status);
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        map.put("duration", duration);
        return map;
    }

    public static CallSession fromMap(Map<String, Object> map) {
        CallSession session = new CallSession();
        session.setId((String) map.get("id"));
        session.setCallerId((String) map.get("callerId"));
        session.setReceiverId((String) map.get("receiverId"));
        session.setType((String) map.get("type"));
        session.setStatus((String) map.get("status"));
        session.setStartTime((Long) map.get("startTime"));
        session.setEndTime((Long) map.get("endTime"));
        session.setDuration((Long) map.get("duration"));
        return session;
    }
}