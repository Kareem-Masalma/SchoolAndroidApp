package com.example.schoolapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.schoolapp.AddTeacherActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Immediately go to AddTeacherActivity
        Intent intent = new Intent(MainActivity.this, AddTeacherActivity.class);
        startActivity(intent);

        // Optional: finish MainActivity so user can't go back to it
        finish();
    }
}
