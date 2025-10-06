package com.cutechat.app;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.google.firebase.FirebaseApp;

public class CuteChatApplication extends Application {
    
    public static final String CHANNEL_ID = "cute_chat_channel";
    public static final String CALL_CHANNEL_ID = "cute_chat_call_channel";
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        
        // Create notification channels
        createNotificationChannels();
    }
    
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            
            // General notifications channel
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Cute Chat Messages",
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for new messages and friend requests");
            notificationManager.createNotificationChannel(channel);
            
            // Call notifications channel
            NotificationChannel callChannel = new NotificationChannel(
                CALL_CHANNEL_ID,
                "Cute Chat Calls",
                NotificationManager.IMPORTANCE_HIGH
            );
            callChannel.setDescription("Notifications for incoming calls");
            callChannel.setSound(null, null);
            callChannel.enableVibration(true);
            notificationManager.createNotificationChannel(callChannel);
        }
    }
}