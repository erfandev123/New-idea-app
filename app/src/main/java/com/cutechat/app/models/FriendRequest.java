package com.cutechat.app.models;

public class FriendRequest {
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_ACCEPTED = 1;
    public static final int STATUS_REJECTED = 2;

    private String requestId;
    private String senderId;
    private String receiverId;
    private int status;
    private long timestamp;
    
    // Additional fields for display
    private String senderName;
    private String senderImageUrl;

    public FriendRequest() {
        // Required empty constructor for Firebase
    }

    public FriendRequest(String senderId, String receiverId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.status = STATUS_PENDING;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderImageUrl() {
        return senderImageUrl;
    }

    public void setSenderImageUrl(String senderImageUrl) {
        this.senderImageUrl = senderImageUrl;
    }
}
