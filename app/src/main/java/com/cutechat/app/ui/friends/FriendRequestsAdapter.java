package com.cutechat.app.ui.friends;

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

public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.RequestViewHolder> {

    private List<User> requestsList;
    private OnRequestActionListener listener;

    public interface OnRequestActionListener {
        void onRequestAction(User user, String action);
    }

    public FriendRequestsAdapter(List<User> requestsList, OnRequestActionListener listener) {
        this.requestsList = requestsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        User user = requestsList.get(position);
        holder.bind(user, listener);
    }

    @Override
    public int getItemCount() {
        return requestsList.size();
    }

    public void updateRequestsList(List<User> newRequestsList) {
        this.requestsList = newRequestsList;
        notifyDataSetChanged();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView ivProfile;
        private TextView tvName;
        private TextView tvStatus;
        private TextView btnAccept;
        private TextView btnReject;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.iv_profile);
            tvName = itemView.findViewById(R.id.tv_name);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnAccept = itemView.findViewById(R.id.btn_accept);
            btnReject = itemView.findViewById(R.id.btn_reject);
        }

        public void bind(User user, OnRequestActionListener listener) {
            tvName.setText(user.getName());
            tvStatus.setText("Wants to be your friend");
            
            // Load profile image
            if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(user.getProfileImageUrl())
                        .placeholder(R.drawable.ic_person)
                        .error(R.drawable.ic_person)
                        .into(ivProfile);
            } else {
                ivProfile.setImageResource(R.drawable.ic_person);
            }
            
            // Set click listeners
            btnAccept.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRequestAction(user, "accept");
                }
            });
            
            btnReject.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRequestAction(user, "reject");
                }
            });
        }
    }
}