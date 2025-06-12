package com.example.schoolapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
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

    private View cardSubjects, cardNewAssignment, cardAttendance, cardStudents, cardSchedule, cardExamMarks;
    private TextView tvClassName;
    private SchoolClass selectedClass;
    private Teacher teacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_class_dashboard);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getInfo();
        findViews();
        addActions();
    }

    private void addActions() {
        cardAttendance.setOnClickListener(v -> {
            Intent intent = new Intent(ClassDashboard.this, TakeAttendance.class);
            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
            String classString = gson.toJson(selectedClass);
            intent.putExtra("Class", classString);
            startActivity(intent);
        });

        cardStudents.setOnClickListener(v -> {
            Intent intent = new Intent(ClassDashboard.this, ClassStudents.class);
            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
            String strClass = gson.toJson(selectedClass);
            intent.putExtra(AddSchedule.CLASS, strClass);
            startActivity(intent);
        });

        cardSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(ClassDashboard.this, ViewSchedule.class);
            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
            String classJson = gson.toJson(selectedClass);
            intent.putExtra(AddSchedule.CLASS, classJson);
            startActivity(intent);
        });

        cardSubjects.setOnClickListener(v -> {
            Intent intent = new Intent(ClassDashboard.this, TeacherClassSubjectsActivity.class);
            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
            String classString = gson.toJson(selectedClass);
            intent.putExtra(AddSchedule.CLASS, classString);
            startActivity(intent);
        });

        cardNewAssignment.setOnClickListener(v -> {
            Intent intent = new Intent(ClassDashboard.this, SendAssignmentActivity.class);
            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
            String stringClass = gson.toJson(selectedClass);
            intent.putExtra(AddSchedule.CLASS, stringClass);
            startActivity(intent);
        });

        cardExamMarks.setOnClickListener(v -> {
            Intent intent = new Intent(ClassDashboard.this, ExamMarks.class);
            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
            String classString = gson.toJson(selectedClass);
            intent.putExtra(AddSchedule.CLASS, classString);
            startActivity(intent);
        });
    }


    private void getInfo() {
        Intent intent = getIntent();
        String classString = intent.getStringExtra(AddSchedule.CLASS);
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        selectedClass = gson.fromJson(classString, SchoolClass.class);

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
        this.cardNewAssignment = findViewById(R.id.card_assignments);
        this.cardAttendance = findViewById(R.id.card_attendance);
        this.cardSchedule = findViewById(R.id.card_schedule);
        this.cardSubjects = findViewById(R.id.card_subjects);
        this.cardStudents = findViewById(R.id.card_students);
        this.cardExamMarks = findViewById(R.id.card_exam_marks);

        if (teacher.getUser_id() != selectedClass.getClassManagerId()) {
            cardAttendance.setVisibility(View.GONE);
        }

        this.tvClassName.setText("Class: " + selectedClass.getClassName());
    }


}