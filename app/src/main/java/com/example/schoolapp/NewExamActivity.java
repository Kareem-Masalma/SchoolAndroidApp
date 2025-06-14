package com.example.schoolapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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

import com.example.schoolapp.adapters.StudentMarksAdapter;
import com.example.schoolapp.data_access.ExamDA;
import com.example.schoolapp.data_access.IExamDA;
import com.example.schoolapp.data_access.StudentDA;
import com.example.schoolapp.data_access.StudentDAFactory;
import com.example.schoolapp.data_access.SubjectDA;
import com.example.schoolapp.data_access.SubjectDAFactory;
import com.example.schoolapp.models.SchoolClass;
import com.example.schoolapp.models.Student;
import com.example.schoolapp.models.Exam;
import com.example.schoolapp.models.StudentExamResult;
import com.example.schoolapp.models.Subject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NewExamActivity extends AppCompatActivity {

    private TextView tvClass;
    private Spinner spSubject;
    private EditText edTitle, edDuration, edPercentage;
    private EditText etExamDate;
    private Button btnAdd;
    private LocalDate examDate;
    private SchoolClass selectedClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_exam);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        defineViews();
        getClassData();
        getSpinnerData();
        addActionButton();
    }

    private void addActionButton() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String examTitle = edTitle.getText().toString();
                Subject subject = (Subject) spSubject.getSelectedItem();
                int duration = Integer.parseInt(edDuration.getText().toString());
                int percentage = Integer.parseInt(edPercentage.getText().toString());

                if (examTitle.isEmpty()) {
                    Toast.makeText(NewExamActivity.this, "Title is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (examDate == null) {
                    Toast.makeText(NewExamActivity.this, "Please select a date", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (percentage >= 100 || percentage <= 0) {
                    Toast.makeText(NewExamActivity.this, "Choose a Valid Percentage", Toast.LENGTH_SHORT).show();
                    return;
                }


                Exam exam = new Exam(examTitle, subject.getSubjectId(), examDate, duration, percentage);


                ExamDA examDA = new ExamDA(NewExamActivity.this);
                examDA.addExam(exam, new IExamDA.CallBack() {
                    @Override
                    public void onSuccess(String message) {
                        Toast.makeText(NewExamActivity.this, "New Exam Added: " + message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(NewExamActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    private void getSpinnerData() {
        SubjectDAFactory.getSubjectDA(NewExamActivity.this).getClassSubject(selectedClass.getClassId(), new SubjectDA.ClassSubjectCallback() {
            @Override
            public void onSuccess(List<Subject> list) {
                ArrayAdapter<Subject> adapter = new ArrayAdapter<>(NewExamActivity.this, android.R.layout.simple_list_item_1, list);
                spSubject.setAdapter(adapter);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(NewExamActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void getClassData() {
        Intent intent = getIntent();
        String classJson = intent.getStringExtra(AddSchedule.CLASS);
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new com.example.schoolapp.json_helpers.LocalDateAdapter()).create();
        selectedClass = gson.fromJson(classJson, SchoolClass.class);
        tvClass.setText("Class: " + selectedClass.getClassName());
    }

    private void defineViews() {
        this.edTitle = findViewById(R.id.etExamTitle);
        this.tvClass = findViewById(R.id.tvClass);
        this.spSubject = findViewById(R.id.spSubject);
        this.etExamDate = findViewById(R.id.etExamDate);
        this.btnAdd = findViewById(R.id.btnAdd);
        this.edDuration = findViewById(R.id.etDuration);
        this.edPercentage = findViewById(R.id.etPercentage);
        this.etExamDate.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            examDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay);
            etExamDate.setText(examDate.toString());
        }, year, month, day);
        datePickerDialog.show();
    }
}