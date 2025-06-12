package com.example.schoolapp;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.example.schoolapp.data_access.AssignmentDA;
import com.example.schoolapp.data_access.IAssignmentDA;
import com.example.schoolapp.data_access.SubjectDA;
import com.example.schoolapp.json_helpers.LocalDateAdapter;
import com.example.schoolapp.models.*;

import com.example.schoolapp.receivers.ReminderReceiver;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SendAssignmentActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST_CODE = 1;

    private EditText editTitle, editDetails, editDeadline, editPercentage, editReminderTime ;
    private TextView textSelectedFiles;
    private Spinner spinnerSubject;
    private Button btnSelectFile, btnSend, btnCancel;
    private LinearLayout deadlineField, spinnerSubjectContainer;

    private final List<Uri> selectedFileUris = new ArrayList<>();
    private int selectedDeadlineYear, selectedDeadlineMonth, selectedDeadlineDay;

    private IAssignmentDA assignmentDA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_assignment);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        assignmentDA = new AssignmentDA(this);

        editTitle = findViewById(R.id.editTitle);
        editDetails = findViewById(R.id.editDetails);
        editDeadline = findViewById(R.id.editDeadline);
        editPercentage = findViewById(R.id.editPercentage);
        editReminderTime = findViewById(R.id.editReminderTime);
        textSelectedFiles = findViewById(R.id.textSelectedFile);
        spinnerSubject = findViewById(R.id.spinnerClass);
        btnSelectFile = findViewById(R.id.btnSelectFile);
        btnSend = findViewById(R.id.btnSend);
        btnCancel = findViewById(R.id.btnCancel);
        deadlineField = findViewById(R.id.deadlineField);
        spinnerSubjectContainer = findViewById(R.id.spinnerSubjectContainer);


        spinnerSubjectContainer.setOnClickListener(v -> spinnerSubject.performClick());

        // --- Load Teacher object from SharedPreferences ---
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String teacherJson = prefs.getString(Login.LOGGED_IN_USER, null);
        if (teacherJson == null) {
            Toast.makeText(this, "User not found. Please login again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        Teacher teacher = gson.fromJson(teacherJson, Teacher.class);

        // --- Deserialize Class from Intent Extra ---
        Intent intent = getIntent();
        String classString = intent.getStringExtra(AddSchedule.CLASS);
        SchoolClass selectedClass = gson.fromJson(classString, SchoolClass.class);

        // --- Load subjects for this teacher & class ---
        SubjectDA subjectDA = new SubjectDA(this);
        subjectDA.getClassTeacherSubject(selectedClass.getClassId(), teacher.getUser_id(), new SubjectDA.ClassSubjectCallback() {
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
            Calendar calendar = Calendar.getInstance(); // today

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

            // Set minimum date to today
            dialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

            // Set maximum date to 4 months from today
            Calendar maxDate = (Calendar) calendar.clone();
            maxDate.add(Calendar.MONTH, 4);
            dialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

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

        // --- Send Assignment ---
        btnSend.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();
            String details = editDetails.getText().toString().trim();
            String deadline = editDeadline.getText().toString().trim();
            String reminderStr = editReminderTime.getText().toString().trim();
            int hour = 8, minute = 0; // default

            if (!reminderStr.isEmpty() && reminderStr.matches("\\d{2}:\\d{2}")) {
                String[] timeParts = reminderStr.split(":");
                hour = Integer.parseInt(timeParts[0]);
                minute = Integer.parseInt(timeParts[1]);
            }

            String percentageStr = editPercentage.getText().toString().trim();
            Subject subject = (Subject) spinnerSubject.getSelectedItem();

            if (title.isEmpty()) { editTitle.setError("Required"); return; }
            if (deadline.isEmpty()) { editDeadline.setError("Required"); return; }
            if (percentageStr.isEmpty()) { editPercentage.setError("Required"); return; }

            if (details.isEmpty() && selectedFileUris.isEmpty()) {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("Missing Information")
                        .setMessage("Please provide either assignment details or attach a file (or both).")
                        .setPositiveButton("OK", null)
                        .show();
                return;
            }

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
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            if (selectedDate.before(today)) {
                editDeadline.setError("Deadline must be today or in the future"); return;
            }

            int finalMinute = minute;
            int finalHour = hour;
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
                            scheduleReminder(title, deadline, finalHour, finalMinute);
                            Toast.makeText(SendAssignmentActivity.this, message, Toast.LENGTH_SHORT).show();
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("assignment_sent", true);
                            setResult(RESULT_OK, resultIntent);
                            clearFields();
                            finish();
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(SendAssignmentActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                        }
                    }
            );
        });

        View.OnClickListener reminderTimePicker = v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog dialog = new TimePickerDialog(
                    SendAssignmentActivity.this,
                    (view, selectedHour, selectedMinute) -> {
                        String time = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                        editReminderTime.setText(time);
                    },
                    hour,
                    minute,
                    true
            );
            dialog.show();
        };

        editReminderTime.setOnClickListener(reminderTimePicker);
        findViewById(R.id.reminderTimeField).setOnClickListener(reminderTimePicker);

        btnCancel.setOnClickListener(v -> {
            if (hasUnsavedInput()) {
                showDiscardChangesDialog();
            } else {
                finish(); // just go back
            }
        });
    }

    // --- File picker result ---
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

    @Override
    public void onBackPressed() {
        if (hasUnsavedInput()) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Discard Changes?")
                .setMessage("You have unsaved input. Are you sure you want to cancel and lose your data?")
                .setPositiveButton("Yes, Discard", (dialog, which) -> super.onBackPressed())
                .setNegativeButton("No", null)
                .show();
        } else {
            super.onBackPressed();
        }
    }

    private void showDiscardChangesDialog() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Discard Changes?")
                .setMessage("You have unsaved input. Are you sure you want to cancel and lose your data?")
                .setPositiveButton("Yes, Discard", (dialog, which) -> finish())
                .setNegativeButton("No", null)
                .show();
    }
    void scheduleReminder(String title, String deadlineDate, int hour, int minute) {
        try {
            String[] parts = deadlineDate.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]) - 1;
            int day = Integer.parseInt(parts[2]);

            Calendar reminderTime = Calendar.getInstance();
            reminderTime.set(Calendar.YEAR, year);
            reminderTime.set(Calendar.MONTH, month);
            reminderTime.set(Calendar.DAY_OF_MONTH, day);
            reminderTime.set(Calendar.HOUR_OF_DAY, hour);
            reminderTime.set(Calendar.MINUTE, minute);
            reminderTime.set(Calendar.SECOND, 0);
            reminderTime.set(Calendar.MILLISECOND, 0);

            Log.d("Reminder", String.format("Scheduled: %d-%02d-%02d %02d:%02d (millis: %d)",
                    year, month + 1, day, hour, minute, reminderTime.getTimeInMillis()));

            if (reminderTime.getTimeInMillis() < System.currentTimeMillis()) {
                Log.w("Reminder", "Reminder is in the past. Not setting it.");
                return;
            }

            Intent intent = new Intent(this, ReminderReceiver.class);
            intent.putExtra("title", "Assignment Reminder");
            intent.putExtra("message", "Do not forget assignment \"" + title + "\" is due soon!");

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    (int) System.currentTimeMillis(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,reminderTime.getTimeInMillis(), pendingIntent);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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
    private boolean hasUnsavedInput() {
        return !editTitle.getText().toString().trim().isEmpty()
                || !editDetails.getText().toString().trim().isEmpty()
                || !editDeadline.getText().toString().trim().isEmpty()
                || !editPercentage.getText().toString().trim().isEmpty()
                || !selectedFileUris.isEmpty();
    }

}
