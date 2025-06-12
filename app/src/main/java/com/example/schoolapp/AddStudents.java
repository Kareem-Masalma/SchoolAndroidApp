package com.example.schoolapp;

import android.app.DatePickerDialog;
import android.content.pm.ActivityInfo;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.schoolapp.data_access.ClassDA;
import com.example.schoolapp.data_access.ClassDAFactory;
import com.example.schoolapp.data_access.IClassDA;
import com.example.schoolapp.data_access.IStudentDA;
import com.example.schoolapp.data_access.StudentDA;
import com.example.schoolapp.data_access.StudentDAFactory;
import com.example.schoolapp.models.Role;
import com.example.schoolapp.models.SchoolClass;
import com.example.schoolapp.models.Student;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AddStudents extends AppCompatActivity {

    private TextInputEditText etFirstName;
    private TextInputEditText etLastName;
    private EditText etBirthDate;
    private TextInputEditText etAddress;
    private TextInputEditText etPhone;
    private TextInputEditText etInitialPassword;

    private Spinner spinnerClass;
    private Button btnAdd;
    private Button btnCancel;
    private LinearLayout birthDateField;


    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_students);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setupViews();
        handleAddButton();
        handleCancelButton();
    }

    private void handleCancelButton() {
        btnCancel.setOnClickListener(e -> {
            finish();
        });
    }

    private void setupViews() {
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etBirthDate = findViewById(R.id.etBirthDate);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        spinnerClass = findViewById(R.id.spinnerClassNum);
        btnAdd = findViewById(R.id.btnAdd);
        btnCancel = findViewById(R.id.btnCancel);
        etInitialPassword = findViewById(R.id.etInitialPassword);
        birthDateField = findViewById(R.id.birthDateField);

        setupDatePicker();

        fillSpinner();
    }

    private void setupDatePicker() {
        View.OnClickListener openDatePicker = v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        String date = year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth);
                        etBirthDate.setText(date);
                        etBirthDate.clearFocus();
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        };

        etBirthDate.setOnClickListener(openDatePicker);
        birthDateField.setOnClickListener(openDatePicker);
    }

    private void fillSpinner() {
       IClassDA classDA =  ClassDAFactory.getClassDA(this);
        classDA.getAllClasses(new ClassDA.ClassListCallback() {
            @Override
            public void onSuccess(List<SchoolClass> list) {
                ArrayAdapter<SchoolClass> gradeAdapter = new ArrayAdapter<>(AddStudents.this, android.R.layout.simple_spinner_item, list);
                gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerClass.setAdapter(gradeAdapter);
            }

            @Override
            public void onError(String error) {
                Log.d("Error", error);
            }
        });
    }


    private void handleAddButton() {
        btnAdd.setOnClickListener(v -> {

            String first_name = etFirstName.getText().toString().trim();
            String last_name = etLastName.getText().toString().trim();
            LocalDate birth_date = LocalDate.parse(etBirthDate.getText().toString());
            String address = etAddress.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            Integer class_id = ((SchoolClass) spinnerClass.getSelectedItem()).getClassId();
            String initialPassword = etInitialPassword.getText().toString().trim();

            IStudentDA studentDA = StudentDAFactory.getStudentDA(this);
            Student student = new Student(0, first_name, last_name, birth_date, address, phone, Role.STUDENT, class_id, initialPassword);

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
