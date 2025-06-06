package com.example.schoolapp;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.schoolapp.models.Role;
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
    private User fromUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_send_message2);

        bindViews();
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

    private void extractIntentExtras() {
        Intent intent = getIntent();
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        String json = intent.getStringExtra("user");

        toUser = gson.fromJson(json, User.class);

        // TODO this should be read from the logged in user environment variable in shared preferences
        fromUser = new Teacher(1, "John", "Smith", LocalDate.now(), "123 Elm St", "555-1001", Role.TEACHER, "Mathematics");

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
                    fromUser.getUser_id(),
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