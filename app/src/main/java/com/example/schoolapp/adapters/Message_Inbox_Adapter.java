package com.example.schoolapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.schoolapp.R;
import com.example.schoolapp.data_access.IUserDA;
import com.example.schoolapp.data_access.UserDAFactory;
import com.example.schoolapp.models.Message;
import com.example.schoolapp.models.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Message_Inbox_Adapter extends RecyclerView.Adapter<Message_Inbox_Adapter.MessageViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Message message);
        void onReplyClick(Message message, User user);
    }

    private final Context context;
    private final List<Message> messages;
    private final OnItemClickListener listener;
    private final ImageLoader imageLoader;
    private User user = null;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy");

    // track which positions are expanded
    private final Set<Integer> expandedPositions = new HashSet<>();

    public Message_Inbox_Adapter(Context context,
                                 List<Message> messages,
                                 ImageLoader imageLoader,
                                 OnItemClickListener listener) {
        this.context     = context;
        this.messages    = messages;
        this.imageLoader = imageLoader;
        this.listener    = listener;
    }

    @NonNull @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.message_inbox, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message msg = messages.get(position);

        // profile avatar
        String avatarUrl = /* msg.getSenderAvatarUrl() */ null;
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            holder.imgProfile.setImageUrl(avatarUrl, imageLoader);
        } else {
            holder.imgProfile.setDefaultImageResId(R.drawable.profile_icon);
        }

        // TODO get the name of the sender
        IUserDA userDA = UserDAFactory.getUserDA(context.getApplicationContext());
        userDA.getUserById(msg.getFrom_user_id(), new IUserDA.SingleUserCallback() {
            @Override
            public void onSuccess(User a) {
               holder.tvSender.setText(a.getFirstName() + " " + a.getLastName());
                user = a;
            }

            @Override
            public void onError(String error) {
                holder.tvSender.setText("User #" + msg.getFrom_user_id());
            }
        });


        holder.tvSubject.setText(msg.getTitle());
        String snippet = msg.getContent();
        if (snippet.length() > 60) {
            snippet = snippet.substring(0, 57) + "...";
        }
        holder.tvSnippet.setText(snippet);


        holder.tvContent.setText(msg.getContent());
        boolean expanded = expandedPositions.contains(position);
        holder.tvContent.setVisibility(expanded ? View.VISIBLE : View.GONE);

        holder.tvSentDate.setText(msg.getSentDate().format(dateFormatter));

        long days = ChronoUnit.DAYS.between(msg.getSentDate(), LocalDate.now());
        String ago;
        if (days == 0)        ago = "Today";
        else if (days == 1)   ago = "1d ago";
        else                  ago = days + "d ago";
        holder.tvTimeAgo.setText(ago);

        holder.itemView.setOnClickListener(v -> {
            if (expanded) expandedPositions.remove(position);
            else          expandedPositions.add(position);
            notifyItemChanged(position);
            listener.onItemClick(msg);
        });

        holder.btnReply.setOnClickListener(v -> listener.onReplyClick(msg, user));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        NetworkImageView imgProfile;
        TextView tvSender, tvTimeAgo, tvSubject, tvSnippet, tvContent, tvSentDate;
        Button btnReply;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            tvSender   = itemView.findViewById(R.id.tvSender);
            tvTimeAgo  = itemView.findViewById(R.id.tvTimeAgo);
            tvSubject  = itemView.findViewById(R.id.tvSubject);
            tvSnippet  = itemView.findViewById(R.id.tvSnippet);
            tvContent  = itemView.findViewById(R.id.tvContent);
            tvSentDate = itemView.findViewById(R.id.tvSentDate);
            btnReply   = itemView.findViewById(R.id.btnReply);
        }
    }
}
