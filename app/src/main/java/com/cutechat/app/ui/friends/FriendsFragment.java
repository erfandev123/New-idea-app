package com.cutechat.app.ui.friends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cutechat.app.R;
import com.cutechat.app.databinding.FragmentFriendsBinding;
import com.cutechat.app.firebase.AuthManager;
import com.cutechat.app.models.User;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {

    private FragmentFriendsBinding binding;
    private AuthManager authManager;
    private FriendsListAdapter friendsAdapter;
    private FriendRequestsAdapter requestsAdapter;
    private List<User> friendsList;
    private List<User> requestsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFriendsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        authManager = AuthManager.getInstance(requireContext());
        friendsList = new ArrayList<>();
        requestsList = new ArrayList<>();
        
        setupUI();
        loadFriends();
        loadFriendRequests();
    }

    private void setupUI() {
        // Setup friends recycler view
        friendsAdapter = new FriendsListAdapter(friendsList, this::onFriendClick);
        binding.recyclerViewFriends.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewFriends.setAdapter(friendsAdapter);
        
        // Setup requests recycler view
        requestsAdapter = new FriendRequestsAdapter(requestsList, this::onRequestAction);
        binding.recyclerViewRequests.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewRequests.setAdapter(requestsAdapter);
        
        // Setup tab layout
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("My Friends"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Requests"));
        
        binding.tabLayout.addOnTabSelectedListener(new com.google.android.material.tabs.TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(com.google.android.material.tabs.TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    binding.recyclerViewFriends.setVisibility(View.VISIBLE);
                    binding.recyclerViewRequests.setVisibility(View.GONE);
                } else {
                    binding.recyclerViewFriends.setVisibility(View.GONE);
                    binding.recyclerViewRequests.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(com.google.android.material.tabs.TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(com.google.android.material.tabs.TabLayout.Tab tab) {}
        });
    }

    private void loadFriends() {
        // TODO: Implement friends loading from Firestore
        // For now, show empty state
        binding.tvEmptyFriends.setVisibility(View.VISIBLE);
        binding.recyclerViewFriends.setVisibility(View.GONE);
    }

    private void loadFriendRequests() {
        // TODO: Implement friend requests loading from Firestore
        // For now, show empty state
        binding.tvEmptyRequests.setVisibility(View.VISIBLE);
        binding.recyclerViewRequests.setVisibility(View.GONE);
    }

    private void onFriendClick(User friend) {
        // Open chat with friend
        if (getActivity() instanceof com.cutechat.app.ui.main.MainActivity) {
            ((com.cutechat.app.ui.main.MainActivity) getActivity()).openChat(friend.getUid(), friend.getName());
        }
    }

    private void onRequestAction(User user, String action) {
        if ("accept".equals(action)) {
            // TODO: Accept friend request
            Toast.makeText(requireContext(), "Friend request accepted!", Toast.LENGTH_SHORT).show();
        } else if ("reject".equals(action)) {
            // TODO: Reject friend request
            Toast.makeText(requireContext(), "Friend request rejected", Toast.LENGTH_SHORT).show();
        }
    }
}