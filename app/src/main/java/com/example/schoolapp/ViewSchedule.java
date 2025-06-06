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

import com.example.schoolapp.adapters.ScheduleAdapter;
import com.example.schoolapp.data_access.ScheduleDA;
import com.example.schoolapp.data_access.ScheduleDAFactory;
import com.example.schoolapp.json_helpers.LocalDateAdapter;
import com.example.schoolapp.models.SchoolClass;

import com.example.schoolapp.models.ScheduleSubject;
import com.example.schoolapp.models.Teacher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.util.List;

public class ViewSchedule extends AppCompatActivity {

    private TextView tvUser, tvId;
    private RecyclerView rvSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_schedule);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        defineViews();
        getData();
    }

    @SuppressLint("SetTextI18n")
    private void getData() {
        Intent intent = getIntent();
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        if (intent.hasExtra(AddSchedule.CLASS)) {
            String classJson = intent.getStringExtra(AddSchedule.CLASS);
            SchoolClass selectedClass = gson.fromJson(classJson, SchoolClass.class);

            tvUser.setText("Class: " + selectedClass.getClassName());
            tvId.setText("ID: " + selectedClass.getClassId());

            loadSchedule(selectedClass.getScheduleId());
        } else {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            String teacherString = pref.getString(Login.LOGGED_IN_USER, "");
            if (!teacherString.isEmpty()) {
                Teacher teacher = gson.fromJson(teacherString, Teacher.class);
                tvUser.setText("Teacher: " + teacher.getFirstName() + " " + teacher.getLastName());
                tvId.setText("ID: " + teacher.getUser_id());

                loadSchedule(teacher.getSchedule_id());
            }
        }
    }

    private void loadSchedule(int scheduleId) {
        ScheduleDAFactory.getScheduleDA(ViewSchedule.this).getScheduleById(scheduleId, new ScheduleDA.ScheduleListCallback() {
            @Override
            public void onSuccess(List<ScheduleSubject> schedules) {
                ScheduleAdapter adapter = new ScheduleAdapter(schedules);
                rvSchedule.setAdapter(adapter);
                Log.d("Schedule", "Schedules size: " + schedules.size());
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ViewSchedule.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void defineViews() {
        this.tvId = findViewById(R.id.tvId);
        this.tvUser = findViewById(R.id.tvUser);
        this.rvSchedule = findViewById(R.id.rvSchedule);
        this.rvSchedule.setLayoutManager(new LinearLayoutManager(this));
    }
}