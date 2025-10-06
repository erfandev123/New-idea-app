package com.cutechat.app.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cutechat.app.R;
import com.cutechat.app.databinding.FragmentSettingsBinding;
import com.cutechat.app.firebase.AuthManager;
import com.cutechat.app.ui.auth.AuthActivity;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private FragmentSettingsBinding binding;
    private AuthManager authManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        authManager = AuthManager.getInstance(requireContext());
        
        setupUI();
        loadUserProfile();
    }

    private void setupUI() {
        // Setup click listeners
        binding.layoutEditProfile.setOnClickListener(this);
        binding.layoutTheme.setOnClickListener(this);
        binding.layoutNotifications.setOnClickListener(this);
        binding.layoutAbout.setOnClickListener(this);
        binding.btnLogout.setOnClickListener(this);
        
        // Setup profile image click
        binding.ivProfile.setOnClickListener(this);
    }

    private void loadUserProfile() {
        if (authManager.getCurrentUser() != null) {
            String userName = authManager.getCurrentUser().getDisplayName();
            String userEmail = authManager.getCurrentUser().getEmail();
            
            if (userName != null) {
                binding.tvName.setText(userName);
            }
            if (userEmail != null) {
                binding.tvEmail.setText(userEmail);
            }
            
            // Load profile image
            if (authManager.getCurrentUser().getPhotoUrl() != null) {
                // TODO: Load profile image using Glide
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.layout_edit_profile) {
            openEditProfile();
        } else if (v.getId() == R.id.layout_theme) {
            toggleTheme();
        } else if (v.getId() == R.id.layout_notifications) {
            openNotifications();
        } else if (v.getId() == R.id.layout_about) {
            openAbout();
        } else if (v.getId() == R.id.btn_logout) {
            logout();
        } else if (v.getId() == R.id.iv_profile) {
            openEditProfile();
        }
    }

    private void openEditProfile() {
        // TODO: Open edit profile dialog
        Toast.makeText(requireContext(), "Edit Profile", Toast.LENGTH_SHORT).show();
    }

    private void toggleTheme() {
        // TODO: Toggle between light and dark theme
        Toast.makeText(requireContext(), "Theme Toggle", Toast.LENGTH_SHORT).show();
    }

    private void openNotifications() {
        // TODO: Open notifications settings
        Toast.makeText(requireContext(), "Notifications Settings", Toast.LENGTH_SHORT).show();
    }

    private void openAbout() {
        // TODO: Open about dialog
        Toast.makeText(requireContext(), "About CuteChat", Toast.LENGTH_SHORT).show();
    }

    private void logout() {
        authManager.signOut(new AuthManager.AuthCallback() {
            @Override
            public void onSuccess() {
                startActivity(new Intent(requireContext(), AuthActivity.class));
                requireActivity().finish();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(requireContext(), "Logout failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}