package com.example.schoolapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolapp.adapters.ScheduleSubjectAdapter;
import com.example.schoolapp.data_access.ClassDA;
import com.example.schoolapp.data_access.ClassDAFactory;
import com.example.schoolapp.data_access.ScheduleDA;
import com.example.schoolapp.data_access.ScheduleDAFactory;
import com.example.schoolapp.data_access.SubjectDA;
import com.example.schoolapp.data_access.SubjectDAFactory;
import com.example.schoolapp.models.Class;

import com.example.schoolapp.data_access.DaysFactory;
import com.example.schoolapp.models.ScheduleSubject;
import com.example.schoolapp.models.Subject;
import com.example.schoolapp.models.Teacher;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class AddTeacherSchedule extends AppCompatActivity {


    private Button btnAdd, btnCancel;
    private EditText etStartTime, etEndTime;
    private Spinner spSubject, spGrade, spDay;
    private TextView tvTeacher, tvId;
    private RecyclerView rvScheduleItems;
    private int teacherScheduleId = -1;
    private Teacher teacher;
    private List<ScheduleSubject> teacherSchedules;
    ScheduleDA scheduleDA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_teacher_schedule);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        defineViews();
        teacherData();
        getSpinnersData();
        loadTeacherSchedule(teacher.getSchedule_id());
        actionButtons();
    }


    private void actionButtons() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Class selectedClass = (Class) spGrade.getSelectedItem();
                Subject subject = (Subject) spSubject.getSelectedItem();
                String day = spDay.getSelectedItem().toString();

                String startTime = etStartTime.getText().toString();
                String endTime = etEndTime.getText().toString();

                Log.d("Schedule", "Class: " + selectedClass.getClassName());
                Log.d("Schedule", "Subject: " + subject.getTitle());
                Log.d("Schedule", "Day: " + day);
                Log.d("Schedule", "Start Time:" + startTime);
                Log.d("Schedule", "End Time: " + endTime);

                ScheduleSubject schedule = new ScheduleSubject(teacher.getSchedule_id(), subject.getSubjectId(), selectedClass.getClassId(),
                        subject.getTitle(), selectedClass.getClassName(), day, startTime, endTime);


                if (teacherSchedules.isEmpty()) {
                    teacherSchedules.add(schedule);
                    scheduleDA.addScheduleSubject(schedule, new ScheduleDA.ScheduleCallback() {
                        @Override
                        public void onSuccess(String message) {
                            Toast.makeText(AddTeacherSchedule.this, message, Toast.LENGTH_SHORT).show();
                            loadTeacherSchedule(teacher.getSchedule_id());
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(AddTeacherSchedule.this, error, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    for (ScheduleSubject curr : teacherSchedules) {
                        if (checkConflict(curr, schedule)) {
                            Toast.makeText(AddTeacherSchedule.this, "Conflict with Schedule", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    scheduleDA.addScheduleSubject(schedule, new ScheduleDA.ScheduleCallback() {
                        @Override
                        public void onSuccess(String message) {
                            loadTeacherSchedule(teacher.getSchedule_id());
                            Toast.makeText(AddTeacherSchedule.this, message, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(AddTeacherSchedule.this, error, Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }

    private boolean checkConflict(ScheduleSubject curr, ScheduleSubject newSchedule) {
        String startTime = curr.getStartTime();
        String endTime = curr.getEndTime();

        int startMinutes = getMinutes(startTime);
        int endMinutes = getMinutes(endTime);

        String newStartTime = newSchedule.getStartTime();
        String newEndTime = newSchedule.getEndTime();

        int newStartMinutes = getMinutes(newStartTime);
        int newEndMinutes = getMinutes(newEndTime);

        return ((newStartMinutes < endMinutes) && (newEndMinutes > startMinutes)) && curr.getDay().equalsIgnoreCase(newSchedule.getDay());
    }

    private int getMinutes(String time) {
        String[] splitTime = time.split(":");

        int hours = Integer.parseInt(splitTime[0]);
        int minutes = Integer.parseInt(splitTime[1]);

        return hours * 60 + minutes;
    }

    private void loadTeacherSchedule(int scheduleId) {
        ScheduleSubjectAdapter adapter = new ScheduleSubjectAdapter(new ArrayList<>());
        rvScheduleItems.setAdapter(adapter);

        scheduleDA = ScheduleDAFactory.getScheduleDA(AddTeacherSchedule.this);
        scheduleDA.getScheduleById(scheduleId, new ScheduleDA.ScheduleListCallback() {
            @Override
            public void onSuccess(List<ScheduleSubject> list) {
                if (!list.isEmpty()) {
                    teacherSchedules = list;
                    adapter.updateData(list);
                    rvScheduleItems.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(AddTeacherSchedule.this, "No schedule entries found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AddTeacherSchedule.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getSpinnersData() {
        String[] days = DaysFactory.getDays();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(AddTeacherSchedule.this, android.R.layout.simple_list_item_1, days);
        spDay.setAdapter(adapter);

        ClassDAFactory.getClassDA(AddTeacherSchedule.this).getAllClasses(new ClassDA.ClassListCallback() {
            @Override
            public void onSuccess(List<Class> list) {
                ArrayAdapter<Class> classesAdapter = new ArrayAdapter<>(AddTeacherSchedule.this, android.R.layout.simple_list_item_1, list);
                spGrade.setAdapter(classesAdapter);
            }

            @Override
            public void onError(String error) {
                Log.d("Error", error);
            }
        });

        SubjectDAFactory.getSubjectDA(AddTeacherSchedule.this).getAllSubjects(new SubjectDA.SubjectListCallback() {
            @Override
            public void onSuccess(List<Subject> list) {
                ArrayAdapter<Subject> adapter = new ArrayAdapter<>(AddTeacherSchedule.this, android.R.layout.simple_list_item_1, list);
                spSubject.setAdapter(adapter);
            }

            @Override
            public void onError(String error) {
                Log.d("Error", error);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void teacherData() {
        Intent intent = getIntent();
        String teacherString = intent.getStringExtra(AddSchedule.TEACHER);
        Gson gson = new Gson();
        teacher = gson.fromJson(teacherString, Teacher.class);
        tvTeacher.setText("Teacher: " + teacher.getFirstName() + " " + teacher.getLastName());
        tvId.setText("ID: " + teacher.getUser_id());
    }

    private void defineViews() {
        this.btnAdd = findViewById(R.id.btnAddSchedule);
        this.btnCancel = findViewById(R.id.btnCancel);
        this.etStartTime = findViewById(R.id.etStartTime);
        this.spSubject = findViewById(R.id.spSubject);
        this.spGrade = findViewById(R.id.spGrade);
        this.spDay = findViewById(R.id.spDay);
        this.etEndTime = findViewById(R.id.etEndTime);
        this.tvTeacher = findViewById(R.id.tvTeacher);
        this.tvId = findViewById(R.id.tvId);
        this.rvScheduleItems = findViewById(R.id.rvScheduleItems);
        this.rvScheduleItems.setLayoutManager(new LinearLayoutManager(this));
    }
}

