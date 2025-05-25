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

        cancleBT = findViewById(R.id.cancleBT);
        addBT = findViewById(R.id.addBT);

        setupGradeSP();
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
