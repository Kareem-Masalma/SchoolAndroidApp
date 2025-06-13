package com.example.schoolapp;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.example.schoolapp.json_helpers.LocalDateAdapter;
import com.example.schoolapp.models.SchoolClass;

import com.example.schoolapp.data_access.DaysFactory;
import com.example.schoolapp.models.Schedule;
import com.example.schoolapp.models.ScheduleSubject;
import com.example.schoolapp.models.Subject;
import com.example.schoolapp.models.Teacher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddTeacherSchedule extends AppCompatActivity {


    private Button btnAdd, btnCancel;
    private EditText etStartTime, etEndTime;
    private Spinner spSubject, spGrade, spDay, spSemester;
    private TextView tvTeacher, tvId;
    private RecyclerView rvScheduleItems;
    private Teacher teacher;
    private List<ScheduleSubject> teacherSchedules;
    ScheduleDA scheduleDA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_teacher_schedule);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        defineViews();
        teacherData();
        getSpinnersData();
        scheduleDA = ScheduleDAFactory.getScheduleDA(AddTeacherSchedule.this);
        if (teacher.getSchedule_id() != 0)
            loadTeacherSchedule(teacher.getSchedule_id());
        else
            createNewSchedule();
        actionButtons();
    }

    private void createNewSchedule() {
        scheduleDA.addTeacherScheduleID(teacher.getUser_id(), new ScheduleDA.ScheduleIDCallback() {
            @Override
            public void onSuccess(int newId) {
                teacher.setSchedule_id(newId);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AddTeacherSchedule.this, error, Toast.LENGTH_SHORT).show();
            }
        });
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
                SchoolClass selectedClass = (SchoolClass) spGrade.getSelectedItem();
                Log.d("Teacher", "Class id after: " + selectedClass.getClassId());
                Subject subject = (Subject) spSubject.getSelectedItem();
                String day = spDay.getSelectedItem().toString();

                String start = etStartTime.getText().toString().trim();
                String end = etEndTime.getText().toString().trim();

                if (!Schedule.isValidTimeFormat(start) || !Schedule.isValidTimeFormat(end)) {
                    Toast.makeText(AddTeacherSchedule.this, "Please enter time in HH:mm format", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!Schedule.isTimeRangeValid(start, end)) {
                    Toast.makeText(AddTeacherSchedule.this, "Start time must be before end time", Toast.LENGTH_SHORT).show();
                    return;
                }
                String semester = spSemester.getSelectedItem().toString();
                int year = LocalDate.now().getYear();


                ScheduleSubject schedule = new ScheduleSubject(teacher.getSchedule_id(), subject.getSubjectId(), selectedClass.getClassId(),
                        subject.getTitle(), selectedClass.getClassName(), day, start, end, semester, year);


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
                        if (Schedule.checkConflict(curr, schedule)) {
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

    private void loadTeacherSchedule(int scheduleId) {
        ScheduleSubjectAdapter adapter = new ScheduleSubjectAdapter(new ArrayList<>());
        rvScheduleItems.setAdapter(adapter);

        scheduleDA.getScheduleById(scheduleId, new ScheduleDA.ScheduleListCallback() {
            @Override
            public void onSuccess(List<ScheduleSubject> list) {
                if (!list.isEmpty()) {
                    teacherSchedules = list;
                    adapter.updateData(list);
                    rvScheduleItems.setVisibility(View.VISIBLE);
                } else {
                    teacherSchedules = new ArrayList<>();
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

        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(AddTeacherSchedule.this, android.R.layout.simple_list_item_1, new String[]{"Spring", "Summer", "Winter", "Fall"});
        spSemester.setAdapter(semesterAdapter);

        ClassDAFactory.getClassDA(AddTeacherSchedule.this).getAllClasses(new ClassDA.ClassListCallback() {
            @Override
            public void onSuccess(List<SchoolClass> list) {
                ArrayAdapter<SchoolClass> classesAdapter = new ArrayAdapter<>(AddTeacherSchedule.this, android.R.layout.simple_list_item_1, list);
                spGrade.setAdapter(classesAdapter);
            }

            @Override
            public void onError(String error) {
                Log.d("Error", error);
            }
        });

        spGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SchoolClass selectedClass = (SchoolClass) spGrade.getSelectedItem();
                if (selectedClass != null) {
                    int classId = selectedClass.getClassId();

                    SubjectDA subjectDA = SubjectDAFactory.getSubjectDA(AddTeacherSchedule.this);
                    subjectDA.getClassSubject(classId, new SubjectDA.ClassSubjectCallback() {
                        @Override
                        public void onSuccess(List<Subject> list) {
                            ArrayAdapter<Subject> adapter = new ArrayAdapter<>(AddTeacherSchedule.this, android.R.layout.simple_list_item_1, list);
                            spSubject.setAdapter(adapter);
                            spSubject.setEnabled(true);
                        }

                        @Override
                        public void onError(String error) {
                            spSubject.setEnabled(false);
                            Toast.makeText(AddTeacherSchedule.this, "No subjects found for this class", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spSubject.setEnabled(false);
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void teacherData() {
        Intent intent = getIntent();
        String teacherString = intent.getStringExtra(AddSchedule.TEACHER);
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        teacher = gson.fromJson(teacherString, Teacher.class);
        tvTeacher.setText("Teacher: " + teacher.getFirstName() + " " + teacher.getLastName());
        tvId.setText("ID: " + teacher.getUser_id());
    }

    private void showTimePicker(EditText targetEditText) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePicker = new TimePickerDialog(this,
                (view, selectedHour, selectedMinute) -> {
                    String formatted = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                    targetEditText.setText(formatted);
                },
                hour, minute, true);

        timePicker.show();
    }

    private void defineViews() {
        this.btnAdd = findViewById(R.id.btnAddSchedule);
        this.btnCancel = findViewById(R.id.btnCancel);
        this.etStartTime = findViewById(R.id.etStartTime);
        this.spSubject = findViewById(R.id.spSubject);
        this.spGrade = findViewById(R.id.spGrade);
        this.spDay = findViewById(R.id.spDay);
        this.spSemester = findViewById(R.id.spSemester);
        this.etEndTime = findViewById(R.id.etEndTime);
        this.tvTeacher = findViewById(R.id.tvTeacher);
        this.tvId = findViewById(R.id.tvId);
        this.rvScheduleItems = findViewById(R.id.rvScheduleItems);
        this.rvScheduleItems.setLayoutManager(new LinearLayoutManager(this));
        spSubject.setEnabled(false);
        etStartTime.setOnClickListener(v -> showTimePicker(etStartTime));
        etEndTime.setOnClickListener(v -> showTimePicker(etEndTime));
    }
}

