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
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolapp.adapters.OnStudentClickListener;
import com.example.schoolapp.adapters.StudentAdapter;
import com.example.schoolapp.adapters.TeacherAdapter;
import com.example.schoolapp.data_access.StudentDAFactory;
import com.example.schoolapp.data_access.TeacherDAFactory;
import com.example.schoolapp.data_access.StudentDA;
import com.example.schoolapp.data_access.TeacherDA;
import com.example.schoolapp.models.Student;
import com.example.schoolapp.models.Teacher;
import com.google.gson.Gson;

import java.util.List;

public class AddSchedule extends AppCompatActivity {


    private RadioGroup rdRole;
    private RecyclerView rvUsers;
    public static final String STUDENT = "Student";
    public static final String TEACHER = "Teacher";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
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
                } else if ("Student".equals(role)) {
                    StudentDAFactory.getStudentDA(AddSchedule.this).getAllStudents(new StudentDA.StudentListCallback() {
                        @Override
                        public void onSuccess(List<Student> students) {
                            rvUsers.setAdapter(new StudentAdapter(students, new OnStudentClickListener() {
                                @Override
                                public void onStudentClick(Student student) {
                                    Intent intent = new Intent(AddSchedule.this, AddClassSchedule.class);
                                    Gson gson = new Gson();
                                    String studentString = gson.toJson(student);
                                    intent.putExtra(AddSchedule.STUDENT, studentString);
                                    startActivity(intent);
                                }
                            }));
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(AddSchedule.this, "Error loading students: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}