package com.example.schoolapp;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.schoolapp.data_access.DAFactory;
import com.example.schoolapp.data_access.TeacherDA;
import com.example.schoolapp.models.Teacher;

import java.util.List;

public class AddSchedule extends AppCompatActivity {


    private RadioGroup rdRole;
    private ScrollView svUsers;

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
    }

    private void defineViews() {
        rdRole = findViewById(R.id.rdRoles);
        svUsers = findViewById(R.id.svUsers);
    }

    private void getUsers() {
        rdRole.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton selectedRole = findViewById(checkedId);
                String role = selectedRole.getText().toString();
                if ("Teacher".equals(role)) {
                    DAFactory.getTeacherDA(AddSchedule.this).getAllTeachers(new TeacherDA.TeacherListCallback() {
                        @Override
                        public void onSuccess(List<Teacher> teachers) {

                        }

                        @Override
                        public void onError(String error) {

                        }
                    });
                } else if ("Student".equals(role)) {

                }
            }
        });
    }
}