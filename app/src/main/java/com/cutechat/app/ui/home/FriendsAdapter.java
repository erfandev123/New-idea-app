package com.cutechat.app.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cutechat.app.R;
import com.cutechat.app.models.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder> {

    private List<User> friendsList;
    private OnFriendClickListener listener;

    public interface OnFriendClickListener {
        void onFriendClick(User friend);
    }

    public FriendsAdapter(List<User> friendsList, OnFriendClickListener listener) {
        this.friendsList = friendsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        User friend = friendsList.get(position);
        holder.bind(friend, listener);
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    public void updateFriendsList(List<User> newFriendsList) {
        this.friendsList = newFriendsList;
        notifyDataSetChanged();
    }

    static class FriendViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView ivProfile;
        private TextView tvName;
        private TextView tvStatus;
        private ImageView ivOnlineStatus;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.iv_profile);
            tvName = itemView.findViewById(R.id.tv_name);
            tvStatus = itemView.findViewById(R.id.tv_status);
            ivOnlineStatus = itemView.findViewById(R.id.iv_online_status);
        }

        public void bind(User friend, OnFriendClickListener listener) {
            tvName.setText(friend.getName());
            tvStatus.setText(friend.getStatus());
            
            // Load profile image
            if (friend.getProfileImageUrl() != null && !friend.getProfileImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(friend.getProfileImageUrl())
                        .placeholder(R.drawable.ic_person)
                        .error(R.drawable.ic_person)
                        .into(ivProfile);
            } else {
                ivProfile.setImageResource(R.drawable.ic_person);
            }
            
            // Set online status
            if (friend.isOnline()) {
                ivOnlineStatus.setVisibility(View.VISIBLE);
                ivOnlineStatus.setImageResource(R.drawable.ic_online);
            } else {
                ivOnlineStatus.setVisibility(View.GONE);
            }
            
            // Set click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFriendClick(friend);
                }
            });
        }
    }
}