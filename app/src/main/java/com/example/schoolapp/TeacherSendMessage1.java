package com.example.schoolapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolapp.adapters.TeacherSendMessageAdapter;
import com.example.schoolapp.data_access.IStudentDA;
import com.example.schoolapp.data_access.StudentDA;
import com.example.schoolapp.data_access.StudentDAFactory;
import com.example.schoolapp.models.Student;

import java.util.ArrayList;
import java.util.List;

public class TeacherSendMessage1 extends AppCompatActivity {

    private EditText etStudentSearch;
    private Button btnSearch;
    private RecyclerView rvStudents;
    private Button btnCancel;

    private TeacherSendMessageAdapter studentAdapter;
    private List<Student> studentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_send_message);

        setupViews();
        setupRecyclerView();
        handleBtnSearch();

    }

    private void handleBtnSearch() {
        btnSearch.setOnClickListener(e-> {
        IStudentDA studentDA = StudentDAFactory.getStudentDA(this);
        studentDA.getAllStudents(new StudentDA.StudentListCallback() {
            @Override
            public void onSuccess(List<Student> list) {
                List<Student> queryRes = new ArrayList<>();
                String nameOrId = etStudentSearch.getText().toString();
                if(nameOrId == null || nameOrId.isEmpty()){
                    setupRecyclerView();
                }
                else{
                for(Student student : list){
                    if((String.valueOf(student.getUser_id()).contains(nameOrId)) || student.getFirstName().toLowerCase().contains(nameOrId.toLowerCase()) || student.getLastName().toLowerCase().contains(nameOrId.toLowerCase())){
                        queryRes.add(student);
                    }
                }
                setupRecyclerView(queryRes);
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(TeacherSendMessage1.this, "Could not load students: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        });
    }

    private void setupRecyclerView() {
        rvStudents.setLayoutManager(new LinearLayoutManager(this));
        IStudentDA studentDA = StudentDAFactory.getStudentDA(this);
        studentDA.getAllStudents(new StudentDA.StudentListCallback() {
            @Override
            public void onSuccess(List<Student> list) {
                studentList = list;
                studentAdapter = new TeacherSendMessageAdapter(studentList);
                rvStudents.setAdapter(studentAdapter);
                rvStudents.addItemDecoration(
                        new DividerItemDecoration(TeacherSendMessage1.this, DividerItemDecoration.VERTICAL)
                );
            }

            @Override
            public void onError(String error) {
                Toast.makeText(TeacherSendMessage1.this, "Could not load students: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView(List<Student> list) {
        rvStudents.setLayoutManager(new LinearLayoutManager(this));
        studentAdapter = new TeacherSendMessageAdapter(list);
        rvStudents.setAdapter(studentAdapter);
        rvStudents.addItemDecoration(
                new DividerItemDecoration(TeacherSendMessage1.this, DividerItemDecoration.VERTICAL)
        );
    }

    private void setupViews() {
        etStudentSearch = findViewById(R.id.etStudentSearch);
        btnSearch       = findViewById(R.id.btnSearch);
        rvStudents      = findViewById(R.id.rvStudents);
        btnCancel       = findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(v -> {
            finish();
        });
    }

}
