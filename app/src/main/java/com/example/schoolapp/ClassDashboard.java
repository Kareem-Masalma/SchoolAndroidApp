package com.example.schoolapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.schoolapp.models.Class;
import com.example.schoolapp.models.Teacher;
import com.google.gson.Gson;

public class ClassDashboard extends AppCompatActivity {

    private Button btnSubjects, btnExams, btnAssignments, btnAttendance, btnStudents, btnSchedule;
    private TextView tvClassName;
    private Class selectedClass;
    private Teacher teacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_class_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getInfo();
        findViews();
        addActions();
    }

    private void addActions() {
        btnAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClassDashboard.this, TakeAttendance.class);
                Gson gson = new Gson();
                String classString = gson.toJson(selectedClass);
                intent.putExtra("schoolClass", classString);
                startActivity(intent);
            }
        });

        btnStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void getInfo() {
        Intent intent = getIntent();
        String classString = intent.getStringExtra(AddSchedule.CLASS);
        String teacherString = intent.getStringExtra(AddSchedule.TEACHER);
        Gson gson = new Gson();
        selectedClass = gson.fromJson(classString, Class.class);
        teacher = gson.fromJson(teacherString, Teacher.class);
    }

    @SuppressLint("SetTextI18n")
    private void findViews() {
        this.tvClassName = findViewById(R.id.tvClassName);
        this.btnAssignments = findViewById(R.id.btnAssignments);
        this.btnAttendance = findViewById(R.id.btnAttendance);
        this.btnExams = findViewById(R.id.btnExams);
        this.btnSchedule = findViewById(R.id.btnSchedule);
        this.btnSubjects = findViewById(R.id.btnSubjects);
        this.btnStudents = findViewById(R.id.btnStudents);
        this.btnAttendance.setEnabled(teacher.getUser_id() == selectedClass.getClassManagerId());
        this.tvClassName.setText("Class: " + selectedClass.getClassName());
    }
}