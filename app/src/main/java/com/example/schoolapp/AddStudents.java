package com.example.schoolapp;

import android.app.DatePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.schoolapp.data_access.IStudentDA;
import com.example.schoolapp.data_access.StudentDA;
import com.example.schoolapp.data_access.StudentDAFactory;
import com.example.schoolapp.models.Role;
import com.example.schoolapp.models.Student;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AddStudents extends AppCompatActivity {

    private TextInputEditText etFirstName;
    private TextInputEditText etLastName;
    private TextInputEditText etBirthDate;
    private TextInputEditText etAddress;
    private TextInputEditText etPhone;
    private Spinner          spinnerClass;
    private Button           btnSave;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_students);

        setupViews();
        setupSaveButton();
    }

    private void setupViews() {
        etFirstName   = findViewById(R.id.etFirstName);
        etLastName    = findViewById(R.id.etLastName);
        etBirthDate   = findViewById(R.id.etBirthDate);
        etAddress     = findViewById(R.id.etAddress);
        etPhone       = findViewById(R.id.etPhone);
        spinnerClass  = findViewById(R.id.spinnerClassNum);
        btnSave       = findViewById(R.id.btnSave);

        etBirthDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year  = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day   = cal.get(Calendar.DAY_OF_MONTH);

            new DatePickerDialog(
                    AddStudents.this,
                    (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                        // month is zero-based in DatePicker
                        LocalDate pickedDate = LocalDate.of(
                                selectedYear,
                                selectedMonth + 1,
                                selectedDay
                        );
                        etBirthDate.setText(pickedDate.format(dateFormatter));
                    },
                    year, month, day
            ).show();
        });

        fillSpinner();
    }

    private void fillSpinner() { // fill it from 1 to 12

        List<String> classNumbers = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            classNumbers.add(String.valueOf(i));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                classNumbers
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(adapter);
    }


    private void setupSaveButton() {
        btnSave.setOnClickListener(v -> {

            String first_name  = etFirstName.getText().toString().trim();
            String last_name   = etLastName .getText().toString().trim();
            LocalDate birth_date  = LocalDate.parse(etBirthDate.getText().toString());
            String address   = etAddress   .getText().toString().trim();
            String phone  = etPhone     .getText().toString().trim();
            Integer class_id = Integer.valueOf((String) spinnerClass.getSelectedItem());

            IStudentDA studentDA = StudentDAFactory.getStudentDA(this);
            Student student = new Student(0,first_name,last_name,birth_date,address,phone, Role.STUDENT,class_id);
            studentDA.addStudent(student, new StudentDA.BaseCallback() {
                @Override
                public void onSuccess(String message) {
                    // called when the HTTP POST returns { "success": true, "message": "Student added" }
                    runOnUiThread(() -> {
                        Toast.makeText(
                                AddStudents.this,
                                "Student added successfully!",
                                Toast.LENGTH_SHORT
                        ).show();
                        finish(); // close the Activity
                    });
                }

                @Override
                public void onError(String error) {
                    // called on network failure or server-side error
                    runOnUiThread(() -> {
                        Toast.makeText(
                                AddStudents.this,
                                "Failed to add student: " + error,
                                Toast.LENGTH_LONG
                        ).show();
                    });
                }
            });


        });
    }
}
