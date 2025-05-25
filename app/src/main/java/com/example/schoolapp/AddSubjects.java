package com.example.schoolapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class AddSubjects extends AppCompatActivity {

    private EditText subNameET;
    private Spinner assignGradeSP;
    private CheckBox mondayCB, tuesdayCB, wednesdayCB, thursdayCB, fridayCB, saturdayCB, sundayCB;
    private Spinner startTimeSP;
    private Spinner endTimeSP;
    private Button cancleBT;
    private Button addBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_subjects);
        initializeViews();

    }

    private void initializeViews(){
        subNameET = findViewById(R.id.subNameET);
        assignGradeSP = findViewById(R.id.assignGradeSP);

        mondayCB = findViewById(R.id.mondayCB);
        tuesdayCB = findViewById(R.id.tuesdayCB);
        wednesdayCB = findViewById(R.id.wednesdayCB);
        thursdayCB = findViewById(R.id.thursdayCB);
        fridayCB = findViewById(R.id.fridayCB);
        saturdayCB = findViewById(R.id.saturdayCB);
        sundayCB = findViewById(R.id.sundayCB);

        startTimeSP = findViewById(R.id.startTimeSP);
        endTimeSP = findViewById(R.id.endTimeSP);

        cancleBT = findViewById(R.id.cancleBT);
        addBT = findViewById(R.id.addBT);

        setupTimeSP();
        setupGradeSP();
    }

    private void setupTimeSP(){
        List<String> times = generateTime();

        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item, times);

        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startTimeSP.setAdapter(timeAdapter);
        endTimeSP.setAdapter(timeAdapter);
    }

    private List<String> generateTime() {
        List<String> times = new ArrayList<>();
        for (int hour = 7; hour <= 17; hour++) {
            for (int min = 0; min < 60; min += 15) {
                times.add(String.format("%02d:%02d:00", hour, min));
            }
        }
        return times;
    }

    private void setupGradeSP(){
        List<String> gradeNum = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            gradeNum.add(String.valueOf(i));
        }

        ArrayAdapter<String> gradeAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item, gradeNum);

        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assignGradeSP.setAdapter(gradeAdapter);
    }


}
