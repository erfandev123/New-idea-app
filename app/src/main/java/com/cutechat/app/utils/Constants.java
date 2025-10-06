package com.cutechat.app.utils;

public class Constants {
    
    // Firebase Database References
    public static final String REF_USERS = "users";
    public static final String REF_MESSAGES = "messages";
    public static final String REF_FRIEND_REQUESTS = "friend_requests";
    public static final String REF_FRIENDS = "friends";
    public static final String REF_CALLS = "calls";
    public static final String REF_TYPING = "typing";
    public static final String REF_PRESENCE = "presence";
    
    // Intent Extras
    public static final String EXTRA_USER_ID = "user_id";
    public static final String EXTRA_USERNAME = "username";
    public static final String EXTRA_USER_IMAGE = "user_image";
    public static final String EXTRA_CALL_TYPE = "call_type";
    public static final String EXTRA_CALL_ID = "call_id";
    public static final String EXTRA_IS_INCOMING = "is_incoming";
    
    // Shared Preferences
    public static final String PREF_NAME = "CuteChatPrefs";
    public static final String PREF_USER_ID = "user_id";
    public static final String PREF_USERNAME = "username";
    public static final String PREF_EMAIL = "email";
    public static final String PREF_PROFILE_IMAGE = "profile_image";
    public static final String PREF_FCM_TOKEN = "fcm_token";
    public static final String PREF_THEME = "theme";
    
    // WebRTC
    public static final String[] STUN_SERVERS = {
        "stun:stun.l.google.com:19302",
        "stun:stun1.l.google.com:19302"
    };
    
    // Request Codes
    public static final int REQUEST_CAMERA_PERMISSION = 100;
    public static final int REQUEST_AUDIO_PERMISSION = 101;
    public static final int REQUEST_STORAGE_PERMISSION = 102;
    public static final int REQUEST_IMAGE_PICK = 103;
    public static final int REQUEST_NOTIFICATION_PERMISSION = 104;
    
    // Message Types
    public static final String MESSAGE_TYPE_TEXT = "text";
    public static final String MESSAGE_TYPE_IMAGE = "image";
    public static final String MESSAGE_TYPE_EMOJI = "emoji";
    
    // Online Status
    public static final String STATUS_ONLINE = "online";
    public static final String STATUS_OFFLINE = "offline";
    public static final String STATUS_TYPING = "typing";
    
    // Theme
    public static final String THEME_LIGHT = "light";
    public static final String THEME_DARK = "dark";
    public static final String THEME_SYSTEM = "system";
}
