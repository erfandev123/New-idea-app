package com.cutechat.app.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.cutechat.app.R;
import com.cutechat.app.databinding.ActivityMainBinding;
import com.cutechat.app.firebase.AuthManager;
import com.cutechat.app.ui.auth.AuthActivity;
import com.cutechat.app.ui.chat.ChatActivity;
import com.cutechat.app.ui.friends.FriendsFragment;
import com.cutechat.app.ui.home.HomeFragment;
import com.cutechat.app.ui.settings.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authManager = AuthManager.getInstance(this);

        // Check if user is logged in
        if (!authManager.isUserLoggedIn()) {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
            return;
        }

        setupUI();
        setupNavigation();
        loadFragment(new HomeFragment());
    }

    private void setupUI() {
        // Setup floating action button
        binding.fabAddFriend.setOnClickListener(v -> {
            // Open add friend dialog or activity
            showAddFriendDialog();
        });
    }

    private void setupNavigation() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        
        if (item.getItemId() == R.id.nav_home) {
            fragment = new HomeFragment();
        } else if (item.getItemId() == R.id.nav_friends) {
            fragment = new FriendsFragment();
        } else if (item.getItemId() == R.id.nav_chats) {
            // Open chats list or recent chats
            openChats();
            return true;
        } else if (item.getItemId() == R.id.nav_calls) {
            // Open calls history
            openCalls();
            return true;
        } else if (item.getItemId() == R.id.nav_settings) {
            fragment = new SettingsFragment();
        }

        if (fragment != null) {
            loadFragment(fragment);
            return true;
        }

        return false;
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void showAddFriendDialog() {
        // TODO: Implement add friend dialog
        // For now, just show a placeholder
    }

    private void openChats() {
        // TODO: Implement chats list
        // For now, just show a placeholder
    }

    private void openCalls() {
        // TODO: Implement calls history
        // For now, just show a placeholder
    }

    public void openChat(String userId, String userName) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("userName", userName);
        startActivity(intent);
    }
}