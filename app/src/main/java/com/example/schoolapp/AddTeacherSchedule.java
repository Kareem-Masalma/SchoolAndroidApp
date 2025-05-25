package com.example.schoolapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.schoolapp.data_access.ClassDA;
import com.example.schoolapp.data_access.ClassDAFactory;
import com.example.schoolapp.data_access.SubjectDA;
import com.example.schoolapp.data_access.SubjectDAFactory;
import com.example.schoolapp.models.Class;

import com.example.schoolapp.data_access.DaysFactory;
import com.example.schoolapp.models.Subject;
import com.example.schoolapp.models.Teacher;
import com.google.gson.Gson;

import java.util.List;

public class AddTeacherSchedule extends AppCompatActivity {


    private Button btnAdd, btnCancel;
    private EditText etStartTime, etEndTime;
    private Spinner spSubject, spGrade, spDay;
    private ScrollView svSchedule;
    private TextView tvTeacher, tvId;

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
        teacherData();
        getSpinnersData();
        getPrevSchedule();
    }

    private void getPrevSchedule() {

    }

    private void getSpinnersData() {
        String[] days = DaysFactory.getDays();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(AddTeacherSchedule.this, android.R.layout.simple_list_item_1, days);
        spDay.setAdapter(adapter);

        ClassDAFactory.getClassDA(AddTeacherSchedule.this).getAllClasses(new ClassDA.ClassListCallback() {
            @Override
            public void onSuccess(List<Class> list) {
                ArrayAdapter<Class> classesAdapter = new ArrayAdapter<>(AddTeacherSchedule.this, android.R.layout.simple_list_item_1, list);
                spGrade.setAdapter(classesAdapter);
            }

            @Override
            public void onError(String error) {
                Log.d("Error", error);
            }
        });

        SubjectDAFactory.getSubjectDA(AddTeacherSchedule.this).getAllSubjects(new SubjectDA.SubjectListCallback() {
            @Override
            public void onSuccess(List<Subject> list) {
                ArrayAdapter<Subject> adapter = new ArrayAdapter<>(AddTeacherSchedule.this, android.R.layout.simple_list_item_1, list);
                spSubject.setAdapter(adapter);
            }

            @Override
            public void onError(String error) {
                Log.d("Error", error);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void teacherData() {
        Intent intent = getIntent();
        String teacherString = intent.getStringExtra(AddSchedule.TEACHER);
        Gson gson = new Gson();
        Teacher teacher = gson.fromJson(teacherString, Teacher.class);
        tvTeacher.setText("Teacher: " + teacher.getFirstName() + " " + teacher.getLastName());
        tvId.setText("ID: " + teacher.getUser_id());
    }

    private void defineViews() {
        this.btnAdd = findViewById(R.id.btnAdd);
        this.btnCancel = findViewById(R.id.btnCancel);
        this.etStartTime = findViewById(R.id.etStartTime);
        this.spSubject = findViewById(R.id.spSubject);
        this.spGrade = findViewById(R.id.spGrade);
        this.spDay = findViewById(R.id.spDay);
        this.etEndTime = findViewById(R.id.etEndTime);
        this.tvTeacher = findViewById(R.id.tvTeacher);
        this.tvId = findViewById(R.id.tvId);
//        this.svSchedule = findViewById(R.id.rvScheduleItems);
    }
}