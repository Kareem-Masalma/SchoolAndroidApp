package com.example.schoolapp;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolapp.adapters.StudentAssignmentResultAdapter;
import com.example.schoolapp.adapters.StudentExamResultAdapter;
import com.example.schoolapp.data_access.AssignmentDA;
import com.example.schoolapp.data_access.ExamDA;
import com.example.schoolapp.data_access.StudentAssignmentResultDA;
import com.example.schoolapp.data_access.StudentExamResultDA;
import com.example.schoolapp.json_helpers.LocalDateAdapter;
import com.example.schoolapp.models.Assignment;
import com.example.schoolapp.models.Exam;
import com.example.schoolapp.models.Student;
import com.example.schoolapp.models.StudentAssignmentResult;
import com.example.schoolapp.models.StudentExamResult;
import com.example.schoolapp.models.Subject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import androidx.recyclerview.widget.ConcatAdapter;



import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ViewStudentMarks extends AppCompatActivity {

    private TextView subjectTitleTV;
    private RecyclerView marksRV;
    private RadioGroup resultFilterGroup;
    private RadioButton radioAll, radioAssignments, radioExams;
    private Button btnBack;

    private TextView totalMarkTV;

    private int subjectId;
    private String subjectTitle;
    private int studentId;

    private final List<Assignment> assignmentList = new ArrayList<>();
    private final List<Exam> examList = new ArrayList<>();
    private final List<StudentAssignmentResult> assignmentResults = new ArrayList<>();
    private final List<StudentExamResult> examResults = new ArrayList<>();

    private StudentAssignmentResultAdapter assignmentAdapter;
    private StudentExamResultAdapter examAdapter;

    private boolean assignmentsLoaded = false;
    private boolean examsLoaded = false;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_student_marks);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        String subjectStr = getIntent().getStringExtra("SELECTED_SUBJECT");
        if (subjectStr != null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .create();
            Subject subject = gson.fromJson(subjectStr, Subject.class);
            subjectId = subject.getSubjectId();
            subjectTitle = subject.getTitle();
        } else {
            Toast.makeText(this, "No subject data found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // load student from SharedPrefs
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String userJson = prefs.getString(Login.LOGGED_IN_USER, null);
        Gson gsonUser = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
        Student student = gsonUser.fromJson(userJson, Student.class);
        studentId = student.getUser_id();

        initViews();
        subjectTitleTV.setText(subjectTitle);

        loadAssignmentData();
        loadExamData();
    }


    private void initViews() {
        subjectTitleTV = findViewById(R.id.subjectTitle_tv);
        marksRV = findViewById(R.id.marksRV);
        marksRV.setLayoutManager(new LinearLayoutManager(this));

        resultFilterGroup = findViewById(R.id.resultFilterGroup);
        radioAll = findViewById(R.id.radioAll);
        radioAssignments = findViewById(R.id.radioAssignments);
        radioExams = findViewById(R.id.radioExams);
        btnBack = findViewById(R.id.btnBackToCourses);
        totalMarkTV = findViewById(R.id.totalMarkTV);


        resultFilterGroup.setOnCheckedChangeListener((group, checkedId) -> updateFilter());
        btnBack.setOnClickListener(v -> finish());


        marksRV.setAdapter(new StudentAssignmentResultAdapter(this, new ArrayList<>(), new ArrayList<>(), result -> {}));
    }


    private void loadAssignmentData() {
        AssignmentDA assignmentDA = new AssignmentDA(this);
        assignmentDA.getAssignmentsBySubject(subjectId, new AssignmentDA.AssignmentListTitleCallback() {
            @Override
            public void onSuccess(List<Assignment> assignments) {
                assignmentList.clear();
                assignmentList.addAll(assignments);

                StudentAssignmentResultDA resultDA = new StudentAssignmentResultDA(ViewStudentMarks.this);

                resultDA.getAssignmentResults(studentId, subjectId, new StudentAssignmentResultDA.AssignmentResultCallback() {
                    @Override
                    public void onSuccess(List<StudentAssignmentResult> results) {
                        assignmentResults.clear();
                        assignmentResults.addAll(results);
                        assignmentsLoaded = true;
                        checkAndUpdateUI();

                    }

                    @Override
                    public void onError(String error) {
                        assignmentsLoaded = true;
                        Toast.makeText(ViewStudentMarks.this, "Error loading assignment results", Toast.LENGTH_SHORT).show();
                        checkAndUpdateUI();
                    }
                });
            }

            @Override
            public void onError(String error) {
                assignmentsLoaded = true;
                Toast.makeText(ViewStudentMarks.this, "Error loading assignments", Toast.LENGTH_SHORT).show();
                checkAndUpdateUI();
            }
        });
    }

    private void loadExamData() {
        ExamDA examDA = new ExamDA(this);

        examDA.getExamsBySubject(subjectId, new ExamDA.ExamTitleCallback() {
            @Override
            public void onSuccess(List<Exam> exams) {
                examList.clear();
                examList.addAll(exams);

                StudentExamResultDA resultDA = new StudentExamResultDA(ViewStudentMarks.this);
                resultDA.getExamResults(studentId, subjectId, new StudentExamResultDA.ExamResultCallback() {
                    @Override
                    public void onSuccess(List<StudentExamResult> results) {
                        examResults.clear();
                        examResults.addAll(results);
                        examsLoaded = true;
                        checkAndUpdateUI();
                    }

                    @Override
                    public void onError(String error) {
                        examsLoaded = true;
                        Toast.makeText(ViewStudentMarks.this, "Error loading exam results", Toast.LENGTH_SHORT).show();
                        checkAndUpdateUI();
                    }
                });
            }

            @Override
            public void onError(String error) {
                examsLoaded = true;
                Toast.makeText(ViewStudentMarks.this, "Error loading exams", Toast.LENGTH_SHORT).show();
                checkAndUpdateUI();
            }
        });
    }

    private void checkAndUpdateUI() {
        if (assignmentsLoaded || examsLoaded) {
            if ((!assignmentResults.isEmpty() && !assignmentList.isEmpty()) ||
                    (!examResults.isEmpty() && !examList.isEmpty())) {
                updateFilter();
            }
        }
    }

    private void updateFilter() {
        if (radioAssignments.isChecked()) {
            assignmentAdapter = new StudentAssignmentResultAdapter(this, assignmentResults, assignmentList, result -> {});
            marksRV.setAdapter(assignmentAdapter);

        } else if (radioExams.isChecked()) {
            examAdapter = new StudentExamResultAdapter(this, examResults, examList, result -> {});
            marksRV.setAdapter(examAdapter);

        } else {
            assignmentAdapter = new StudentAssignmentResultAdapter(this, assignmentResults, assignmentList, result -> {});
            examAdapter = new StudentExamResultAdapter(this, examResults, examList, result -> {});
            ConcatAdapter concatAdapter = new ConcatAdapter(examAdapter,assignmentAdapter);
            marksRV.setAdapter(concatAdapter);
        }
        updateTotalMark();
    }

    private void updateTotalMark() {
        float totalStudentMark = 0f;
        float totalOutOf = 0f;

        if (radioAssignments.isChecked() || radioAll.isChecked()) {
            for (StudentAssignmentResult result : assignmentResults) {
                Assignment assignment = findAssignmentById(result.getAssignment_id());
                if (assignment != null) {
                    totalOutOf += assignment.getPercentage_of_grade();
                    totalStudentMark += result.getMark();
                }
            }
        }

        if (radioExams.isChecked() || radioAll.isChecked()) {
            for (StudentExamResult result : examResults) {
                Exam exam = findExamById(result.getExam_id());
                if (exam != null) {
                    totalOutOf += exam.getPercentage();
                    totalStudentMark += result.getMark();
                }
            }
        }

        String text;
        if (totalOutOf == 0f) {
            text = "No marks available.";
        } else {
            text = String.format("Total: %.2f / %.0f", totalStudentMark, totalOutOf);
        }
        totalMarkTV.setText(text);
    }

    private Assignment findAssignmentById(int id) {
        for (Assignment a : assignmentList) {
            if (a.getAssignment_id() == id) return a;
        }
        return null;
    }

    private Exam findExamById(int id) {
        for (Exam e : examList) {
            if (e.getExamId() == id) return e;
        }
        return null;
    }

}




