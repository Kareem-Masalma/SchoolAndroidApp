package com.example.schoolapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.schoolapp.json_helpers.LocalDateAdapter;
import com.example.schoolapp.models.Class;
import com.example.schoolapp.models.Role;
import com.example.schoolapp.models.Teacher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        Intent intent = new Intent(MainActivity.this, AddSchedule.class);
//        Intent intent = new Intent(MainActivity.this, AddStudent.class);


        // take attendance activity
//        Intent intent = new Intent(MainActivity.this, TakeAttendance.class);
//        SchoolClass schoolClass = new SchoolClass(1,"6-B",2,1);
//        Gson gson = new Gson();
//        String json = gson.toJson(schoolClass);
//        intent.putExtra("schoolClass", json);

//        Intent intent = new Intent(MainActivity.this, TeacherSendMessage1.class);

        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("user_id", 3);
        editor.apply();


        Intent intent = new Intent(MainActivity.this,AssignmentListActivity.class);
//        Class sClass = new Class(1, "10-A", 1, "John",11);
//        Teacher teacher = new Teacher(1,"Joan","Smith", LocalDate.parse("1980-03-15"), "123 Elm St","555-1001", Role.TEACHER,"Mathematics");
//        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
//        String json = gson.toJson(teacher);
//        intent.putExtra(AddSchedule.TEACHER, json);
//        String json1 = gson.toJson(sClass);
//        intent.putExtra(AddSchedule.CLASS, json1);

//        Intent intent = new Intent(MainActivity.this, AddTeacherActivity.class);

        startActivity(intent);
    }
}