package com.example.schoolapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.schoolapp.data_access.IStudentDA;
import com.example.schoolapp.data_access.ISubjectDA;
import com.example.schoolapp.data_access.StudentDAFactory;
import com.example.schoolapp.data_access.SubjectDA;
import com.example.schoolapp.data_access.SubjectDAFactory;
import com.example.schoolapp.models.Subject;

import java.util.ArrayList;
import java.util.List;

public class AddSubjects extends AppCompatActivity {
    private EditText subNameET;
    private Spinner assignGradeSP;
    private Button cancleBT;
    private Button addBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_subjects);
        initializeViews();
        addButton();
        cancleButton();
    }

    private void initializeViews(){
        subNameET = findViewById(R.id.subNameET);
        assignGradeSP = findViewById(R.id.assignGradeSP);

        cancleBT = findViewById(R.id.cancleBT);
        addBT = findViewById(R.id.addBT);

        setupGradeSP();
    }

    private void setupGradeSP(){
        List<String> gradeNum = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            gradeNum.add(String.valueOf(i));
        }

        ArrayAdapter<String> gradeAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item, gradeNum);

        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assignGradeSP.setAdapter(gradeAdapter);
    }

    private void addButton(){
        addBT.setOnClickListener(v -> {
            Integer class_id = Integer.valueOf((String) assignGradeSP.getSelectedItem() +1);
            String title = subNameET.getText().toString().trim();


            ISubjectDA subjectDA = SubjectDAFactory.getSubjectDA(this);
            Subject subject = new Subject(0,class_id,title);

            subjectDA.addSubject(subject, new SubjectDA.BaseCallback() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(AddSubjects.this,
                            "subject add successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(AddSubjects.this,
                            "add failed", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }
    private void cancleButton() {
        cancleBT.setOnClickListener(v -> {
            finish();
        });
    }

}
