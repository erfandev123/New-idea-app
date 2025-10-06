package com.cutechat.app.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cutechat.app.ui.call.CallActivity;

public class CallNotificationReceiver extends BroadcastReceiver {

    public static final String ACTION_ANSWER_CALL = "com.cutechat.app.ANSWER_CALL";
    public static final String ACTION_DECLINE_CALL = "com.cutechat.app.DECLINE_CALL";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        
        if (ACTION_ANSWER_CALL.equals(action)) {
            // Answer the call
            Intent callIntent = new Intent(context, CallActivity.class);
            callIntent.putExtra("userId", intent.getStringExtra("userId"));
            callIntent.putExtra("userName", intent.getStringExtra("userName"));
            callIntent.putExtra("isVideoCall", intent.getBooleanExtra("isVideoCall", false));
            callIntent.putExtra("isIncoming", true);
            callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(callIntent);
        } else if (ACTION_DECLINE_CALL.equals(action)) {
            // Decline the call
            // TODO: Send decline signal to caller
        }
    }
}