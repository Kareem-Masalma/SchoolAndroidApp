package com.example.schoolapp;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolapp.adapters.DeleteAdapter;

import com.example.schoolapp.data_access.StudentDA;
import com.example.schoolapp.data_access.SubjectDA;
import com.example.schoolapp.data_access.TeacherDA;
import com.example.schoolapp.models.Student;
import com.example.schoolapp.models.Subject;
import com.example.schoolapp.models.Teacher;


import java.util.ArrayList;
import java.util.List;

public class DeleteUserOrSubjectActivity extends AppCompatActivity {
    private RecyclerView deleteItemsRV;
    private RadioGroup deleteFilterGroup;
    private RadioButton radioSubjects, radioStudents, radioTeachers;
    private Button backToProfile_BT;
    private DeleteAdapter<?> adapter;

    private StudentDA studentDA;
    private TeacherDA teacherDA;
    private SubjectDA subjectDA;

    private final List<Object> entities = new ArrayList<>();

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        initViews();
        updateFilter();
    }

    private void initViews() {

        deleteItemsRV = findViewById(R.id.deleteItemsRV);
        deleteFilterGroup = findViewById(R.id.deleteFilterGroup);
        radioStudents = findViewById(R.id.radioStudents);
        radioTeachers = findViewById(R.id.radioTeachers);
        radioSubjects = findViewById(R.id.radioSubjects);

        deleteItemsRV.setLayoutManager(new LinearLayoutManager(this));

        backToProfile_BT = findViewById(R.id.backToProfile_BT);


        studentDA = new StudentDA(this);
        teacherDA = new TeacherDA(this);
        subjectDA = new SubjectDA(this);
        deleteFilterGroup.setOnCheckedChangeListener((group, checkedId) -> updateFilter());

        backToProfile_BT.setOnClickListener(v -> finish());
    }


    private void loadSubjects() {
        subjectDA.getAllSubjects(new SubjectDA.SubjectListCallback() {
            @Override
            public void onSuccess(List<Subject> list) {
                adapter = new DeleteAdapter<>(DeleteUserOrSubjectActivity.this, list, id -> {
                    subjectDA.deleteSubject(id, new SubjectDA.BaseCallback() {
                        @Override
                        public void onSuccess(String msg) {
                            Toast.makeText(DeleteUserOrSubjectActivity.this, "Subject deleted", Toast.LENGTH_SHORT).show();
                            loadSubjects();
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(DeleteUserOrSubjectActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                });
                deleteItemsRV.setAdapter(adapter);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(DeleteUserOrSubjectActivity.this, "Failed to load subjects", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadStudents() {
        studentDA.getAllStudents(new StudentDA.StudentListCallback() {
            @Override
            public void onSuccess(List<Student> list) {
                adapter = new DeleteAdapter<>(DeleteUserOrSubjectActivity.this, list, id -> {
                    studentDA.deleteStudent(id, new StudentDA.BaseCallback() {
                        @Override
                        public void onSuccess(String msg) {
                            Toast.makeText(DeleteUserOrSubjectActivity.this, "Student deleted", Toast.LENGTH_SHORT).show();
                            loadStudents();
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(DeleteUserOrSubjectActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                });
                deleteItemsRV.setAdapter(adapter);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(DeleteUserOrSubjectActivity.this, "Failed to load students", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadTeachers() {
        teacherDA.getAllTeachers(new TeacherDA.TeacherListCallback() {
            @Override
            public void onSuccess(List<Teacher> list) {
                adapter = new DeleteAdapter<>(DeleteUserOrSubjectActivity.this, list, id -> {
                    teacherDA.deleteTeacher(id, new TeacherDA.BaseCallback() {
                        @Override
                        public void onSuccess(String msg) {
                            Toast.makeText(DeleteUserOrSubjectActivity.this, msg, Toast.LENGTH_SHORT).show();
                            loadTeachers();
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(DeleteUserOrSubjectActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                });
                deleteItemsRV.setAdapter(adapter);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(DeleteUserOrSubjectActivity.this, "Failed to load teachers", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateFilter() {
        if (radioSubjects.isChecked()) {
            loadSubjects();
        } else if (radioStudents.isChecked()) {
            loadStudents();
        } else {
            loadTeachers();
        }
    }

}
