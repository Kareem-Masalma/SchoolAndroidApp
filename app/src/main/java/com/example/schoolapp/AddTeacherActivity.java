// AddTeacherActivity.java
package com.example.schoolapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.schoolapp.data_access.ITeacherDA;
import com.example.schoolapp.data_access.TeacherDA;
import com.example.schoolapp.models.Role;
import com.example.schoolapp.models.Teacher;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;

public class AddTeacherActivity extends AppCompatActivity {

    private ViewFlipper viewFlipper;
    private Button btnNext, btnPrevious;
    private int currentStep = 0;

    // Step 1
    private EditText editFirstName, editLastName, editBirthDate;

    // Step 2
    private EditText editCity, editAddress, editPhone;

    // Step 3
    private Spinner spinnerSpecialty;
    private EditText editPassword;
    private TextView textRole;

    private final String[] specialties = {
            "Select Specialty", "Math", "Physics", "Chemistry", "Biology",
            "English", "Technology", "Science", "Arabic", "Religion",
            "Programming", "Social Studies", "Geography"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher);

        viewFlipper = findViewById(R.id.viewFlipper);
        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);

        // Step 1
        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editBirthDate = findViewById(R.id.editBirthDate);

        // Step 2
        editCity = findViewById(R.id.editCity);
        editAddress = findViewById(R.id.editAddress);
        editPhone = findViewById(R.id.editPhone);

        // Step 3
        editPassword = findViewById(R.id.editPassword);
        spinnerSpecialty = findViewById(R.id.spinnerSpecialty);
        textRole = findViewById(R.id.textRole);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, specialties);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSpecialty.setAdapter(adapter);

        editBirthDate.setOnClickListener(v -> showDatePicker());

        btnNext.setOnClickListener(v -> {
            if (!validateCurrentStep()) return;
            if (currentStep < 2) {
                currentStep++;
                viewFlipper.showNext();
                updateButtonState();
            } else {
                submitTeacher();
            }
        });

        btnPrevious.setOnClickListener(v -> {
            if (currentStep > 0) {
                currentStep--;
                viewFlipper.showPrevious();
                updateButtonState();
            }
        });

        updateButtonState();
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> editBirthDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void updateButtonState() {
        btnPrevious.setVisibility(currentStep == 0 ? View.INVISIBLE : View.VISIBLE);
        btnNext.setText(currentStep == 2 ? "Finish" : "Next");
    }

    private boolean validateCurrentStep() {
        switch (currentStep) {
            case 0:
                if (editFirstName.getText().toString().isEmpty()) {
                    editFirstName.setError("Required");
                    return false;
                }
                if (editLastName.getText().toString().isEmpty()) {
                    editLastName.setError("Required");
                    return false;
                }
                if (editBirthDate.getText().toString().isEmpty()) {
                    editBirthDate.setError("Required");
                    return false;
                }
                break;
            case 1:
                if (editCity.getText().toString().isEmpty()) {
                    editCity.setError("Required");
                    return false;
                }
                if (editAddress.getText().toString().isEmpty()) {
                    editAddress.setError("Required");
                    return false;
                }
                if (!editPhone.getText().toString().matches("^05\\d{8}$")) {
                    editPhone.setError("Phone must start with 05 and be 10 digits");
                    return false;
                }
                break;
            case 2:
                if (editPassword.getText().toString().length() < 8) {
                    editPassword.setError("Min 8 characters");
                    return false;
                }
                if (spinnerSpecialty.getSelectedItem().toString().equals("Select Specialty")) {
                    Toast.makeText(this, "Please select a valid specialty", Toast.LENGTH_SHORT).show();
                    return false;
                }
                break;
        }
        return true;
    }

    private void submitTeacher() {
        Teacher teacher = new Teacher();
        teacher.setFirstName(editFirstName.getText().toString());
        teacher.setLastName(editLastName.getText().toString());
        teacher.setBirthDate(LocalDate.parse(editBirthDate.getText().toString(), DateTimeFormatter.ofPattern("yyyy-M-d")));
        teacher.setAddress(editCity.getText().toString() + " " + editAddress.getText().toString());
        teacher.setPhone(editPhone.getText().toString());
        teacher.setPassword(editPassword.getText().toString());
        teacher.setRole(Role.TEACHER);
        teacher.setSpeciality(spinnerSpecialty.getSelectedItem().toString());

        new TeacherDA(this).addTeacher(teacher);
        Toast.makeText(this, "Teacher added successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}
