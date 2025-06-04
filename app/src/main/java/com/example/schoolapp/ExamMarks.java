package com.example.schoolapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolapp.adapters.StudentMarksAdapter;
import com.example.schoolapp.data_access.StudentDA;
import com.example.schoolapp.data_access.StudentDAFactory;
import com.example.schoolapp.data_access.SubjectDA;
import com.example.schoolapp.data_access.SubjectDAFactory;
import com.example.schoolapp.models.Class;
import com.example.schoolapp.models.Student;
import com.example.schoolapp.models.StudentExamResult;
import com.example.schoolapp.models.Subject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ExamMarks extends AppCompatActivity {

    private TextView tvClass;
    private Spinner spSubject;
    private EditText edTitle;
    private RecyclerView rvStudents;
    private EditText etExamDate;
    private LocalDate examDate;
    private Class selectedClass;

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
                StudentMarksAdapter adapter = new StudentMarksAdapter(results);
                rvStudents.setAdapter(adapter);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ExamMarks.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getSpinnerData() {
        SubjectDAFactory.getSubjectDA(ExamMarks.this).getClassSubject(selectedClass.getClassId(), new SubjectDA.ClassSubjectCallback() {
            @Override
            public void onSuccess(List<Subject> list) {
                ArrayAdapter<Subject> adapter = new ArrayAdapter<>(ExamMarks.this, android.R.layout.simple_list_item_1, list);
                spSubject.setAdapter(adapter);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ExamMarks.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void getClassData() {
        Intent intent = getIntent();
        String classJson = intent.getStringExtra(AddSchedule.CLASS);
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new com.example.schoolapp.json_helpers.LocalDateAdapter()).create();
        selectedClass = gson.fromJson(classJson, Class.class);
        tvClass.setText("Class: " + selectedClass.getClassName());
    }

    private void defineViews() {
        this.edTitle = findViewById(R.id.etExamTitle);
        this.tvClass = findViewById(R.id.tvClass);
        this.spSubject = findViewById(R.id.spSubject);
        this.rvStudents = findViewById(R.id.rvStudentMarks);
        this.etExamDate = findViewById(R.id.etExamDate);
        this.etExamDate.setOnClickListener(v -> showDatePicker());
        this.rvStudents.setLayoutManager(new LinearLayoutManager(ExamMarks.this));
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    examDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay);
                    etExamDate.setText(examDate.toString());
                },
                year, month, day
        );
        datePickerDialog.show();
    }
}