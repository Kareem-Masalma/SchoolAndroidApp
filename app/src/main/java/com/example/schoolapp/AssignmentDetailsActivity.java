package com.example.schoolapp;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.schoolapp.data_access.DA_Config;
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
        TextView textFile = findViewById(R.id.textFile);
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
        if (assignment.getFilePath() != null && !assignment.getFilePath().trim().isEmpty() && !assignment.getFilePath().equalsIgnoreCase("null")) {
            textFile.setVisibility(View.VISIBLE);
            textFile.setText("Open attached file");
            textFile.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
            textFile.setPaintFlags(textFile.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            textFile.setOnClickListener(v -> {
                String fileUrl = "http://" + DA_Config.BACKEND_IP_ADDRESS + "/" + DA_Config.BACKEND_DIR + "/" + assignment.getFilePath();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl));
                startActivity(browserIntent);
            });
        } else {
            textFile.setVisibility(View.GONE);
        }



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
