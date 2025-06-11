package com.example.schoolapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.schoolapp.data_access.ClassDA;
import com.example.schoolapp.data_access.ClassDAFactory;
import com.example.schoolapp.data_access.IClassDA;
import com.example.schoolapp.json_helpers.LocalDateAdapter;
import com.example.schoolapp.models.Registrar;
import com.example.schoolapp.models.Role;
import com.example.schoolapp.models.SchoolClass;
import com.example.schoolapp.models.Student;
import com.example.schoolapp.models.Teacher;
import com.example.schoolapp.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;

public class Profile extends AppCompatActivity {

    //all
    private CardView         cardInfo;
    private CardView         cardInbox;
    private ImageView        imageProfile;
    private TextView         textFullName;
    private TextView         textRole;
    private TextView         textBirthDate;
    private TextView         textAddress;
    private TextView         textPhone;


    //teacher/student
    private CardView         cardSchedule;

    //teacher
    private CardView         cardClasses;

    //registrar
    private CardView         cardAddStudent;
    private CardView         cardAddTeacher;
    private CardView         cardAddSubject;
    private CardView         cardBuildSchedule;

    //student
    private CardView         cardAssignments;
    private CardView         cardExams;
    private CardView         cardMarks;


    //all
    private FloatingActionButton fabMessage;
    User logged_in_user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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

                    logged_in_user = gson.fromJson(json, Student.class);
                    setupStudentViews();
                } else if (user.getRole().equals(Role.TEACHER)) {
                    setContentView(R.layout.activity_profile_teacher);
                    logged_in_user = gson.fromJson(json, Teacher.class);
                    setupTeacherViews();
                }else{
                    setContentView(R.layout.activity_profile_registrar);
                    logged_in_user = gson.fromJson(json, Registrar.class);
                    setupRegistrarViews();
                }

            }
        }


    }

    private void setupRegistrarViews() {
        cardAddStudent = findViewById(R.id.card_add_student);
        cardAddTeacher = findViewById(R.id.card_add_teacher);
        cardAddSubject = findViewById(R.id.card_add_subject);
        cardBuildSchedule = findViewById(R.id.card_build_schedule);

        cardAddStudent.setOnClickListener(e->{
            Intent intent = new Intent(Profile.this, AddStudents.class);
            startActivity(intent);
        });

        cardAddTeacher.setOnClickListener(e->{
            Intent intent = new Intent(Profile.this, AddTeacherActivity.class);
            startActivity(intent);
        });

        cardAddSubject.setOnClickListener(e->{
            Intent intent = new Intent(Profile.this, AddSubjects.class);
            startActivity(intent);
        });


        cardBuildSchedule.setOnClickListener(e->{
            Intent intent = new Intent(Profile.this, AddSchedule.class);
            startActivity(intent);
        });
    }

    private void setupStudentViews() {
        cardSchedule    = findViewById(R.id.card_schedule);
        cardAssignments = findViewById(R.id.card_assignments);
        cardExams       = findViewById(R.id.card_exams);
        cardMarks       = findViewById(R.id.card_marks);
        cardAssignments.setOnClickListener(e->{
            Intent intent = new Intent(Profile.this, AssignmentListActivity.class);
            intent.putExtra("USER_ID", logged_in_user.getUser_id());
            startActivity(intent);
        });


        cardExams.setOnClickListener(e->{
            Intent intent = new Intent(Profile.this, ExamListActivity.class);
            intent.putExtra("USER_ID", logged_in_user.getUser_id());
            startActivity(intent);
        });

        cardMarks.setOnClickListener(e->{
            Intent intent = new Intent(Profile.this, StudentCoursesActivity.class);
            startActivity(intent);
        });

        cardSchedule.setOnClickListener(e->{
            Intent intent = new Intent(Profile.this, ViewSchedule.class);
            int id = ((Student) logged_in_user).getClass_id();
            IClassDA classDA = ClassDAFactory.getClassDA(this);
            classDA.getClassById(id, new ClassDA.SingleClassCallback() {
                @Override
                public void onSuccess(SchoolClass c) {
                    Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
                    String json = gson.toJson(c,SchoolClass.class);
                    intent.putExtra(AddSchedule.CLASS, json);
                    startActivity(intent);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(Profile.this, "Error Occurred: " + error, Toast.LENGTH_SHORT).show();
                }
            });

        });
    }

    private void setupTeacherViews() {
        cardSchedule    = findViewById(R.id.card_schedule);
        cardClasses     = findViewById(R.id.card_classes);


        cardSchedule.setOnClickListener(e->{
            Intent intent = new Intent(Profile.this, ViewSchedule.class);
            startActivity(intent);
        });

        cardClasses.setOnClickListener(e->{
            Intent intent = new Intent(Profile.this, SelectClass.class);
            startActivity(intent);
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
        fabMessage      = findViewById(R.id.fab_send_message);
        cardInbox       = findViewById(R.id.card_inbox);
        fabMessage.setOnClickListener(e->{
            Intent intent = new Intent(Profile.this, UserSendMessage1.class);
            startActivity(intent);
        });

        if(logged_in_user!=null){
            textFullName.setText(logged_in_user.getFirstName() + " " + logged_in_user.getLastName());
//            Log.i("birth_date", logged_in_user.getBirthDate().toString());
            textBirthDate.setText(getString(R.string.personal_info_bdate) + logged_in_user.getBirthDate().toString());
            textAddress.setText(getString(R.string.personal_info_address) +logged_in_user.getAddress());
            textPhone.setText(getString(R.string.personal_info_phone) + logged_in_user.getPhone());
        }



        cardInbox.setOnClickListener(e->{
            Intent intent = new Intent(Profile.this, Inbox.class);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
            new android.app.AlertDialog.Builder(this)
                    .setTitle("Logout?")
                    .setMessage("You will be logged out of the application.")
                    .setPositiveButton("Yes, Logout", (dialog, which) -> super.onBackPressed())
                    .setNegativeButton("Stay", null)
                    .show();
    }
}
