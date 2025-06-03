package com.example.schoolapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolapp.adapters.TeacherClassAdapter;
import com.example.schoolapp.data_access.ClassDA;
import com.example.schoolapp.data_access.ClassDAFactory;
import com.example.schoolapp.json_helpers.LocalDateAdapter;
import com.example.schoolapp.models.Class;
import com.example.schoolapp.models.Teacher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.util.List;

public class SelectClass extends AppCompatActivity {

    private RecyclerView rvClasses;
    private TextView tvTeacher, tvId;
    private Teacher teacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_class);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        defineViews();
        getTeacherData();
        getClasses();
    }

    private void getClasses() {
        ClassDAFactory.getClassDA(this).getTeacherClasses(teacher.getUser_id(), new ClassDA.ClassListCallback() {
            @Override
            public void onSuccess(List<Class> list) {
                TeacherClassAdapter adapter = new TeacherClassAdapter(SelectClass.this, list, new TeacherClassAdapter.OnClassClickListener() {
                    @Override
                    public void onClassClick(Class selectedClass) {
                        Intent intent = new Intent(SelectClass.this, ClassDashboard.class);
                        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
                        String classString = gson.toJson(selectedClass);
                        String teacherString = gson.toJson(teacher);
                        intent.putExtra(AddSchedule.CLASS, classString);
                        intent.putExtra(AddSchedule.TEACHER, teacherString);
                        startActivity(intent);
                    }
                });
                rvClasses.setAdapter(adapter);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(SelectClass.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void getTeacherData() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(SelectClass.this);
        boolean isLoggedIn = pref.getBoolean("Logged_in", false);
        String teacherString = "";
        if (isLoggedIn)
            teacherString = pref.getString("Logged_in_user", "");
        else {
            Intent intent = new Intent(SelectClass.this, Login.class);
            startActivity(intent);
        }
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        teacher = gson.fromJson(teacherString, Teacher.class);
        tvTeacher.setText("Teacher: " + teacher.getFirstName() + " " + teacher.getLastName());
        tvId.setText("ID: " + teacher.getUser_id());
        Log.d("Date", "Date Before 2: " + teacher.getBirthDate().toString());
    }

    private void defineViews() {
        this.rvClasses = findViewById(R.id.rvClasses);
        this.tvTeacher = findViewById(R.id.tvTeacher);
        this.tvId = findViewById(R.id.tvId);
        rvClasses.setLayoutManager(new LinearLayoutManager(SelectClass.this));
    }
}