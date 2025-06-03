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
import com.example.schoolapp.models.Class;
import com.example.schoolapp.models.User;
import com.google.gson.Gson;

import java.util.List;

public class ClassStudents extends AppCompatActivity {

    private TextView tvClass, tvClassId;
    private RecyclerView rvStudents;
    private Class selectedClass;
    private StudentDA studentDA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_students);
        defineViews();
        loadClassData();
//        fetchStudents();
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
        selectedClass = new Gson().fromJson(classJson, Class.class);

        tvClass.setText("Class: " + selectedClass.getClassName());
        tvClassId.setText("ID: " + selectedClass.getClassId());
    }

//    private void fetchStudents() {
//        studentDA = StudentDAFactory.getStudentDA(this);
//        studentDA.getStudentsByClass(selectedClass.getClassId(), new StudentDA.StudentListCallback() {
//            @Override
//            public void onSuccess(List<User> students) {
//                StudentClassAdapter adapter = new StudentClassAdapter(ClassStudentsActivity.this, students, student -> {
//                    Toast.makeText(ClassStudentsActivity.this, "Send message to: " + student.getFirstName(), Toast.LENGTH_SHORT).show();
//                });
//                rvStudents.setAdapter(adapter);
//            }
//
//            @Override
//            public void onError(String error) {
//                Toast.makeText(ClassStudentsActivity.this, error, Toast.LENGTH_SHORT).show();
//            }
//        });
}

