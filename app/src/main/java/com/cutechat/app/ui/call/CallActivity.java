package com.cutechat.app.ui.call;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cutechat.app.R;
import com.cutechat.app.databinding.ActivityCallBinding;
import com.cutechat.app.firebase.AuthManager;
import com.cutechat.app.webrtc.WebRTCManager;

import org.webrtc.EglBase;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;

import java.util.concurrent.TimeUnit;

public class CallActivity extends AppCompatActivity implements View.OnClickListener, WebRTCManager.CallListener {

    private static final int PERMISSION_REQUEST_CODE = 1001;
    private ActivityCallBinding binding;
    private AuthManager authManager;
    private WebRTCManager webRTCManager;
    private MediaPlayer ringtonePlayer;
    private Handler callTimerHandler;
    private Runnable callTimerRunnable;
    private long callStartTime;
    private boolean isVideoCall;
    private boolean isIncoming;
    private String userId;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Keep screen on during call
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        binding = ActivityCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get call info from intent
        userId = getIntent().getStringExtra("userId");
        userName = getIntent().getStringExtra("userName");
        isVideoCall = getIntent().getBooleanExtra("isVideoCall", false);
        isIncoming = getIntent().getBooleanExtra("isIncoming", false);

        if (userId == null || userName == null) {
            Toast.makeText(this, "Invalid call", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        authManager = AuthManager.getInstance(this);
        webRTCManager = WebRTCManager.getInstance(this);
        webRTCManager.setCallListener(this);

        setupUI();
        checkPermissions();
    }

    private void setupUI() {
        // Setup toolbar
        binding.toolbar.setTitle(userName);
        binding.toolbar.setNavigationOnClickListener(v -> endCall());

        // Setup click listeners
        binding.btnMute.setOnClickListener(this);
        binding.btnSpeaker.setOnClickListener(this);
        binding.btnVideo.setOnClickListener(this);
        binding.btnEndCall.setOnClickListener(this);
        binding.btnAnswer.setOnClickListener(this);
        binding.btnDecline.setOnClickListener(this);

        // Setup video views
        binding.localVideoView.init(webRTCManager.getEglBase().getEglBaseContext(), null);
        binding.localVideoView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
        binding.localVideoView.setMirror(true);

        binding.remoteVideoView.init(webRTCManager.getEglBase().getEglBaseContext(), null);
        binding.remoteVideoView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);

        // Show appropriate UI based on call type and state
        if (isIncoming) {
            showIncomingCallUI();
            playRingtone();
        } else {
            showOutgoingCallUI();
            startCall();
        }

        // Setup call timer
        callTimerHandler = new Handler();
    }

    private void checkPermissions() {
        String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.MODIFY_AUDIO_SETTINGS
        };

        boolean allPermissionsGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }

        if (!allPermissionsGranted) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                if (isIncoming) {
                    showIncomingCallUI();
                } else {
                    startCall();
                }
            } else {
                Toast.makeText(this, "Permissions required for call", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void showIncomingCallUI() {
        binding.layoutIncomingCall.setVisibility(View.VISIBLE);
        binding.layoutCallControls.setVisibility(View.GONE);
        binding.layoutOutgoingCall.setVisibility(View.GONE);
        
        if (isVideoCall) {
            binding.tvCallType.setText("Incoming Video Call");
            binding.ivCallType.setImageResource(R.drawable.ic_videocam);
        } else {
            binding.tvCallType.setText("Incoming Audio Call");
            binding.ivCallType.setImageResource(R.drawable.ic_call);
        }
    }

    private void showOutgoingCallUI() {
        binding.layoutOutgoingCall.setVisibility(View.VISIBLE);
        binding.layoutCallControls.setVisibility(View.GONE);
        binding.layoutIncomingCall.setVisibility(View.GONE);
        
        if (isVideoCall) {
            binding.tvCallType.setText("Calling...");
            binding.ivCallType.setImageResource(R.drawable.ic_videocam);
        } else {
            binding.tvCallType.setText("Calling...");
            binding.ivCallType.setImageResource(R.drawable.ic_call);
        }
    }

    private void showCallConnectedUI() {
        binding.layoutCallControls.setVisibility(View.VISIBLE);
        binding.layoutIncomingCall.setVisibility(View.GONE);
        binding.layoutOutgoingCall.setVisibility(View.GONE);
        
        // Show video views for video calls
        if (isVideoCall) {
            binding.localVideoView.setVisibility(View.VISIBLE);
            binding.remoteVideoView.setVisibility(View.VISIBLE);
            binding.ivProfile.setVisibility(View.GONE);
        } else {
            binding.localVideoView.setVisibility(View.GONE);
            binding.remoteVideoView.setVisibility(View.GONE);
            binding.ivProfile.setVisibility(View.VISIBLE);
        }
        
        startCallTimer();
    }

    private void startCall() {
        webRTCManager.startCall(userId, isVideoCall);
    }

    private void playRingtone() {
        try {
            Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            ringtonePlayer = MediaPlayer.create(this, ringtoneUri);
            ringtonePlayer.setLooping(true);
            ringtonePlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopRingtone() {
        if (ringtonePlayer != null) {
            ringtonePlayer.stop();
            ringtonePlayer.release();
            ringtonePlayer = null;
        }
    }

    private void startCallTimer() {
        callStartTime = System.currentTimeMillis();
        callTimerRunnable = new Runnable() {
            @Override
            public void run() {
                long duration = System.currentTimeMillis() - callStartTime;
                String timeString = formatDuration(duration);
                binding.tvCallDuration.setText(timeString);
                callTimerHandler.postDelayed(this, 1000);
            }
        };
        callTimerHandler.post(callTimerRunnable);
    }

    private String formatDuration(long duration) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % 60;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) % 60;
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_mute) {
            toggleMute();
        } else if (v.getId() == R.id.btn_speaker) {
            toggleSpeaker();
        } else if (v.getId() == R.id.btn_video) {
            toggleVideo();
        } else if (v.getId() == R.id.btn_end_call) {
            endCall();
        } else if (v.getId() == R.id.btn_answer) {
            answerCall();
        } else if (v.getId() == R.id.btn_decline) {
            declineCall();
        }
    }

    private void toggleMute() {
        // TODO: Implement mute toggle
        Toast.makeText(this, "Mute toggled", Toast.LENGTH_SHORT).show();
    }

    private void toggleSpeaker() {
        // TODO: Implement speaker toggle
        Toast.makeText(this, "Speaker toggled", Toast.LENGTH_SHORT).show();
    }

    private void toggleVideo() {
        if (isVideoCall) {
            // TODO: Implement video toggle
            Toast.makeText(this, "Video toggled", Toast.LENGTH_SHORT).show();
        }
    }

    private void answerCall() {
        stopRingtone();
        webRTCManager.answerCall();
        showCallConnectedUI();
    }

    private void declineCall() {
        stopRingtone();
        webRTCManager.declineCall();
        finish();
    }

    private void endCall() {
        stopRingtone();
        if (callTimerRunnable != null) {
            callTimerHandler.removeCallbacks(callTimerRunnable);
        }
        webRTCManager.endCall();
        finish();
    }

    @Override
    public void onCallConnected() {
        runOnUiThread(() -> {
            showCallConnectedUI();
        });
    }

    @Override
    public void onCallEnded() {
        runOnUiThread(() -> {
            finish();
        });
    }

    @Override
    public void onCallFailed(String error) {
        runOnUiThread(() -> {
            Toast.makeText(this, "Call failed: " + error, Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRingtone();
        if (callTimerRunnable != null) {
            callTimerHandler.removeCallbacks(callTimerRunnable);
        }
        if (webRTCManager != null) {
            webRTCManager.cleanup();
        }
    }
}