package com.example.schoolapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.schoolapp.models.SchoolClass;
import com.google.gson.Gson;

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

//        Intent intent = new Intent(MainActivity.this, UserSendMessage1.class);

      // logout before we open login.class
        Intent intent = new Intent(MainActivity.this, Login.class);

//        Intent intent = new Intent(MainActivity.this, AddTeacherActivity.class);
//          Intent intent = new Intent(MainActivity.this, AddSubjects.class);


//        Intent intent = new Intent(MainActivity.this, AddSchedule.class);

//        Intent intent = new Intent(MainActivity.this, Profile.class);
        startActivity(intent);
    }

}