package com.example.schoolapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.schoolapp.json_helpers.LocalDateAdapter;
import com.example.schoolapp.models.SchoolClass;
import com.example.schoolapp.models.Teacher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;

public class ClassDashboard extends AppCompatActivity {

    private Button btnSubjects, btnNewAssignment, btnAttendance, btnStudents, btnSchedule, btnExamMarks;
    private TextView tvClassName;
    private SchoolClass selectedSchoolClass;
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
                Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
                String classString = gson.toJson(selectedSchoolClass);
                intent.putExtra("schoolClass", classString);
                startActivity(intent);
            }
        });

        btnStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClassDashboard.this, ClassStudents.class);
                Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
                String strClass = gson.toJson(selectedSchoolClass);
                intent.putExtra(AddSchedule.CLASS, strClass);
                startActivity(intent);
            }
        });

        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClassDashboard.this, ViewSchedule.class);
                Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
                String classJson = gson.toJson(selectedSchoolClass);
                intent.putExtra(AddSchedule.CLASS, classJson);
                startActivity(intent);
            }
        });

        btnSubjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClassDashboard.this, TeacherClassSubjectsActivity.class);
                Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new com.example.schoolapp.json_helpers.LocalDateAdapter()).create();
                String classString = gson.toJson(selectedSchoolClass);
                intent.putExtra(AddSchedule.CLASS, classString);
                startActivity(intent);
            }
        });

        btnNewAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClassDashboard.this, SendAssignmentActivity.class);
                Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
                String stringClass = gson.toJson(selectedSchoolClass);
                intent.putExtra(AddSchedule.CLASS, stringClass);
                startActivity(intent);
            }
        });

        btnExamMarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClassDashboard.this, ExamMarks.class);
                Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new com.example.schoolapp.json_helpers.LocalDateAdapter()).create();
                String classString = gson.toJson(selectedSchoolClass);
                intent.putExtra(AddSchedule.CLASS, classString);
                startActivity(intent);
            }
        });

    }

    private void getInfo() {
        Intent intent = getIntent();
        String classString = intent.getStringExtra(AddSchedule.CLASS);
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        selectedSchoolClass = gson.fromJson(classString, SchoolClass.class);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ClassDashboard.this);
        boolean isLoggedIn = pref.getBoolean(Login.LOGGED_IN, false);
        String teacherString = "";
        if (isLoggedIn) {
            teacherString = pref.getString(Login.LOGGED_IN_USER, "");
            teacher = gson.fromJson(teacherString, Teacher.class);
        } else {
            intent = new Intent(this, Login.class);
            startActivity(intent);
        }
    }

    @SuppressLint("SetTextI18n")
    private void findViews() {
        this.tvClassName = findViewById(R.id.tvClassName);
        this.btnNewAssignment = findViewById(R.id.btnNewAssignment);
        this.btnAttendance = findViewById(R.id.btnAttendance);
        this.btnSchedule = findViewById(R.id.btnSchedule);
        this.btnSubjects = findViewById(R.id.btnSubjects);
        this.btnStudents = findViewById(R.id.btnStudents);
        this.btnExamMarks = findViewById(R.id.btnExamMarks);
        this.btnAttendance.setEnabled(teacher.getUser_id() == selectedSchoolClass.getClassManagerId());
        this.tvClassName.setText("Class: " + selectedSchoolClass.getClassName());
    }
}