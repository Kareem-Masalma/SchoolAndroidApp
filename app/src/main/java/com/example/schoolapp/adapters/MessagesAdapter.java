package com.example.schoolapp.adapters;// File: MessagesAdapter.java


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolapp.R;
import com.example.schoolapp.models.AiMessage;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_ASSISTANT = 2;
    private static final int VIEW_TYPE_TYPING = 3;

    private final List<AiMessage> messageList;

    public MessagesAdapter(List<AiMessage> messageList) {
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        AiMessage msg = messageList.get(position);
        if (msg.isTypingIndicator()) {
            return VIEW_TYPE_TYPING;
        } else if (msg.isUser()) {
            return VIEW_TYPE_USER;
        } else if (msg.isAssistant()) {
            return VIEW_TYPE_ASSISTANT;
        } else {

            return VIEW_TYPE_ASSISTANT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_USER) {
            View view = inflater.inflate(R.layout.user_message, parent, false);
            return new UserViewHolder(view);
        } else if (viewType == VIEW_TYPE_ASSISTANT) {
            View view = inflater.inflate(R.layout.ai_message, parent, false);
            return new BotViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_typing, parent, false);
            return new TypingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AiMessage msg = messageList.get(position);
        if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).tvMessage.setText(msg.getContent());

        } else if (holder instanceof BotViewHolder) {
            ((BotViewHolder) holder).tvMessage.setText(msg.getContent());

        } else if (holder instanceof TypingViewHolder) {

        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;


        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tv_user_msg);

        }
    }


    static class BotViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;


        public BotViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tv_message_bot);
            // tvTimestamp = itemView.findViewById(R.id.tv_timestamp_bot);
        }
    }


    static class TypingViewHolder extends RecyclerView.ViewHolder {

        public TypingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
