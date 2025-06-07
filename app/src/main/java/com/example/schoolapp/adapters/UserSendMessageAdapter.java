package com.example.schoolapp.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolapp.R;
import com.example.schoolapp.UserSendMessage2;
import com.example.schoolapp.json_helpers.LocalDateAdapter;
import com.example.schoolapp.models.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.util.List;

public class UserSendMessageAdapter extends RecyclerView.Adapter<UserSendMessageAdapter.UserViewHolder> {

    private List<User> userList;

    public UserSendMessageAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_send_message_row, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.tvUserName2.setText(user.getFirstName() + " " + user.getLastName());
        holder.tvUserId2.setText(String.valueOf(user.getUser_id()));

        holder.btnSelect2.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), UserSendMessage2.class);
            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
            String json = gson.toJson(user);
            intent.putExtra("user", json);
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void updateData(List<User> newList) {
        this.userList = newList;
        notifyDataSetChanged();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView tvUserId2;
        public TextView tvUserName2;
        public Button btnSelect2;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserId2   = itemView.findViewById(R.id.tvUserId2);
            tvUserName2 = itemView.findViewById(R.id.tvUserName2);
            btnSelect2  = itemView.findViewById(R.id.btnSelect2);
        }
    }
}
