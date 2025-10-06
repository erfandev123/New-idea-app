package com.cutechat.app.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.cutechat.app.CuteChatApplication;
import com.cutechat.app.R;
import com.cutechat.app.ui.call.CallActivity;
import com.cutechat.app.ui.chat.ChatActivity;
import com.cutechat.app.ui.main.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Handle data payload
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            handleDataMessage(remoteMessage.getData());
        }

        // Handle notification payload
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotificationMessage(remoteMessage);
        }
    }

    private void handleDataMessage(Map<String, String> data) {
        String type = data.get("type");
        
        if ("call".equals(type)) {
            handleCallNotification(data);
        } else if ("message".equals(type)) {
            handleMessageNotification(data);
        } else if ("friend_request".equals(type)) {
            handleFriendRequestNotification(data);
        }
    }

    private void handleCallNotification(Map<String, String> data) {
        String callerId = data.get("caller_id");
        String callerName = data.get("caller_name");
        String callType = data.get("call_type");
        boolean isVideoCall = "video".equals(callType);

        Intent intent = new Intent(this, CallActivity.class);
        intent.putExtra("userId", callerId);
        intent.putExtra("userName", callerName);
        intent.putExtra("isVideoCall", isVideoCall);
        intent.putExtra("isIncoming", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        String title = isVideoCall ? "Incoming Video Call" : "Incoming Audio Call";
        String body = callerName + " is calling you";

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CuteChatApplication.CALL_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_call)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setFullScreenIntent(pendingIntent, true)
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, notificationBuilder.build());
    }

    private void handleMessageNotification(Map<String, String> data) {
        String senderId = data.get("sender_id");
        String senderName = data.get("sender_name");
        String message = data.get("message");

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("userId", senderId);
        intent.putExtra("userName", senderName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CuteChatApplication.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_chat)
                .setContentTitle(senderName)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(2, notificationBuilder.build());
    }

    private void handleFriendRequestNotification(Map<String, String> data) {
        String senderName = data.get("sender_name");

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("tab", "friends");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CuteChatApplication.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_person_add)
                .setContentTitle("New Friend Request")
                .setContentText(senderName + " wants to be your friend")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_SOCIAL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(3, notificationBuilder.build());
    }

    private void handleNotificationMessage(RemoteMessage remoteMessage) {
        // Handle notification payload if needed
        // This is called when the app is in the foreground
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);
        
        // TODO: Send token to server
        sendTokenToServer(token);
    }

    private void sendTokenToServer(String token) {
        // TODO: Send FCM token to Firebase Firestore for the current user
    }
}