package com.example.schoolapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolapp.adapters.StudentMarksAdapter;
import com.example.schoolapp.data_access.ExamDA;
import com.example.schoolapp.data_access.IExamDA;
import com.example.schoolapp.data_access.StudentDA;
import com.example.schoolapp.data_access.StudentDAFactory;
import com.example.schoolapp.data_access.SubjectDA;
import com.example.schoolapp.data_access.SubjectDAFactory;
import com.example.schoolapp.json_helpers.LocalDateAdapter;
import com.example.schoolapp.models.SchoolClass;
import com.example.schoolapp.models.Student;
import com.example.schoolapp.models.Exam;
import com.example.schoolapp.models.StudentExamResult;
import com.example.schoolapp.models.Subject;
import com.example.schoolapp.models.Teacher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExamMarks extends AppCompatActivity {

    private RecyclerView rvStudents;
    private Button btnPublish;
    private Spinner spExams;
    private SchoolClass selectedClass;
    private StudentMarksAdapter adapter;
    private Teacher teacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exam_marks);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        defineViews();
        getClassData();
        getSpinnerData();
        loadStudents();
        addActionButton();
    }

    private void addActionButton() {
        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Exam exam = (Exam) spExams.getSelectedItem();

                List<StudentExamResult> studentMarks = adapter.getStudentMarks();

                boolean hasValidMark = false;
                for (StudentExamResult res : studentMarks) {
                    if (res.getMark() > 0) {
                        hasValidMark = true;
                        break;
                    }
                }
                if (!hasValidMark) {
                    Toast.makeText(ExamMarks.this, "Please enter at least one mark", Toast.LENGTH_SHORT).show();
                    return;
                }

                ExamDA examDA = new ExamDA(ExamMarks.this);
                examDA.publishExamResults(exam.getExamId(), studentMarks, new IExamDA.PublishCallback() {

                    @Override
                    public void onSuccess(String message) {
                        Toast.makeText(ExamMarks.this, "Results published", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(ExamMarks.this, error, Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }

    private void loadStudents() {
        StudentDAFactory.getStudentDA(ExamMarks.this).getClassStudents(selectedClass.getClassId(), new StudentDA.StudentListCallback() {
            @Override
            public void onSuccess(List<Student> list) {
                List<StudentExamResult> results = new ArrayList<>();
                for (Student student : list) {
                    StudentExamResult result = new StudentExamResult(
                            student.getUser_id(),
                            student.getFirstName() + " " + student.getLastName()
                    );
                    results.add(result);
                }
                adapter = new StudentMarksAdapter(results);
                rvStudents.setAdapter(adapter);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ExamMarks.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getSpinnerData() {
        ExamDA examDA = new ExamDA(ExamMarks.this);
        examDA.getClassExams(teacher.getUser_id(), new ExamDA.ExamListCallBack() {
            @Override
            public void onSuccess(List<Exam> exams) {
                ArrayAdapter<Exam> adapter = new ArrayAdapter<>(ExamMarks.this, android.R.layout.simple_list_item_1, exams);
                spExams.setAdapter(adapter);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ExamMarks.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void getClassData() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ExamMarks.this);
        boolean isLoggedIn = pref.getBoolean(Login.LOGGED_IN, false);
        String teacherString = "";
        if (isLoggedIn)
            teacherString = pref.getString(Login.LOGGED_IN_USER, "");
        else {
            Intent intent = new Intent(ExamMarks.this, Login.class);
            startActivity(intent);
        }
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        teacher = gson.fromJson(teacherString, Teacher.class);

        Intent intent = getIntent();
        String classJson = intent.getStringExtra(AddSchedule.CLASS);
        selectedClass = gson.fromJson(classJson, SchoolClass.class);
    }

    private void defineViews() {
        this.rvStudents = findViewById(R.id.rvStudentMarks);
        this.btnPublish = findViewById(R.id.btnPublish);
        this.spExams = findViewById(R.id.spExams);
        this.rvStudents.setLayoutManager(new LinearLayoutManager(ExamMarks.this));
    }
}