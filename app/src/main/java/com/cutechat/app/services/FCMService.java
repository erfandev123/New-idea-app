package com.cutechat.app.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.cutechat.app.CuteChatApplication;
import com.cutechat.app.R;
import com.cutechat.app.activities.ChatActivity;
import com.cutechat.app.activities.IncomingCallActivity;
import com.cutechat.app.utils.Constants;
import com.cutechat.app.utils.FirebaseUtils;
import com.cutechat.app.utils.PreferenceManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FCMService extends FirebaseMessagingService {
    
    private static final String TAG = "FCMService";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "New FCM Token: " + token);
        
        // Save token to preferences
        PreferenceManager preferenceManager = new PreferenceManager(this);
        preferenceManager.putString(Constants.PREF_FCM_TOKEN, token);
        
        // Update token in Firebase if user is logged in
        String userId = preferenceManager.getCurrentUserId();
        if (userId != null) {
            FirebaseUtils.getUsersRef().child(userId).child("fcmToken").setValue(token);
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        
        Log.d(TAG, "Message received from: " + remoteMessage.getFrom());
        
        Map<String, String> data = remoteMessage.getData();
        
        if (!data.isEmpty()) {
            String type = data.get("type");
            
            if ("call".equals(type)) {
                handleIncomingCall(data);
            } else if ("message".equals(type)) {
                handleNewMessage(data);
            }
        }
        
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            showNotification(title, body);
        }
    }

    private void handleIncomingCall(Map<String, String> data) {
        String callerId = data.get("callerId");
        String callerName = data.get("callerName");
        String callerImage = data.get("callerImage");
        String callType = data.get("callType");
        String callId = data.get("callId");

        Intent intent = new Intent(this, IncomingCallActivity.class);
        intent.putExtra(Constants.EXTRA_USER_ID, callerId);
        intent.putExtra(Constants.EXTRA_USERNAME, callerName);
        intent.putExtra(Constants.EXTRA_USER_IMAGE, callerImage);
        intent.putExtra(Constants.EXTRA_CALL_TYPE, callType);
        intent.putExtra(Constants.EXTRA_CALL_ID, callId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        startActivity(intent);
    }

    private void handleNewMessage(Map<String, String> data) {
        String senderId = data.get("senderId");
        String senderName = data.get("senderName");
        String message = data.get("message");

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Constants.EXTRA_USER_ID, senderId);
        intent.putExtra(Constants.EXTRA_USERNAME, senderName);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this, CuteChatApplication.CHANNEL_ID_MESSAGES)
                .setSmallIcon(R.drawable.ic_message)
                .setContentTitle(senderName)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = 
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(senderId.hashCode(), builder.build());
    }

    private void showNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this, CuteChatApplication.CHANNEL_ID_MESSAGES)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = 
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
