package com.example.schoolapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolapp.adapters.StudentAttendanceAdapter;
import com.example.schoolapp.data_access.AttendanceDAFactory;
import com.example.schoolapp.data_access.AttendanceStudentDAFactory;
import com.example.schoolapp.data_access.IAttendanceDA;
import com.example.schoolapp.data_access.IAttendanceStudentDA;
import com.example.schoolapp.data_access.IStudentDA;
import com.example.schoolapp.data_access.StudentDA;
import com.example.schoolapp.data_access.StudentDAFactory;
import com.example.schoolapp.json_helpers.LocalDateAdapter;
import com.example.schoolapp.models.Attendance;
import com.example.schoolapp.models.Attendance_student;
import com.example.schoolapp.models.Class;
import com.example.schoolapp.models.SchoolClass;
import com.example.schoolapp.models.Student;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TakeAttendance extends AppCompatActivity {


    private TextView tvClassName;
    private EditText editLectureDate;
    private LinearLayout lectureDateField;
    private RecyclerView rvStudents;
    private Button btnCancel;
    private Button btnFinish;

    // this class expects an intent that contains a SchoolClass object
    private Class schoolClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_take_atendance);

        setupViews();
        getSchoolClass();
//        setupRecyclerView();
        setupDatePicker();
        handleBtnFinish();
        handleBtnCancel();
    }

    private void handleBtnCancel() {
        btnCancel.setOnClickListener(e -> {
            finish();
        });
    }

    private void handleBtnFinish() {
        btnFinish.setOnClickListener(e -> {
            LocalDate date = LocalDate.parse(editLectureDate.getText().toString());
            Attendance attendance = new Attendance(date, schoolClass.getClassId());
            IAttendanceDA attendanceDA = AttendanceDAFactory.getAttendanceDA(TakeAttendance.this);
            attendanceDA.addAttendance(attendance, new IAttendanceDA.BaseCallback() {
                @Override
                public void onSuccess(String message) {
                    attendance.setAttendance_id(Integer.parseInt(message));
                    for (int i = 0; i < rvStudents.getChildCount(); i++) {
                        StudentAttendanceAdapter.StudentViewHolder holder = (StudentAttendanceAdapter.StudentViewHolder) rvStudents.findViewHolderForAdapterPosition(i);
                        int student_id = Integer.parseInt(holder.tvStudentId.getText().toString().trim());
//                 Log.i("student_id", student_id+"");
                        boolean attended = holder.cbPresent.isChecked();
                        IStudentDA studentDA = StudentDAFactory.getStudentDA(TakeAttendance.this);
                        studentDA.getStudentById(student_id, new StudentDA.SingleStudentCallback() {
                            @Override
                            public void onSuccess(Student s) {
                                IAttendanceStudentDA attendanceStudentDA = AttendanceStudentDAFactory.getAttendanceStudentDA(TakeAttendance.this);
                                Attendance_student as = new Attendance_student(attendance.getAttendance_id(), s.getUser_id(), attended, "No excuse");
                                attendanceStudentDA.addAttendanceStudent(as, new IAttendanceStudentDA.BaseCallback() {
                                    @Override
                                    public void onSuccess(String message) {
                                        Toast.makeText(TakeAttendance.this, "Successfully Recorded Attendance!", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onError(String error) {
                                        Toast.makeText(TakeAttendance.this, "Error recording attendance student: " + error, Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }

                            @Override
                            public void onError(String error) {
                                Toast.makeText(TakeAttendance.this, "Error fetching student: " + error, Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(TakeAttendance.this, "Error adding attendance: " + error, Toast.LENGTH_SHORT).show();

                }
            });


        });
    }

    private void getSchoolClass() {
        Intent intent = getIntent();
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        String json = intent.getStringExtra("schoolClass");
        schoolClass = gson.fromJson(json, Class.class);
        tvClassName.setText(schoolClass.getClassName());
    }

    private void setupViews() {
        tvClassName = findViewById(R.id.tvClassName);
        editLectureDate = findViewById(R.id.editLectureDate);
        lectureDateField = findViewById(R.id.lectureDateField);
        rvStudents = findViewById(R.id.rvStudents);
        btnCancel = findViewById(R.id.btnCancel);
        btnFinish = findViewById(R.id.btnFinish);
    }

    private void setupRecyclerView() {
        rvStudents.setLayoutManager(new LinearLayoutManager(this));
        IStudentDA studentDA = StudentDAFactory.getStudentDA(this);

        studentDA.getAllStudents(new StudentDA.StudentListCallback() {
            @Override
            public void onSuccess(List<Student> students) {
                runOnUiThread(() -> {
                    // check if the student.class_id == schoolClass.class_id
                    List<Student> classStudents = new ArrayList<>();
                    for (Student student : students) {
                        if (student.getClass_id() == schoolClass.getClassId()) {
                            classStudents.add(student);
                        }
                    }
                    StudentAttendanceAdapter adapter = new StudentAttendanceAdapter(classStudents);
                    rvStudents.setAdapter(adapter);
                    Toast.makeText(TakeAttendance.this, "Displayed all students successfully", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(TakeAttendance.this, "Failed to receive students: " + error, Toast.LENGTH_LONG).show());
            }
        });
    }

    private void setupDatePicker() {
        View.OnClickListener openDatePicker = v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        String date = year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth);
                        editLectureDate.setText(date);
                        editLectureDate.clearFocus();
                        setupRecyclerView();
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        };

        editLectureDate.setOnClickListener(openDatePicker);
        lectureDateField.setOnClickListener(openDatePicker);
    }
}
