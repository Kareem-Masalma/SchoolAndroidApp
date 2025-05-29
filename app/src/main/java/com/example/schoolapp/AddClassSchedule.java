package com.example.schoolapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolapp.adapters.ScheduleSubjectAdapter;
import com.example.schoolapp.data_access.ClassDA;
import com.example.schoolapp.data_access.ClassDAFactory;
import com.example.schoolapp.data_access.DaysFactory;
import com.example.schoolapp.data_access.ScheduleDA;
import com.example.schoolapp.data_access.ScheduleDAFactory;
import com.example.schoolapp.data_access.SubjectDA;
import com.example.schoolapp.data_access.SubjectDAFactory;
import com.example.schoolapp.models.Class;
import com.example.schoolapp.models.ScheduleSubject;
import com.example.schoolapp.models.Subject;
import com.example.schoolapp.models.Teacher;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class AddClassSchedule extends AppCompatActivity {

    private TextView tvClass, tvId;
    private Spinner spSubject, spDay;
    private EditText etStartTime, etEndTime;
    private RecyclerView rvScheduleItems;
    private Button btnAdd, btnCancel;
    private Class selectedClass;
    private List<ScheduleSubject> classSchedules;
    private ScheduleDA scheduleDA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_class_schedule);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        defineViews();
        classData();
        getSpinnerData();
        loadClassSchedule(selectedClass.getScheduleId());
    }

    private void loadClassSchedule(int scheduleId) {
        ScheduleSubjectAdapter adapter = new ScheduleSubjectAdapter(new ArrayList<>());
        rvScheduleItems.setAdapter(adapter);

        scheduleDA = ScheduleDAFactory.getScheduleDA(AddClassSchedule.this);
        scheduleDA.getScheduleById(scheduleId, new ScheduleDA.ScheduleListCallback() {
            @Override
            public void onSuccess(List<ScheduleSubject> list) {
                if (!list.isEmpty()) {
                    classSchedules = list;
                    adapter.updateData(list);
                    rvScheduleItems.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(AddClassSchedule.this, "No schedule entries found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AddClassSchedule.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getSpinnerData() {
        String[] days = DaysFactory.getDays();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(AddClassSchedule.this, android.R.layout.simple_list_item_1, days);
        spDay.setAdapter(adapter);

        SubjectDAFactory.getSubjectDA(AddClassSchedule.this).getAllSubjects(new SubjectDA.SubjectListCallback() {
            @Override
            public void onSuccess(List<Subject> list) {
                ArrayAdapter<Subject> adapter = new ArrayAdapter<>(AddClassSchedule.this, android.R.layout.simple_list_item_1, list);
                spSubject.setAdapter(adapter);
            }

            @Override
            public void onError(String error) {
                Log.d("Error", error);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void classData() {
        Intent intent = getIntent();
        String classString = intent.getStringExtra(AddSchedule.CLASS);
        Gson gson = new Gson();
        selectedClass = gson.fromJson(classString, Class.class);
        tvClass.setText("Class: " + selectedClass.getClassName());
        tvId.setText("ID: " + selectedClass.getClassId());
    }

    private void defineViews() {
        this.tvClass = findViewById(R.id.tvClass);
        this.tvId = findViewById(R.id.tvId);
        this.spSubject = findViewById(R.id.spSubject);
        this.spDay = findViewById(R.id.spDay);
        this.etStartTime = findViewById(R.id.etStartTime);
        this.etEndTime = findViewById(R.id.etEndTime);
        this.rvScheduleItems = findViewById(R.id.rvScheduleItems);
        this.btnAdd = findViewById(R.id.btnAddSchedule);
        this.btnCancel = findViewById(R.id.btnCancel);
    }
}