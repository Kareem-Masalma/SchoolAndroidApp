package com.example.schoolapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolapp.data_access.IStudentDA;
import com.example.schoolapp.data_access.StudentDA;
import com.example.schoolapp.data_access.StudentDAFactory;
import com.example.schoolapp.models.Student;

import java.util.List;

public class TakeAttendance extends AppCompatActivity {

    // View references
    private TextView tvTitle;
    private TextView tvClassLabel;
    private TextView tvClassName;
    private TextView tvDateLabel;
    private Button btnPickDate;
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

    }

    private void setupViews() {
        tvTitle        = findViewById(R.id.tvTitle);
        tvClassLabel   = findViewById(R.id.tvClassLabel);
        tvClassName   = findViewById(R.id.tvClassName);
        tvDateLabel    = findViewById(R.id.tvDateLabel);
        btnPickDate    = findViewById(R.id.btnPickDate);
        rvStudents     = findViewById(R.id.rvStudents);
        btnCancel      = findViewById(R.id.btnCancel);
        btnFinish      = findViewById(R.id.btnFinish);
    }

    private void setupRecyclerView() {
        // Example RecyclerView setup; replace `students` & `StudentAdapter` with your data/adapter
        rvStudents.setLayoutManager(new LinearLayoutManager(this));
//        IStudentDA studentDA = StudentDAFactory.getStudentDA(this);
//        List<Student> students = studentDA.getAllStudents();
        // StudentAdapter adapter = new StudentAdapter(students);
        // rvStudents.setAdapter(adapter);
    }
}
