package com.example.schoolapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

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

public class UserSendMessage2 extends AppCompatActivity {

    private TextView tvTitle, tvToUser, tvToUserId;
    private EditText etTitle, etContent;
    private Button btnCancel, btnSend;
    private User toUser;
    private User logged_in_user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_send_message2);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        bindViews();
        checkPrefs();
        extractIntentExtras();
        setupSendAction();
    }

    private void bindViews() {
        tvTitle = findViewById(R.id.tvTitle);
        tvToUser = findViewById(R.id.tvToUser);
        tvToUserId = findViewById(R.id.tvToUserId);
        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        btnCancel = findViewById(R.id.btnCancel);
        btnSend = findViewById(R.id.btnSend);

        btnCancel.setOnClickListener(v -> finish());
    }
    private void checkPrefs() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(UserSendMessage2.this);
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

    private void extractIntentExtras() {
        Intent intent = getIntent();
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        String json = intent.getStringExtra("user");

        toUser = gson.fromJson(json, User.class);

        // update UI
        tvTitle.setText("Send Message");
        tvToUser.setText("To: " + toUser.getFirstName() + " " + toUser.getLastName());
        tvToUserId.setText("ID: " + toUser.getUser_id());
    }

    private void setupSendAction() {
        btnSend.setOnClickListener(v -> {
            String subject = etTitle.getText().toString().trim();
            String body = etContent.getText().toString().trim();

            Message msg = new Message(
                    logged_in_user.getUser_id(),
                    toUser.getUser_id(),
                    subject,
                    body,
                    LocalDate.now()
            );

            IMessageDA messageDA = MessageDAFactory.getMessageDA(this);
            messageDA.sendMessage(msg, new IMessageDA.BaseCallback() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(UserSendMessage2.this,
                                    "Message successfully sent!",
                                    Toast.LENGTH_SHORT)
                            .show();
                    finish();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(UserSendMessage2.this,
                                    "Could not send message: " + error,
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            });
        });
    }
}