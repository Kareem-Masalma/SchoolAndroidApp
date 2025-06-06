package com.example.schoolapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.schoolapp.data_access.ClassDA;
import com.example.schoolapp.data_access.ClassDAFactory;
import com.example.schoolapp.data_access.ISubjectDA;
import com.example.schoolapp.data_access.SubjectDA;
import com.example.schoolapp.data_access.SubjectDAFactory;
import com.example.schoolapp.models.Class;
import com.example.schoolapp.models.Subject;

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
        ClassDAFactory.getClassDA(this).getAllClasses(new ClassDA.ClassListCallback() {
            @Override
            public void onSuccess(List<Class> list) {
                ArrayAdapter<Class> gradeAdapter = new ArrayAdapter<>(AddSubjects.this, android.R.layout.simple_spinner_item, list);
                gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                assignGradeSP.setAdapter(gradeAdapter);
            }

            @Override
            public void onError(String error) {
                Log.d("Error", error);
            }
        });


    }
    private void addButton(){
        addBT.setOnClickListener(v -> {
            Class selectedClass = (Class) assignGradeSP.getSelectedItem();
            String title = subNameET.getText().toString().trim();

            if(selectedClass == null){
                Toast.makeText(this, "Please select a class", Toast.LENGTH_SHORT).show();
                return;
            }

            if(title.isEmpty()){
                Toast.makeText(this, "Please enter subject name", Toast.LENGTH_SHORT).show();
                return;
            }

            Subject subject = new Subject(0,title, selectedClass.getClassId(), selectedClass.getClassName());
            ISubjectDA subjectDA = SubjectDAFactory.getSubjectDA(this);

            subjectDA.addSubject(subject, new SubjectDA.BaseCallback() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(AddSubjects.this,
                            "Subject add successfully", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(AddSubjects.this,
                            "Add failed", Toast.LENGTH_SHORT).show();

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
