package com.example.schoolapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.schoolapp.data_access.AssignmentDA;
import com.example.schoolapp.data_access.IAssignmentDA;
import com.example.schoolapp.data_access.SubjectDA;
import com.example.schoolapp.json_helpers.LocalDateAdapter;
import com.example.schoolapp.models.Subject;
import com.example.schoolapp.models.Teacher;
import com.example.schoolapp.models.SchoolClass;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SendAssignmentActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST_CODE = 1;

    private EditText editTitle, editDetails, editDeadline, editPercentage;
    private TextView textSelectedFiles;
    private Spinner spinnerSubject;
    private Button btnSelectFile, btnSend, btnCancel;
    private LinearLayout deadlineField, spinnerSubjectContainer;

    private final List<Uri> selectedFileUris = new ArrayList<>();
    private final List<JSONObject> classSubjectOptions = new ArrayList<>();
    private int selectedDeadlineYear, selectedDeadlineMonth, selectedDeadlineDay;

    private IAssignmentDA assignmentDA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_assignment);

        assignmentDA = new AssignmentDA(this);

        editTitle = findViewById(R.id.editTitle);
        editDetails = findViewById(R.id.editDetails);
        editDeadline = findViewById(R.id.editDeadline);
        editPercentage = findViewById(R.id.editPercentage);
        textSelectedFiles = findViewById(R.id.textSelectedFile);
        spinnerSubject = findViewById(R.id.spinnerClass);
        btnSelectFile = findViewById(R.id.btnSelectFile);
        btnSend = findViewById(R.id.btnSend);
        btnCancel = findViewById(R.id.btnCancel);
        deadlineField = findViewById(R.id.deadlineField);
        spinnerSubjectContainer = findViewById(R.id.spinnerSubjectContainer);

        spinnerSubjectContainer.setOnClickListener(v -> spinnerSubject.performClick());

        // --- Deserialize Teacher and Class from Intent Extras ---
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        Intent intent = getIntent();
        String teacherString = intent.getStringExtra(AddSchedule.TEACHER);
        String classString = intent.getStringExtra(AddSchedule.CLASS);
        Teacher teacher = gson.fromJson(teacherString, Teacher.class);
        SchoolClass selectClass = gson.fromJson(classString, SchoolClass.class);

        // --- Load subjects for teacher and selected class ---
        SubjectDA subjectDA = new SubjectDA(this);
        subjectDA.getClassTeacherSubject(selectClass.getClassId(), teacher.getUser_id(), new SubjectDA.ClassSubjectCallback() {
            @Override
            public void onSuccess(List<Subject> list) {
                ArrayAdapter<Subject> adapter = new ArrayAdapter<>(SendAssignmentActivity.this,
                        android.R.layout.simple_spinner_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSubject.setAdapter(adapter);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(SendAssignmentActivity.this, "Failed to load subjects", Toast.LENGTH_SHORT).show();
            }
        });

        // --- Deadline Picker ---
        View.OnClickListener deadlinePicker = v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        selectedDeadlineYear = year;
                        selectedDeadlineMonth = month;
                        selectedDeadlineDay = dayOfMonth;
                        editDeadline.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            dialog.show();
        };
        editDeadline.setOnClickListener(deadlinePicker);
        deadlineField.setOnClickListener(deadlinePicker);

        // --- File Picker ---
        btnSelectFile.setOnClickListener(v -> {
            Intent fileIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            fileIntent.setType("*/*");
            fileIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
            fileIntent.addCategory(Intent.CATEGORY_OPENABLE);
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            startActivityForResult(Intent.createChooser(fileIntent, "Select File"), PICK_FILE_REQUEST_CODE);
        });

        // --- Submit Assignment ---
        btnSend.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();
            String details = editDetails.getText().toString().trim();
            String deadline = editDeadline.getText().toString().trim();
            String percentageStr = editPercentage.getText().toString().trim();
            Subject subject = (Subject) spinnerSubject.getSelectedItem();

            if (title.isEmpty()) { editTitle.setError("Required"); return; }
            if (details.isEmpty()) { editDetails.setError("Required"); return; }
            if (deadline.isEmpty()) { editDeadline.setError("Required"); return; }
            if (percentageStr.isEmpty()) { editPercentage.setError("Required"); return; }

            float percentage;
            try {
                percentage = Float.parseFloat(percentageStr);
                if (percentage < 0 || percentage > 100) {
                    editPercentage.setError("Must be between 0 and 100"); return;
                }
            } catch (NumberFormatException e) {
                editPercentage.setError("Invalid number"); return;
            }

            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(selectedDeadlineYear, selectedDeadlineMonth, selectedDeadlineDay, 0, 0, 0);
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0); today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0); today.set(Calendar.MILLISECOND, 0);

            if (selectedDate.before(today)) {
                editDeadline.setError("Deadline must be today or in the future"); return;
            }

            assignmentDA.sendAssignment(
                    title,
                    details,
                    subject.getSubjectId(),
                    deadline,
                    percentage,
                    selectedFileUris,
                    new AssignmentDA.BaseCallback() {
                        @Override
                        public void onSuccess(String message) {
                            Toast.makeText(SendAssignmentActivity.this, message, Toast.LENGTH_SHORT).show();
                            clearFields();
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(SendAssignmentActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                        }
                    }
            );
        });

        btnCancel.setOnClickListener(v -> finish());
    }

    // --- Handle file picker result ---
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedFileUris.clear();
            Uri uri = data.getData();
            if (uri != null) {
                getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                selectedFileUris.add(uri);
                textSelectedFiles.setText("Selected File:\n• " + getFileName(uri));
                textSelectedFiles.setVisibility(View.VISIBLE);
            }
        }
    }

    private String getFileName(Uri uri) {
        String result = "file";
        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index >= 0) result = cursor.getString(index);
            }
        } catch (Exception e) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    private void clearFields() {
        editTitle.setText("");
        editDetails.setText("");
        editDeadline.setText("");
        editPercentage.setText("");
        spinnerSubject.setSelection(0);
        textSelectedFiles.setText("");
        textSelectedFiles.setVisibility(View.GONE);
        selectedFileUris.clear();
    }
}
