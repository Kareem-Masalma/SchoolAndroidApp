package com.example.schoolapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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


//        Intent intent = new Intent(MainActivity.this, TeacherSendMessage1.class);

//        Intent intent = new Intent(MainActivity.this, UserSendMessage1.class);

        Intent intent = new Intent(MainActivity.this, SelectClass.class);
        Teacher teacher = new Teacher(1, "John", "Smith", LocalDate.parse("1980-03-15"), "123 Elm St", "555-1001", Role.TEACHER, "Mathematics");
        Log.d("Date", "Date Before: " + teacher.getBirthDate());
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new com.example.schoolapp.json_helpers.LocalDateAdapter()).create();
        String json = gson.toJson(teacher);
//        intent.putExtra(AddSchedule.TEACHER, json);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("Logged_in_user", json);
        editor.putBoolean("Logged_in", true);
        editor.apply();


//        Intent intent = new Intent(MainActivity.this, AddTeacherActivity.class);

        startActivity(intent);
    }
}