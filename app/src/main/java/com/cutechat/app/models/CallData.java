package com.cutechat.app.models;

public class CallData {
    public static final String TYPE_AUDIO = "audio";
    public static final String TYPE_VIDEO = "video";
    
    public static final String STATUS_CALLING = "calling";
    public static final String STATUS_RINGING = "ringing";
    public static final String STATUS_ANSWERED = "answered";
    public static final String STATUS_REJECTED = "rejected";
    public static final String STATUS_ENDED = "ended";
    public static final String STATUS_MISSED = "missed";

    private String callId;
    private String callerId;
    private String receiverId;
    private String callerName;
    private String callerImageUrl;
    private String type;
    private String status;
    private long timestamp;
    private String offer;
    private String answer;

    public CallData() {
        // Required empty constructor for Firebase
    }

    public CallData(String callerId, String receiverId, String type) {
        this.callerId = callerId;
        this.receiverId = receiverId;
        this.type = type;
        this.status = STATUS_CALLING;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
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

    public String getCallerName() {
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    public String getCallerImageUrl() {
        return callerImageUrl;
    }

    public void setCallerImageUrl(String callerImageUrl) {
        this.callerImageUrl = callerImageUrl;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
