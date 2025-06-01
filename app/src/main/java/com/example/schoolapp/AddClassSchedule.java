package com.example.schoolapp;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
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
import com.example.schoolapp.data_access.DaysFactory;
import com.example.schoolapp.data_access.ScheduleDA;
import com.example.schoolapp.data_access.ScheduleDAFactory;
import com.example.schoolapp.data_access.SubjectDA;
import com.example.schoolapp.data_access.SubjectDAFactory;
import com.example.schoolapp.models.Class;
import com.example.schoolapp.models.Schedule;
import com.example.schoolapp.models.ScheduleSubject;
import com.example.schoolapp.models.Subject;
import com.example.schoolapp.models.Teacher;
import com.google.gson.Gson;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddClassSchedule extends AppCompatActivity {

    private TextView tvClass, tvId;
    private Spinner spSubject, spDay, spSemester;
    private EditText etStartTime, etEndTime;
    private RecyclerView rvScheduleItems;
    private Button btnAdd, btnCancel;
    private Class selectedClass;
    private List<ScheduleSubject> classSchedules;
    private ScheduleDA scheduleDA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_class_schedule);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        defineViews();
        classData();
        getSpinnerData();
        if (selectedClass.getClassId() != 0)
            loadClassSchedule(selectedClass.getScheduleId());
        else
            addClassScheduleId();
        actionButtons();
    }

    private void showTimePicker(EditText targetEditText) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePicker = new TimePickerDialog(this,
                (view, selectedHour, selectedMinute) -> {
                    // Format to HH:mm
                    String formatted = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                    targetEditText.setText(formatted);
                },
                hour, minute, true);

        timePicker.show();
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
                Subject subject = (Subject) spSubject.getSelectedItem();
                String day = spDay.getSelectedItem().toString();
                String semester = spSemester.getSelectedItem().toString();
                String start = etStartTime.getText().toString().trim();
                String end = etEndTime.getText().toString().trim();

                if (!Schedule.isValidTimeFormat(start) || !Schedule.isValidTimeFormat(end)) {
                    Toast.makeText(AddClassSchedule.this, "Please enter time in HH:mm format", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!Schedule.isTimeRangeValid(start, end)) {
                    Toast.makeText(AddClassSchedule.this, "Start time must be before end time", Toast.LENGTH_SHORT).show();
                    return;
                }

                int year = LocalDate.now().getYear();
                ScheduleSubject schedule = new ScheduleSubject(selectedClass.getScheduleId(), subject.getSubjectId(), selectedClass.getClassId(),
                        subject.getTitle(), selectedClass.getClassName(), day, start, end, semester, year);

                if (classSchedules.isEmpty()) {
                    classSchedules.add(schedule);
                    scheduleDA.addScheduleSubject(schedule, new ScheduleDA.ScheduleCallback() {
                        @Override
                        public void onSuccess(String message) {
                            Toast.makeText(AddClassSchedule.this, message, Toast.LENGTH_SHORT).show();
                            loadClassSchedule(selectedClass.getScheduleId());
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(AddClassSchedule.this, error, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    for (ScheduleSubject curr : classSchedules) {
                        if (Schedule.checkConflict(curr, schedule)) {
                            Toast.makeText(AddClassSchedule.this, "Conflict with Schedule", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    scheduleDA.addScheduleSubject(schedule, new ScheduleDA.ScheduleCallback() {
                        @Override
                        public void onSuccess(String message) {
                            loadClassSchedule(selectedClass.getScheduleId());
                            Toast.makeText(AddClassSchedule.this, message, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(AddClassSchedule.this, error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void addClassScheduleId() {
        scheduleDA.addClassScheduleID(selectedClass.getClassId(), new ScheduleDA.ScheduleIDCallback() {
            @Override
            public void onSuccess(int newId) {
                selectedClass.setScheduleId(newId);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AddClassSchedule.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadClassSchedule(int scheduleId) {
        ScheduleSubjectAdapter adapter = new ScheduleSubjectAdapter(new ArrayList<>());
        rvScheduleItems.setAdapter(adapter);

        scheduleDA = ScheduleDAFactory.getScheduleDA(AddClassSchedule.this);
        scheduleDA.getScheduleById(scheduleId, new ScheduleDA.ScheduleListCallback() {
            @Override
            public void onSuccess(List<ScheduleSubject> list) {
                if (!list.isEmpty()) {
                    classSchedules = list;
                    adapter.updateData(list);
                    rvScheduleItems.setVisibility(View.VISIBLE);
                } else {
                    classSchedules = new ArrayList<>();
                    Toast.makeText(AddClassSchedule.this, "No schedule entries found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AddClassSchedule.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getSpinnerData() {
        String[] days = DaysFactory.getDays();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(AddClassSchedule.this, android.R.layout.simple_list_item_1, days);
        spDay.setAdapter(adapter);

        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(AddClassSchedule.this, android.R.layout.simple_list_item_1, new String[]{"Spring", "Summer", "Winter", "Fall"});
        spSemester.setAdapter(semesterAdapter);

        SubjectDAFactory.getSubjectDA(AddClassSchedule.this).getClassSubject(selectedClass.getClassId(), new SubjectDA.ClassSubjectCallback() {
            @Override
            public void onSuccess(List<Subject> list) {
                ArrayAdapter<Subject> adapter = new ArrayAdapter<>(AddClassSchedule.this, android.R.layout.simple_list_item_1, list);
                spSubject.setAdapter(adapter);
            }

            @Override
            public void onError(String error) {
                Log.d("Error", error);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void classData() {
        Intent intent = getIntent();
        String classString = intent.getStringExtra(AddSchedule.CLASS);
        Gson gson = new Gson();
        selectedClass = gson.fromJson(classString, Class.class);
        tvClass.setText("Class: " + selectedClass.getClassName());
        tvId.setText("ID: " + selectedClass.getClassId());
    }

    private void defineViews() {
        this.tvClass = findViewById(R.id.tvClass);
        this.tvId = findViewById(R.id.tvId);
        this.spSubject = findViewById(R.id.spSubject);
        this.spDay = findViewById(R.id.spDay);
        this.spSemester = findViewById(R.id.spSemester);
        this.etStartTime = findViewById(R.id.etStartTime);
        this.etEndTime = findViewById(R.id.etEndTime);
        this.rvScheduleItems = findViewById(R.id.rvScheduleItems);
        this.btnAdd = findViewById(R.id.btnAddSchedule);
        this.btnCancel = findViewById(R.id.btnCancel);
        rvScheduleItems.setLayoutManager(new LinearLayoutManager(this));
        etStartTime.setOnClickListener(v -> showTimePicker(etStartTime));
        etEndTime.setOnClickListener(v -> showTimePicker(etEndTime));
    }
}