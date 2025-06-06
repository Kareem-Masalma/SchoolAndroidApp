package com.example.schoolapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolapp.adapters.StudentClassAdapter;
import com.example.schoolapp.data_access.StudentDA;
import com.example.schoolapp.data_access.StudentDAFactory;
import com.example.schoolapp.models.SchoolClass;
import com.example.schoolapp.models.Student;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.util.List;

public class ClassStudents extends AppCompatActivity {

    private TextView tvClass, tvClassId;
    private RecyclerView rvStudents;
    private SchoolClass selectedClass;
    private StudentDA studentDA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_students);
        defineViews();
        loadClassData();
        fetchStudents();
    }

    private void defineViews() {
        tvClass = findViewById(R.id.tvClass);
        tvClassId = findViewById(R.id.tvClassId);
        rvStudents = findViewById(R.id.rvStudents);
        rvStudents.setLayoutManager(new LinearLayoutManager(this));
    }

    @SuppressLint("SetTextI18n")
    private void loadClassData() {
        Intent intent = getIntent();
        String classJson = intent.getStringExtra(AddSchedule.CLASS);
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new com.example.schoolapp.json_helpers.LocalDateAdapter()).create();
        selectedClass = gson.fromJson(classJson, SchoolClass.class);
        tvClass.setText("Class: " + selectedClass.getClassName());
        tvClassId.setText("ID: " + selectedClass.getClassId());
    }

    private void fetchStudents() {
        studentDA = (StudentDA) StudentDAFactory.getStudentDA(ClassStudents.this);
        studentDA.getClassStudents(selectedClass.getClassId(), new StudentDA.StudentListCallback() {

            @Override
            public void onSuccess(List<Student> list) {
                StudentClassAdapter adapter = new StudentClassAdapter(ClassStudents.this, list, student -> {
                    Intent intent = new Intent(ClassStudents.this, UserSendMessage2.class);
                    Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new com.example.schoolapp.json_helpers.LocalDateAdapter()).create();
                    String userJson = gson.toJson(student);
                    intent.putExtra("user", userJson);
                    startActivity(intent);
                });
                rvStudents.setAdapter(adapter);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ClassStudents.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

