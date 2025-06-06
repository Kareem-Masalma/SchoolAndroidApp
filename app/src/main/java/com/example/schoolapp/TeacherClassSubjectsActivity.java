package com.example.schoolapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolapp.adapters.SubjectAdapter;
import com.example.schoolapp.data_access.SubjectDA;
import com.example.schoolapp.models.Class;
import com.example.schoolapp.models.Subject;
import com.example.schoolapp.models.Teacher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.util.List;

public class TeacherClassSubjectsActivity extends AppCompatActivity {

    private TextView tvClass, tvClassId;
    private RecyclerView rvSubjects;
    private Class selectedClass;
    private SubjectDA subjectDA;
    private Teacher teacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_class_subjects);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        defineViews();
        loadClassTeacherData();
        fetchSubjects();
    }

    private void defineViews() {
        tvClass = findViewById(R.id.tvClass);
        tvClassId = findViewById(R.id.tvClassId);
        rvSubjects = findViewById(R.id.rvSubjects);
        rvSubjects.setLayoutManager(new LinearLayoutManager(this));
    }

    @SuppressLint("SetTextI18n")
    private void loadClassTeacherData() {
        Intent intent = getIntent();
        String classJson = intent.getStringExtra(AddSchedule.CLASS);
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new com.example.schoolapp.json_helpers.LocalDateAdapter()).create();
        selectedClass = gson.fromJson(classJson, Class.class);
        tvClass.setText("Class: " + selectedClass.getClassName());
        tvClassId.setText("ID: " + selectedClass.getClassId());

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(TeacherClassSubjectsActivity.this);
        boolean isLoggedIn = pref.getBoolean("Logged_in", false);
        String teacherString = "";
        if (isLoggedIn)
            teacherString = pref.getString("Logged_in_user", "");
        else {
            intent = new Intent(TeacherClassSubjectsActivity.this, Login.class);
            startActivity(intent);
        }
        teacher = gson.fromJson(teacherString, Teacher.class);

    }

    private void fetchSubjects() {
        subjectDA = new SubjectDA(TeacherClassSubjectsActivity.this);
        subjectDA.getClassTeacherSubject(selectedClass.getClassId(), teacher.getUser_id(), new SubjectDA.ClassSubjectCallback() {
            @Override
            public void onSuccess(List<Subject> list) {
                SubjectAdapter adapter = new SubjectAdapter(TeacherClassSubjectsActivity.this, list);
                rvSubjects.setAdapter(adapter);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(TeacherClassSubjectsActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

}