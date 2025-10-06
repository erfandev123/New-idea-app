package com.cutechat.app.ui.home;

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
import com.cutechat.app.databinding.FragmentHomeBinding;
import com.cutechat.app.firebase.AuthManager;
import com.cutechat.app.firebase.FirebaseConfig;
import com.cutechat.app.models.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private AuthManager authManager;
    private FriendsAdapter friendsAdapter;
    private List<User> friendsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        authManager = AuthManager.getInstance(requireContext());
        friendsList = new ArrayList<>();
        
        setupUI();
        loadUserProfile();
        loadFriends();
    }

    private void setupUI() {
        // Setup friends recycler view
        friendsAdapter = new FriendsAdapter(friendsList, this::onFriendClick);
        binding.recyclerViewFriends.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewFriends.setAdapter(friendsAdapter);
        
        // Setup search
        binding.etSearchFriends.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchFriends(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    loadFriends();
                } else {
                    searchFriends(newText);
                }
                return true;
            }
        });
    }

    private void loadUserProfile() {
        if (authManager.getCurrentUser() != null) {
            String userName = authManager.getCurrentUser().getDisplayName();
            if (userName == null) {
                userName = "User";
            }
            binding.tvWelcome.setText("Hello, " + userName + "! 👋");
        }
    }

    private void loadFriends() {
        // TODO: Implement friends loading from Firestore
        // For now, show empty state
        binding.tvEmptyState.setVisibility(View.VISIBLE);
        binding.recyclerViewFriends.setVisibility(View.GONE);
    }

    private void searchFriends(String query) {
        // TODO: Implement friend search
        Toast.makeText(requireContext(), "Searching for: " + query, Toast.LENGTH_SHORT).show();
    }

    private void onFriendClick(User friend) {
        // Open chat with friend
        if (getActivity() instanceof com.cutechat.app.ui.main.MainActivity) {
            ((com.cutechat.app.ui.main.MainActivity) getActivity()).openChat(friend.getUid(), friend.getName());
        }
    }
}