package com.cutechat.app.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtils {

    public static boolean hasCameraPermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasAudioPermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasStoragePermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
    }

    public static boolean hasNotificationPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    public static void requestCameraPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.CAMERA},
                Constants.REQUEST_CAMERA_PERMISSION);
    }

    public static void requestAudioPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.RECORD_AUDIO},
                Constants.REQUEST_AUDIO_PERMISSION);
    }

    public static void requestStoragePermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    Constants.REQUEST_STORAGE_PERMISSION);
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    Constants.REQUEST_STORAGE_PERMISSION);
        }
    }

    public static void requestNotificationPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    Constants.REQUEST_NOTIFICATION_PERMISSION);
        }
    }

    public static void requestCallPermissions(Activity activity) {
        List<String> permissions = new ArrayList<>();
        
        if (!hasCameraPermission(activity)) {
            permissions.add(Manifest.permission.CAMERA);
        }
        if (!hasAudioPermission(activity)) {
            permissions.add(Manifest.permission.RECORD_AUDIO);
        }
        
        if (!permissions.isEmpty()) {
            ActivityCompat.requestPermissions(activity,
                    permissions.toArray(new String[0]),
                    Constants.REQUEST_CAMERA_PERMISSION);
        }
    }

    public static boolean hasCallPermissions(Activity activity) {
        return hasCameraPermission(activity) && hasAudioPermission(activity);
    }
}
