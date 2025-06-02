package com.example.schoolapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.schoolapp.models.Role;
import com.example.schoolapp.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

public class Profile extends AppCompatActivity {

    // 1) Declare member variables for every view you want to initialize
    private ImageView        imageProfile;
    private TextView         textFullName;
    private TextView         textRole;
    private TextView         textBirthDate;
    private TextView         textAddress;
    private TextView         textPhone;
    private CardView         cardInfo;
    private CardView         cardSchedule;
    private CardView         cardClasses;
    private FloatingActionButton fabEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        checkPrefs();


        setupViews();


    }

    private void checkPrefs() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Profile.this);
        boolean loggedin =  prefs.getBoolean(Login.LOGGED_IN, false);
        if(loggedin){
            String json = prefs.getString(Login.LOGGED_IN_USER , null);
            if(json != null){
                Gson gson = new Gson();
                User user = gson.fromJson(json, User.class);
                if(user.getRole().equals(Role.STUDENT)){
                    setContentView(R.layout.activity_profile_student);
                    setupStudentViews();
                } else if (user.getRole().equals(Role.TEACHER)) {
                    setContentView(R.layout.activity_profile_teacher);
                    setupTeacherViews();
                }else{
                    setContentView(R.layout.activity_profile_registrar);
                }
            }
        }


    }

    private void setupStudentViews() {
        cardSchedule    = findViewById(R.id.card_schedule);
        // TODO Open the view schedule activity
        cardSchedule.setOnClickListener(e->{

        });
    }

    private void setupTeacherViews() {
        cardSchedule    = findViewById(R.id.card_schedule);
        cardClasses     = findViewById(R.id.card_classes);


        // TODO Open the view schedule activity
        cardSchedule.setOnClickListener(e->{

        });

        // TODO Open the view classes activity
        cardClasses.setOnClickListener(e->{

        });
    }


    private void setupViews() {
        imageProfile    = findViewById(R.id.image_profile);
        textFullName    = findViewById(R.id.text_full_name);
        textRole        = findViewById(R.id.text_role);
        textBirthDate   = findViewById(R.id.text_birth_date);
        textAddress     = findViewById(R.id.text_address);
        textPhone       = findViewById(R.id.text_phone);
        cardInfo        = findViewById(R.id.card_info);
        fabEdit         = findViewById(R.id.fab_edit_profile);


    }
}
