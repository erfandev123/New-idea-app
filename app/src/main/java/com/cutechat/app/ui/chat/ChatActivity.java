package com.cutechat.app.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cutechat.app.R;
import com.cutechat.app.databinding.ActivityChatBinding;
import com.cutechat.app.firebase.AuthManager;
import com.cutechat.app.firebase.FirebaseConfig;
import com.cutechat.app.models.Message;
import com.cutechat.app.ui.call.CallActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityChatBinding binding;
    private AuthManager authManager;
    private MessagesAdapter messagesAdapter;
    private List<Message> messagesList;
    private String userId;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get user info from intent
        userId = getIntent().getStringExtra("userId");
        userName = getIntent().getStringExtra("userName");

        if (userId == null || userName == null) {
            Toast.makeText(this, "Invalid user", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        authManager = AuthManager.getInstance(this);
        messagesList = new ArrayList<>();

        setupUI();
        loadMessages();
    }

    private void setupUI() {
        // Setup toolbar
        binding.toolbar.setTitle(userName);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        // Setup messages recycler view
        messagesAdapter = new MessagesAdapter(messagesList, authManager.getCurrentUser().getUid());
        binding.recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewMessages.setAdapter(messagesAdapter);

        // Setup click listeners
        binding.btnSend.setOnClickListener(this);
        binding.btnCall.setOnClickListener(this);
        binding.btnVideoCall.setOnClickListener(this);
        binding.btnAddEmoji.setOnClickListener(this);
        binding.btnAddImage.setOnClickListener(this);
    }

    private void loadMessages() {
        // TODO: Load messages from Firestore
        // For now, show empty state
        binding.tvEmptyState.setVisibility(View.VISIBLE);
        binding.recyclerViewMessages.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_send) {
            sendMessage();
        } else if (v.getId() == R.id.btn_call) {
            startCall(false);
        } else if (v.getId() == R.id.btn_video_call) {
            startCall(true);
        } else if (v.getId() == R.id.btn_add_emoji) {
            showEmojiPicker();
        } else if (v.getId() == R.id.btn_add_image) {
            showImagePicker();
        }
    }

    private void sendMessage() {
        String messageText = binding.etMessage.getText().toString().trim();
        if (TextUtils.isEmpty(messageText)) {
            return;
        }

        // TODO: Send message to Firestore
        binding.etMessage.setText("");
        Toast.makeText(this, "Message sent: " + messageText, Toast.LENGTH_SHORT).show();
    }

    private void startCall(boolean isVideoCall) {
        Intent intent = new Intent(this, CallActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("userName", userName);
        intent.putExtra("isVideoCall", isVideoCall);
        intent.putExtra("isIncoming", false);
        startActivity(intent);
    }

    private void showEmojiPicker() {
        // TODO: Show emoji picker
        Toast.makeText(this, "Emoji picker", Toast.LENGTH_SHORT).show();
    }

    private void showImagePicker() {
        // TODO: Show image picker
        Toast.makeText(this, "Image picker", Toast.LENGTH_SHORT).show();
    }
}