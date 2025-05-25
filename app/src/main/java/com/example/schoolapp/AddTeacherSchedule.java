package com.example.schoolapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddTeacherSchedule extends AppCompatActivity {


    private Button btnAdd, btnCancel;
    private EditText etStartTime, etEndTime;
    private Spinner spSubject, spGrade, spDay;
    private ScrollView svSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_teacher_schedule);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        defineViews();

    }

    private void defineViews() {
        this.btnAdd = findViewById(R.id.btnAdd);
        this.btnCancel = findViewById(R.id.btnCancel);
        this.etStartTime = findViewById(R.id.etStartTime);
        this.spSubject = findViewById(R.id.spSubject);
        this.spGrade = findViewById(R.id.spGrade);
        this.spDay = findViewById(R.id.spDay);
        this.etEndTime = findViewById(R.id.etEndTime);
//        this.svSchedule = findViewById(R.id.rvScheduleItems);
    }
}