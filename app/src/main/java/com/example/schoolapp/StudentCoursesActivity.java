package com.example.schoolapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolapp.adapters.SubAdapter;
import com.example.schoolapp.data_access.StudentDA;
import com.example.schoolapp.data_access.SubjectDA;
import com.example.schoolapp.json_helpers.LocalDateAdapter;
import com.example.schoolapp.models.Student;
import com.example.schoolapp.models.Subject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StudentCoursesActivity extends AppCompatActivity {

    private TextView studentNameTV, studentIdTV;
    private RecyclerView viewCoursesRV;
    private Button backToProfileBT;
    private SubAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_courses);

        initializeViews();
        viewCoursesRV.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SubAdapter(this, new ArrayList<>(), subject -> {
            Intent intent = new Intent(StudentCoursesActivity.this, ViewStudentMarks.class);
            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
            String subjectStr = gson.toJson(subject);
            intent.putExtra("SELECTED_SUBJECT", subjectStr);
            startActivity(intent);
        });

        viewCoursesRV.setAdapter(adapter);

        loginInfo();
    }

    private void initializeViews() {
        studentNameTV = findViewById(R.id.studentNameTV);
        studentIdTV = findViewById(R.id.studentIdTV);
        viewCoursesRV = findViewById(R.id.viewCoursesRV);
        backToProfileBT = findViewById(R.id.backToProfileBT);
        backToProfileBT.setOnClickListener(v -> finish());
    }

    private void loadStudentSubjects(int userId) {
        StudentDA studentDA = new StudentDA(this);
        SubjectDA subjectDA = new SubjectDA(this);

        studentDA.getClassIdByUserId(userId, new StudentDA.ClassIdCallback() {
            @Override
            public void onSuccess(int classId) {
                subjectDA.getClassSubject(classId, new SubjectDA.ClassSubjectCallback() {
                    @Override
                    public void onSuccess(List<Subject> list) {
                        adapter.updateData(list);
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(getApplicationContext(), "Failed to load subjects: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getApplicationContext(), "Failed to get class ID: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loginInfo(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isLoggedIn = pref.getBoolean(Login.LOGGED_IN, false);

        if (!isLoggedIn) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String studentStr = pref.getString(Login.LOGGED_IN_USER, "");
        if (studentStr.isEmpty()) {
            Toast.makeText(this, "User data missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        Student student = gson.fromJson(studentStr, Student.class);
        studentNameTV.setText("Student: " + student.getFirstName() + " " + student.getLastName());
        studentIdTV.setText("ID: " + student.getUser_id());

        loadStudentSubjects(student.getUser_id());
    }

}