package com.example.schoolapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.schoolapp.data_access.*;
import com.example.schoolapp.models.*;

import java.time.LocalDate;
import java.util.Calendar;

public class AddTeacherActivity extends AppCompatActivity {

    private EditText editFirstName, editLastName, editBirthDate, editCity, editAddress, editPhone;
    private Spinner spinnerSpecialty;
    private TextView textRole;
    private Button btnAdd, btnCancel;

    private final String[] specialties = {
            "Select Specialty", "Math", "Physics", "Chemistry", "Biology",
            "English", "Technology", "Science", "Arabic", "Religion",
            "Programming", "Social Studies", "geography"
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

        LinearLayout spinnerContainer = findViewById(R.id.spinnerContainer);

        spinnerContainer.setOnClickListener(v -> spinnerSpecialty.performClick());


        // Populate Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, specialties);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSpecialty.setAdapter(adapter);



        // Date picker for birth date field
        editBirthDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String dateString = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                        editBirthDate.setText(dateString);
                    },
                    year, month, day
            );
            dialog.show();
        });
        LinearLayout birthDateField = findViewById(R.id.birthDateField);
        EditText editBirthDate = findViewById(R.id.editBirthDate);

        birthDateField.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        String date = year + "-" + (month + 1) + "-" + dayOfMonth;
                        editBirthDate.setText(date);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            dialog.show();
        });


        // Handle Add button
        btnAdd.setOnClickListener(v -> {
            String firstName = editFirstName.getText().toString().trim();
            String lastName = editLastName.getText().toString().trim();
            String birthDateStr = editBirthDate.getText().toString().trim();
            String city = editCity.getText().toString().trim();
            String address = editAddress.getText().toString().trim();
            String phone = editPhone.getText().toString().trim();
            String specialty = spinnerSpecialty.getSelectedItem().toString();

            boolean valid = true;

            // Reset previous errors
            editFirstName.setError(null);
            editLastName.setError(null);
            editBirthDate.setError(null);
            editCity.setError(null);
            editAddress.setError(null);
            editPhone.setError(null);

            // Validate empty fields
            if (firstName.isEmpty()) {
                editFirstName.setError("Required");
                valid = false;
            }

            if (lastName.isEmpty()) {
                editLastName.setError("Required");
                valid = false;
            }

            if (birthDateStr.isEmpty()) {
                editBirthDate.setError("Required");
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

            // Validate phone format
            if (!phone.matches("^05\\d{8}$")) {
                editPhone.setError("Phone must start with 05 and be 10 digits");
                return;
            }

            // Validate birth date is not in the future
            LocalDate birthDate;
            try {
                birthDate = LocalDate.parse(birthDateStr);
                if (birthDate.isAfter(LocalDate.now())) {
                    editBirthDate.setError("Birth date cannot be in the future");
                    return;
                }
            } catch (Exception e) {
                editBirthDate.setError("Invalid date format");
                return;
            }

            // Validate specialty selection
            if (specialty.equals("Select Specialty")) {
                Toast.makeText(this, "Please select a valid specialty", Toast.LENGTH_SHORT).show();
                spinnerSpecialty.requestFocus();
                return;
            }

            // Construct Teacher object
            Role role = Role.TEACHER;
            Teacher teacher = new Teacher();
            teacher.setFirstName(firstName);
            teacher.setLastName(lastName);
            teacher.setBirthDate(birthDate);
            teacher.setAddress(address);
            teacher.setPhone(phone);
            teacher.setRole(role);
            teacher.setSpeciality(specialty);
            // If your model has city, add: teacher.setCity(city);

            // Send to DAO
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
