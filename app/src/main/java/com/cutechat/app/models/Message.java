package com.cutechat.app.models;

import java.util.HashMap;
import java.util.Map;

public class Message {
    private String id;
    private String senderId;
    private String receiverId;
    private String text;
    private String imageUrl;
    private String type; // text, image, emoji
    private long timestamp;
    private boolean isRead;
    private boolean isDelivered;

    public Message() {
        // Default constructor required for Firestore
    }

    public Message(String id, String senderId, String receiverId, String text, String type) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
        this.isDelivered = false;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public boolean isDelivered() {
        return isDelivered;
    }

    public void setDelivered(boolean delivered) {
        isDelivered = delivered;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("senderId", senderId);
        map.put("receiverId", receiverId);
        map.put("text", text);
        map.put("imageUrl", imageUrl);
        map.put("type", type);
        map.put("timestamp", timestamp);
        map.put("isRead", isRead);
        map.put("isDelivered", isDelivered);
        return map;
    }

    public static Message fromMap(Map<String, Object> map) {
        Message message = new Message();
        message.setId((String) map.get("id"));
        message.setSenderId((String) map.get("senderId"));
        message.setReceiverId((String) map.get("receiverId"));
        message.setText((String) map.get("text"));
        message.setImageUrl((String) map.get("imageUrl"));
        message.setType((String) map.get("type"));
        message.setTimestamp((Long) map.get("timestamp"));
        message.setRead((Boolean) map.get("isRead"));
        message.setDelivered((Boolean) map.get("isDelivered"));
        return message;
    }
}