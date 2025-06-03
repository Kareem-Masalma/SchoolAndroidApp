package com.example.schoolapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolapp.adapters.ClassAdapter;
import com.example.schoolapp.adapters.TeacherAdapter;
import com.example.schoolapp.data_access.ClassDA;
import com.example.schoolapp.data_access.ClassDAFactory;
import com.example.schoolapp.data_access.TeacherDAFactory;
import com.example.schoolapp.data_access.TeacherDA;
import com.example.schoolapp.models.Teacher;
import com.example.schoolapp.models.Class;
import com.google.gson.Gson;

import java.util.List;

public class AddSchedule extends AppCompatActivity {


    private RadioGroup rdRole;
    private RecyclerView rvUsers;
    public static final String CLASS = "Class";
    public static final String TEACHER = "Teacher";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable((AppCompatActivity) this);
        setContentView(R.layout.activity_add_schedule);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        defineViews();
        getUsers();
    }

    private void defineViews() {
        rdRole = findViewById(R.id.rdRoles);
        rvUsers = findViewById(R.id.rvUsers);
    }

    private void getUsers() {
        rdRole.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton selectedRole = findViewById(checkedId);
                String role = selectedRole.getText().toString();
                if ("Teacher".equals(role)) {
                    TeacherDAFactory.getTeacherDA(AddSchedule.this).getAllTeachers(new TeacherDA.TeacherListCallback() {
                        @Override
                        public void onSuccess(List<Teacher> teachers) {
                            rvUsers.setLayoutManager(new LinearLayoutManager(AddSchedule.this));
                            rvUsers.setAdapter(new TeacherAdapter(teachers, new TeacherAdapter.OnTeacherClickListener() {
                                @Override
                                public void onTeacherClick(Teacher teacher) {
                                    Intent intent = new Intent(AddSchedule.this, AddTeacherSchedule.class);
                                    Gson gson = new Gson();
                                    String teacherString = gson.toJson(teacher);
                                    intent.putExtra(AddSchedule.TEACHER, teacherString);
                                    startActivity(intent);
                                }
                            }));
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(AddSchedule.this, "Error loading teachers: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if ("Class".equals(role)) {
                    ClassDAFactory.getClassDA(AddSchedule.this).getAllClasses(new ClassDA.ClassListCallback() {
                        @Override
                        public void onSuccess(List<Class> classes) {
                            rvUsers.setLayoutManager(new LinearLayoutManager(AddSchedule.this));
                            rvUsers.setAdapter(new ClassAdapter(classes, new ClassAdapter.OnClassClickListener() {
                                @Override
                                public void onClassClick(Class c) {
                                    Intent intent = new Intent(AddSchedule.this, AddClassSchedule.class);
                                    Gson gson = new Gson();
                                    String studentString = gson.toJson(c);
                                    intent.putExtra(AddSchedule.CLASS, studentString);
                                    startActivity(intent);
                                }
                            }));
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(AddSchedule.this, "Error loading classes: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}