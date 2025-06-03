package com.example.schoolapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.schoolapp.json_helpers.LocalDateAdapter;
import com.example.schoolapp.models.Role;
import com.example.schoolapp.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;

public class Profile extends AppCompatActivity {

    private ImageView        imageProfile;
    private TextView         textFullName;
    private TextView         textRole;
    private TextView         textBirthDate;
    private TextView         textAddress;
    private TextView         textPhone;
    private CardView         cardInfo;
    private CardView         cardSchedule;
    private CardView         cardClasses;
    private FloatingActionButton fabMessage;
    User logged_in_user = null;

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
                Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
                User user = gson.fromJson(json, User.class);
                logged_in_user = user;
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
        fabMessage         = findViewById(R.id.fab_send_message);

        fabMessage.setOnClickListener(e->{
            Intent intent = new Intent(Profile.this, UserSendMessage1.class);
            startActivity(intent);
        });

        if(logged_in_user!=null){
            textFullName.setText(logged_in_user.getFirstName() + " " + logged_in_user.getLastName());
//            Log.i("birth_date", logged_in_user.getBirthDate().toString());
            textBirthDate.setText(logged_in_user.getBirthDate().toString());
            textAddress.setText(logged_in_user.getAddress());
            textPhone.setText(logged_in_user.getPhone());
        }
    }
}
