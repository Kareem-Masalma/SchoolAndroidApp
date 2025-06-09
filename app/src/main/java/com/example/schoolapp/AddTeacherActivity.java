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
    private EditText editPassword;
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
        editPassword = findViewById(R.id.editPassword);
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

        // Birth date picker logic (youngest valid age = 5)
        View.OnClickListener birthDatePicker = v -> {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, -30);

            int defaultYear = calendar.get(Calendar.YEAR);
            int defaultMonth = calendar.get(Calendar.MONTH);
            int defaultDay = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        String date = year + "-" + (month + 1) + "-" + dayOfMonth;
                        editBirthDate.setText(date);
                        editBirthDate.clearFocus();
                    },
                    defaultYear,
                    defaultMonth,
                    defaultDay);

            Calendar maxDate = Calendar.getInstance();
            maxDate.add(Calendar.YEAR, -20);
            dialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

            Calendar minDate = Calendar.getInstance();
            minDate.add(Calendar.YEAR, -65);
            dialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

            dialog.show();
        };

        editBirthDate.setOnClickListener(birthDatePicker);
        LinearLayout birthDateField = findViewById(R.id.birthDateField);
        birthDateField.setOnClickListener(birthDatePicker);

        // Add Teacher button logic
        btnAdd.setOnClickListener(v -> {
            String firstName = editFirstName.getText().toString().trim();
            String lastName = editLastName.getText().toString().trim();
            String birthDateStr = editBirthDate.getText().toString().trim();
            String city = editCity.getText().toString().trim();
            String address = editAddress.getText().toString().trim();
            String phone = editPhone.getText().toString().trim();
            String specialty = spinnerSpecialty.getSelectedItem().toString();
            String password = editPassword.getText().toString().trim();

            boolean valid = true;

            editFirstName.setError(null);
            editLastName.setError(null);
            editBirthDate.setError(null);
            editCity.setError(null);
            editAddress.setError(null);
            editPhone.setError(null);
            editPassword.setError(null);

            if (firstName.isEmpty()) { editFirstName.setError("Required"); valid = false; }
            if (lastName.isEmpty()) { editLastName.setError("Required"); valid = false; }
            if (birthDateStr.isEmpty()) { editBirthDate.setError("Required"); valid = false; }
            if (city.isEmpty()) { editCity.setError("Required"); valid = false; }
            if (address.isEmpty()) { editAddress.setError("Required"); valid = false; }
            if (phone.isEmpty()) { editPhone.setError("Required"); valid = false; }
            if (password.isEmpty()) {
                editPassword.setError("Required");
                valid = false;
            } else if (password.length() < 8) {
                editPassword.setError("Password must be at least 8 characters");
                return;
            }

            if (!valid) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!phone.matches("^05\\d{8}$")) {
                editPhone.setError("Phone must start with 05 and be 10 digits");
                return;
            }

            // Birth date parsing and logic
            LocalDate birthDate;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
                birthDate = LocalDate.parse(birthDateStr, formatter);

                LocalDate today = LocalDate.now();
                LocalDate minAllowed = today.minusYears(65);
                LocalDate maxAllowed = today.minusYears(20);

                if (birthDate.isBefore(minAllowed) || birthDate.isAfter(maxAllowed)) {
                    editBirthDate.setError("Teacher must be between 20 and 65 years old");
                    return;
                }
            } catch (DateTimeParseException e) {
                editBirthDate.setError("Invalid date format");
                return;
            }


            if (specialty.equals("Select Specialty")) {
                Toast.makeText(this, "Please select a valid specialty", Toast.LENGTH_SHORT).show();
                spinnerSpecialty.requestFocus();
                return;
            }

            Role role = Role.TEACHER;
            Teacher teacher = new Teacher();
            teacher.setFirstName(firstName);
            teacher.setLastName(lastName);
            teacher.setBirthDate(birthDate);
            teacher.setAddress(city + " " + address);
            teacher.setPhone(phone);
            teacher.setRole(role);
            teacher.setSpeciality(specialty);
            teacher.setPassword(password);

            ITeacherDA teacherDA = new TeacherDA(this);
            teacherDA.addTeacher(teacher);

            Toast.makeText(AddTeacherActivity.this, "Teacher added successfully", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();

        });

        // Cancel button
        btnCancel.setOnClickListener(v -> {
            if (hasUnsavedInput()) {
                showDiscardChangesDialog();
            } else {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (hasUnsavedInput()) {
            showDiscardChangesDialog();
        } else {
            super.onBackPressed();
        }
    }

    private void clearFields() {
        editFirstName.setText("");
        editLastName.setText("");
        editBirthDate.setText("");
        editCity.setText("");
        editAddress.setText("");
        editPhone.setText("");
        editPassword.setText("");
        spinnerSpecialty.setSelection(0);
    }

    private void showDiscardChangesDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Discard Changes?")
                .setMessage("You have unsaved input. Are you sure you want to cancel and lose your data?")
                .setPositiveButton("Yes, Discard", (dialog, which) -> finish())
                .setNegativeButton("No", null)
                .show();
    }

    private boolean hasUnsavedInput() {
        return !editFirstName.getText().toString().trim().isEmpty()
                || !editLastName.getText().toString().trim().isEmpty()
                || !editBirthDate.getText().toString().trim().isEmpty()
                || !editCity.getText().toString().trim().isEmpty()
                || !editAddress.getText().toString().trim().isEmpty()
                || !editPhone.getText().toString().trim().isEmpty()
                || !editPassword.getText().toString().trim().isEmpty()
                || spinnerSpecialty.getSelectedItemPosition() != 0;
    }
}
