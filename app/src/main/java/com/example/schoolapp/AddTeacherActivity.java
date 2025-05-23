package com.example.schoolapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.*;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.schoolapp.data_access.*;
import com.example.schoolapp.models.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;

public class AddTeacherActivity extends AppCompatActivity {

    private EditText editFirstName, editLastName, editBirthDate, editCity, editAddress, editPhone;
    private Spinner spinnerSpecialty;
    private TextView textRole;
    private Button btnAdd, btnCancel;

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

        // Link UI elements
        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editBirthDate = findViewById(R.id.editBirthDate);
        editCity = findViewById(R.id.editCity);
        editAddress = findViewById(R.id.editAddress);
        editPhone = findViewById(R.id.editPhone);
        spinnerSpecialty = findViewById(R.id.spinnerSpecialty);
        textRole = findViewById(R.id.textRole);
        btnAdd = findViewById(R.id.btnAdd);
        btnCancel = findViewById(R.id.btnCancel);

        // Spinner container click opens dropdown
        LinearLayout spinnerContainer = findViewById(R.id.spinnerContainer);
        spinnerContainer.setOnClickListener(v -> spinnerSpecialty.performClick());

        // Populate Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, specialties);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSpecialty.setAdapter(adapter);

        // Birth date click listener
        View.OnClickListener openDatePicker = v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        String date = year + "-" + (month + 1) + "-" + dayOfMonth;
                        editBirthDate.setText(date);
                        editBirthDate.clearFocus();
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        };

        editBirthDate.setOnClickListener(openDatePicker);
        LinearLayout birthDateField = findViewById(R.id.birthDateField);
        birthDateField.setOnClickListener(openDatePicker);

        // Add Teacher
        btnAdd.setOnClickListener(v -> {
            String firstName = editFirstName.getText().toString().trim();
            String lastName = editLastName.getText().toString().trim();
            String birthDateStr = editBirthDate.getText().toString().trim();
            String city = editCity.getText().toString().trim();
            String address = editAddress.getText().toString().trim();
            String phone = editPhone.getText().toString().trim();
            String specialty = spinnerSpecialty.getSelectedItem().toString();

            boolean valid = true;

            // Clear previous errors
            editFirstName.setError(null);
            editLastName.setError(null);
            editBirthDate.setError(null);
            editCity.setError(null);
            editAddress.setError(null);
            editPhone.setError(null);

            // Validate required fields
            if (firstName.isEmpty()) {
                editFirstName.setError("Required");
                valid = false;
            }

            if (lastName.isEmpty()) {
                editLastName.setError("Required");
                valid = false;
            }

            if (birthDateStr.isEmpty()) {
                editBirthDate.setError("Required a valid date");
                valid = false;
            }

            if (city.isEmpty()) {
                editCity.setError("Required");
                valid = false;
            }

            if (address.isEmpty()) {
                editAddress.setError("Required");
                valid = false;
            }

            if (phone.isEmpty()) {
                editPhone.setError("Required");
                valid = false;
            }

            if (!valid) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Phone validation
            if (!phone.matches("^05\\d{8}$")) {
                editPhone.setError("Phone must start with 05 and be 10 digits");
                return;
            }

            // Birth date parsing and logic
            LocalDate birthDate;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
                birthDate = LocalDate.parse(birthDateStr, formatter);

                if (birthDate.isAfter(LocalDate.now())) {
                    editBirthDate.setError("Birth date cannot be in the future");
                    return;
                }
            } catch (DateTimeParseException e) {
                editBirthDate.setError("Invalid date format");
                return;
            }

            // Specialty check
            if (specialty.equals("Select Specialty")) {
                Toast.makeText(this, "Please select a valid specialty", Toast.LENGTH_SHORT).show();
                spinnerSpecialty.requestFocus();
                return;
            }

            // Create and send Teacher
            Role role = Role.teacher;
            Teacher teacher = new Teacher();
            teacher.setFirstName(firstName);
            teacher.setLastName(lastName);
            teacher.setBirthDate(birthDate);
            teacher.setAddress(city + " " + address);
            teacher.setPhone(phone);
            teacher.setRole(role);
            teacher.setSpeciality(specialty);

            ITeacherDA teacherDA = new TeacherDA(this);
            teacherDA.addTeacher(teacher, new TeacherDA.BaseCallback() {
                @Override
                public void onSuccess(String message) {
                    runOnUiThread(() -> {
                        Toast.makeText(AddTeacherActivity.this, "Teacher added", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> Toast.makeText(AddTeacherActivity.this, error, Toast.LENGTH_SHORT).show());
                }
            });
        });

        // Cancel button
        btnCancel.setOnClickListener(v -> finish());
    }
}
