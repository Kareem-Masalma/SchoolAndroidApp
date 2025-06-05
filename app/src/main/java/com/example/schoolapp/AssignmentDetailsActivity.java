package com.example.schoolapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.schoolapp.json_helpers.LocalDateAdapter;
import com.example.schoolapp.models.Assignment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;

public class AssignmentDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_details);

        TextView textClass = findViewById(R.id.textClassTitle);
        TextView textTitle = findViewById(R.id.textTitle);
        TextView textDeadline = findViewById(R.id.textDeadline);
        TextView textDetails = findViewById(R.id.textDetails);
        Button btnSubmit = findViewById(R.id.btnSubmitWork);

        // Get data from intent
        String json = getIntent().getStringExtra("ASSIGNMENT_JSON");
        String classTitle = getIntent().getStringExtra("CLASS_TITLE");
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
        Assignment assignment =gson.fromJson(json, Assignment.class);

        // Set text
        textClass.setText("Class: " + classTitle);
        textTitle.setText("Title: " + assignment.getTitle());
        textDeadline.setText("Deadline: " + assignment.getEnd_date());
        textDetails.setText(assignment.getDetails());

        btnSubmit.setOnClickListener(v -> {
            Intent intent = new Intent(this, SubmitAssignmentActivity.class);
            intent.putExtra("ASSIGNMENT_JSON", json);
            intent.putExtra("CLASS_TITLE", classTitle);
            startActivity(intent);
        });
    }
}
