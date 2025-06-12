package com.example.schoolapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.schoolapp.models.Exam;
import com.example.schoolapp.json_helpers.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.LocalDate;

public class ExamDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_details);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        TextView textClass = findViewById(R.id.textClassTitle);
        TextView textSubject = findViewById(R.id.textSubjectTitle);
        TextView textTitle = findViewById(R.id.textTitle);
        TextView textDate = findViewById(R.id.textExamDateAt);
        TextView textDuration = findViewById(R.id.textDuration);
        TextView textPercentage = findViewById(R.id.textPercentage);

        // Get data from intent
        String json = getIntent().getStringExtra("EXAM_JSON");
        String classTitle = getIntent().getStringExtra("CLASS_TITLE");
        String subjectTitle = getIntent().getStringExtra("SUBJECT_TITLE");

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();

        Exam exam = gson.fromJson(json, Exam.class);

        textClass.setText("Class: " + classTitle);
        textSubject.setText("Subject: " + subjectTitle);
        textTitle.setText("Title: " + exam.getTitle());
        textDate.setText("Date: " + exam.getDate());
        textDuration.setText("Duration: " + exam.getDuration() + " min");
        textPercentage.setText("Weight: " + exam.getPercentage() + "%");
    }
}
