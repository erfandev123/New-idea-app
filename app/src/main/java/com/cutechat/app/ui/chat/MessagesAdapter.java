package com.cutechat.app.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cutechat.app.R;
import com.cutechat.app.models.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private List<Message> messagesList;
    private String currentUserId;

    public MessagesAdapter(List<Message> messagesList, String currentUserId) {
        this.messagesList = messagesList;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messagesList.get(position);
        if (message.getSenderId().equals(currentUserId)) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messagesList.get(position);

        if (holder.getItemViewType() == VIEW_TYPE_MESSAGE_SENT) {
            ((SentMessageViewHolder) holder).bind(message);
        } else {
            ((ReceivedMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public void addMessage(Message message) {
        messagesList.add(message);
        notifyItemInserted(messagesList.size() - 1);
    }

    public void updateMessagesList(List<Message> newMessagesList) {
        this.messagesList = newMessagesList;
        notifyDataSetChanged();
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMessage;
        private TextView tvTime;
        private ImageView ivStatus;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tv_message);
            tvTime = itemView.findViewById(R.id.tv_time);
            ivStatus = itemView.findViewById(R.id.iv_status);
        }

        public void bind(Message message) {
            tvMessage.setText(message.getText());
            
            // Format timestamp
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            tvTime.setText(sdf.format(new Date(message.getTimestamp())));
            
            // Set status icon
            if (message.isRead()) {
                ivStatus.setImageResource(R.drawable.ic_done_all);
                ivStatus.setColorFilter(itemView.getContext().getColor(R.color.primary));
            } else if (message.isDelivered()) {
                ivStatus.setImageResource(R.drawable.ic_done_all);
                ivStatus.setColorFilter(itemView.getContext().getColor(R.color.on_surface_variant));
            } else {
                ivStatus.setImageResource(R.drawable.ic_done);
                ivStatus.setColorFilter(itemView.getContext().getColor(R.color.on_surface_variant));
            }
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView ivProfile;
        private TextView tvMessage;
        private TextView tvTime;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.iv_profile);
            tvMessage = itemView.findViewById(R.id.tv_message);
            tvTime = itemView.findViewById(R.id.tv_time);
        }

        public void bind(Message message) {
            tvMessage.setText(message.getText());
            
            // Format timestamp
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            tvTime.setText(sdf.format(new Date(message.getTimestamp())));
            
            // Set profile image (placeholder for now)
            ivProfile.setImageResource(R.drawable.ic_person);
        }
    }
}