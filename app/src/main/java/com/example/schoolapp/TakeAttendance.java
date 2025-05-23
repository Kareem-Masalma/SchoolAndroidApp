package com.example.schoolapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolapp.data_access.IStudentDA;
import com.example.schoolapp.data_access.StudentDAFactory;

import java.util.Calendar;

public class TakeAttendance extends AppCompatActivity {


    private TextView tvTitle;
    private TextView tvClassLabel;
    private TextView tvClassName;
    private TextView tvDateLabel;
    private EditText editLectureDate;
    private LinearLayout lectureDateField;
    private RecyclerView rvStudents;
    private Button btnCancel;
    private Button btnFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_take_atendance);

        setupViews();
        setupRecyclerView();
        setupDatePicker();
    }

    private void setupViews() {
        tvTitle           = findViewById(R.id.tvTitle);
        tvClassLabel      = findViewById(R.id.tvClassLabel);
        tvClassName       = findViewById(R.id.tvClassName);
        tvDateLabel       = findViewById(R.id.tvDateLabel);
        editLectureDate   = findViewById(R.id.editLectureDate);
        lectureDateField  = findViewById(R.id.lectureDateField);
        rvStudents        = findViewById(R.id.rvStudents);
        btnCancel         = findViewById(R.id.btnCancel);
        btnFinish         = findViewById(R.id.btnFinish);
    }

    private void setupRecyclerView() {
        rvStudents.setLayoutManager(new LinearLayoutManager(this));
        // IStudentDA studentDA = StudentDAFactory.getStudentDA(this);
        // List<Student> students = studentDA.getAllStudents();
        // StudentAdapter adapter = new StudentAdapter(students);
        // rvStudents.setAdapter(adapter);
    }

    private void setupDatePicker() {
        View.OnClickListener openDatePicker = v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        String date = year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth);
                        editLectureDate.setText(date);
                        editLectureDate.clearFocus();
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
