package com.example.schoolapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.example.schoolapp.adapters.LruBitmapCache;
import com.example.schoolapp.adapters.Message_Inbox_Adapter;
import com.example.schoolapp.data_access.IMessageDA;
import com.example.schoolapp.data_access.MessageDAFactory;
import com.example.schoolapp.json_helpers.LocalDateAdapter;
import com.example.schoolapp.models.Message;
import com.example.schoolapp.models.Registrar;
import com.example.schoolapp.models.Role;
import com.example.schoolapp.models.Student;
import com.example.schoolapp.models.Teacher;
import com.example.schoolapp.models.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Inbox extends AppCompatActivity {

    private TextView tvInboxTitle;
    private RecyclerView recyclerInbox;
    private Message_Inbox_Adapter adapter;
    private ImageLoader imageLoader;
    private List<Message> messages = new ArrayList<>();
    private User logged_in_user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inbox);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setupViews();
        checkPrefs();
        loadMessages();
    }

    private void checkPrefs() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Inbox.this);
        boolean loggedin =  prefs.getBoolean(Login.LOGGED_IN, false);
        if(loggedin){
            String json = prefs.getString(Login.LOGGED_IN_USER , null);
            if(json != null){
                Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
                User user = gson.fromJson(json, User.class);
                logged_in_user = user;
                if(user.getRole().equals(Role.STUDENT)){
                    logged_in_user = gson.fromJson(json, Student.class);
                } else if (user.getRole().equals(Role.TEACHER)) {
                    logged_in_user = gson.fromJson(json, Teacher.class);
                }else{
                    logged_in_user = gson.fromJson(json, Registrar.class);

                }

            }
        }


    }

    private void setupViews() {
        tvInboxTitle = findViewById(R.id.tvInboxTitle);

        recyclerInbox = findViewById(R.id.recyclerInbox);
        recyclerInbox.setLayoutManager(new LinearLayoutManager(this));

        RequestQueue queue = Volley.newRequestQueue(this);
        imageLoader = new ImageLoader(queue, new LruBitmapCache());

        adapter = new Message_Inbox_Adapter(
                this,
                messages,
                imageLoader,
                new Message_Inbox_Adapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Message message) {
                        // can add extra actions
                    }

                    @Override
                    public void onReplyClick(Message message, User user) {

                        Intent intent = new Intent(Inbox.this, UserSendMessage2.class);
                        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
                        String json = gson.toJson(user);
                        intent.putExtra("user",json);
                        startActivity(intent);
                    }
                }
        );
        recyclerInbox.setAdapter(adapter);
    }



    private void refreshList() {
        adapter.notifyDataSetChanged();
    }

    private void loadMessages() {
        IMessageDA messageDA = MessageDAFactory.getMessageDA(this);
        messageDA.getAllMessages(new IMessageDA.MessageListCallback() {
            @Override
            public void onSuccess(List<Message> list) {
                for(Message msg : list){
                    if(msg.getTo_user_id() == logged_in_user.getUser_id()){
                        messages.add(msg);
                    }
                }
                refreshList();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(Inbox.this, "Error Occurred: " + error, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
