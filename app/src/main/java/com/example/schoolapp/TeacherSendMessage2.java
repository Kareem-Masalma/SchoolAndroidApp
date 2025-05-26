package com.example.schoolapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.schoolapp.data_access.IMessageDA;
import com.example.schoolapp.data_access.MessageDAFactory;
import com.example.schoolapp.models.Message;
import com.example.schoolapp.models.Role;
import com.example.schoolapp.models.Student;
import com.example.schoolapp.models.Teacher;
import com.google.gson.Gson;

import java.time.LocalDate;

public class TeacherSendMessage2 extends AppCompatActivity {

    private TextView tvTitle, tvToStudent, tvToStudentId;
    private EditText etTitle, etContent;
    private Button btnCancel, btnSend;
    private Student toStudent;
    private Teacher fromTeacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_send_message2);
        setupViews();
        setupIntent();
        handleBtnSend();
    }

    private void handleBtnSend() {
        btnSend.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String content = etContent.getText().toString().trim();
            Message msg = new Message(fromTeacher.getUser_id(), toStudent.getUser_id(),title,content,LocalDate.now());
            IMessageDA messageDA = MessageDAFactory.getMessageDA(this);
            messageDA.sendMessage(msg, new IMessageDA.BaseCallback() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(TeacherSendMessage2.this, "Message successfully sent!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(TeacherSendMessage2.this, "Could not send message: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void setupIntent() {
        Intent intent = getIntent();
        Gson gson = new Gson();
        String json =  intent.getStringExtra("student");
        toStudent = gson.fromJson(json , Student.class);
        tvToStudent.setText("To Student: " + toStudent.getFirstName() + " " + toStudent.getLastName());
        tvToStudentId.setText("Student ID: " + toStudent.getUser_id());

// TODO add the logged on teacher from the previous activity to the intent

//        String teacherJson =  intent.getStringExtra("teacher");
//        fromTeacher = gson.fromJson(teacherJson, Teacher.class);
        // Integer user_id, String firstName, String lastName, LocalDate birthDate, String address, String phone, Role role, String speciality
    fromTeacher = new Teacher(1, "John", "Smith", LocalDate.now(), "123 Elm St", "555-1001", Role.TEACHER , "Mathematics");


    }

    private void setupViews() {
        tvTitle = findViewById(R.id.tvTitle);
        tvToStudent = findViewById(R.id.tvToStudent);
        tvToStudentId = findViewById(R.id.tvToStudentId);

        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);

        btnCancel = findViewById(R.id.btnCancel);
        btnSend = findViewById(R.id.btnSend);

        btnCancel.setOnClickListener(v -> {
            finish();
        });


    }
}
