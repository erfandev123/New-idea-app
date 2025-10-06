package com.cutechat.app;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.multidex.MultiDex;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class CuteChatApplication extends Application {
    
    public static final String CHANNEL_ID_MESSAGES = "messages_channel";
    public static final String CHANNEL_ID_CALLS = "calls_channel";

    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize MultiDex
        MultiDex.install(this);
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        
        // Enable Firebase offline persistence
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        
        // Create notification channels
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getSystemService(NotificationManager.class);
            
            // Messages Channel
            NotificationChannel messagesChannel = new NotificationChannel(
                CHANNEL_ID_MESSAGES,
                "Messages",
                NotificationManager.IMPORTANCE_HIGH
            );
            messagesChannel.setDescription("New message notifications");
            messagesChannel.enableVibration(true);
            messagesChannel.setShowBadge(true);
            manager.createNotificationChannel(messagesChannel);
            
            // Calls Channel
            NotificationChannel callsChannel = new NotificationChannel(
                CHANNEL_ID_CALLS,
                "Calls",
                NotificationManager.IMPORTANCE_HIGH
            );
            callsChannel.setDescription("Incoming call notifications");
            callsChannel.enableVibration(true);
            callsChannel.setShowBadge(true);
            manager.createNotificationChannel(callsChannel);
        }
    }
}
