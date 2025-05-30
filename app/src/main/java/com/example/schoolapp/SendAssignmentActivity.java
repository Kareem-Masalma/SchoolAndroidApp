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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SendAssignmentActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST_CODE = 1;

    private EditText editTitle, editDetails, editDeadline;
    private TextView textSelectedFiles;
    private Spinner spinnerClass;
    private Button btnSelectFile, btnSend, btnCancel;
    private LinearLayout deadlineField, spinnerClassContainer;

    private final List<Uri> selectedFileUris = new ArrayList<>();
    private int selectedDeadlineYear, selectedDeadlineMonth, selectedDeadlineDay;

    private final String[] classList = {
            "Select Class", "Class 1", "Class 2", "Class 3", "Class 4"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_assignment);

        // Bind views
        editTitle = findViewById(R.id.editTitle);
        editDetails = findViewById(R.id.editDetails);
        editDeadline = findViewById(R.id.editDeadline);
        textSelectedFiles = findViewById(R.id.textSelectedFile);
        spinnerClass = findViewById(R.id.spinnerClass);
        btnSelectFile = findViewById(R.id.btnSelectFile);
        btnSend = findViewById(R.id.btnSend);
        btnCancel = findViewById(R.id.btnCancel);
        deadlineField = findViewById(R.id.deadlineField);
        spinnerClassContainer = findViewById(R.id.spinnerClassContainer);

        // Spinner setup
        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classList);
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(classAdapter);

        spinnerClassContainer.setOnClickListener(v -> spinnerClass.performClick());

        // Deadline date picker
        View.OnClickListener deadlinePicker = v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        selectedDeadlineYear = year;
                        selectedDeadlineMonth = month;
                        selectedDeadlineDay = dayOfMonth;
                        String date = year + "-" + (month + 1) + "-" + dayOfMonth;
                        editDeadline.setText(date);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            dialog.show();
        };
        editDeadline.setOnClickListener(deadlinePicker);
        deadlineField.setOnClickListener(deadlinePicker);

        // File picker (single file)
        btnSelectFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false); // one file only
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FILE_REQUEST_CODE);
        });

        // Send button
        btnSend.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();
            String details = editDetails.getText().toString().trim();
            String deadline = editDeadline.getText().toString().trim();
            String selectedClass = spinnerClass.getSelectedItem().toString();

            if (title.isEmpty()) {
                editTitle.setError("Required");
                return;
            }
            if (details.isEmpty()) {
                editDetails.setError("Required");
                return;
            }
            if (selectedClass.equals("Select Class")) {
                Toast.makeText(this, "Please select a class", Toast.LENGTH_SHORT).show();
                return;
            }
            if (deadline.isEmpty()) {
                editDeadline.setError("Required");
                return;
            }

            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(selectedDeadlineYear, selectedDeadlineMonth, selectedDeadlineDay, 0, 0, 0);
            selectedDate.set(Calendar.MILLISECOND, 0);

            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            if (selectedDate.before(today)) {
                editDeadline.setError("Deadline must be today or in the future");
                return;
            }

            // Confirmation dialog
            StringBuilder message = new StringBuilder();
            message.append("Title: ").append(title).append("\n");
            message.append("Details: ").append(details).append("\n");
            message.append("Class: ").append(selectedClass).append("\n");
            message.append("Deadline: ").append(deadline).append("\n");

            if (!selectedFileUris.isEmpty()) {
                message.append("File:\n");
                for (Uri uri : selectedFileUris) {
                    message.append("• ").append(uri.getLastPathSegment()).append("\n");
                }
            } else {
                message.append("File: None\n");
            }

            new android.app.AlertDialog.Builder(this)
                    .setTitle("Confirm Assignment Submission")
                    .setMessage(message.toString().trim())
                    .setPositiveButton("Send", (dialog, which) -> {
                        Toast.makeText(this, "Assignment sent!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        btnCancel.setOnClickListener(v -> finish());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedFileUris.clear();
            StringBuilder fileNames = new StringBuilder("Selected File:\n");

            Uri uri = data.getData();
            if (uri != null) {
                getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                selectedFileUris.add(uri);
                fileNames.append("• ").append(getFileName(uri));
            }

            textSelectedFiles.setText(fileNames.toString().trim());
            textSelectedFiles.setVisibility(View.VISIBLE);
        }
    }

    private String getFileName(Uri uri) {
        String result = "";
        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex >= 0) {
                    result = cursor.getString(nameIndex);
                }
            }
        } catch (Exception e) {
            result = uri.getLastPathSegment();
        }
        return result != null ? result : "Unnamed File";
    }
}
