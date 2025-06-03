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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SendAssignmentActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST_CODE = 1;

    private EditText editTitle, editDetails, editDeadline, editPercentage;
    private TextView textSelectedFiles;
    private Spinner spinnerClass;
    private Button btnSelectFile, btnSend, btnCancel;
    private LinearLayout deadlineField, spinnerClassContainer;

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
        spinnerClass = findViewById(R.id.spinnerClass);
        btnSelectFile = findViewById(R.id.btnSelectFile);
        btnSend = findViewById(R.id.btnSend);
        btnCancel = findViewById(R.id.btnCancel);
        deadlineField = findViewById(R.id.deadlineField);
        spinnerClassContainer = findViewById(R.id.spinnerClassContainer);

        spinnerClassContainer.setOnClickListener(v -> spinnerClass.performClick());

        ((AssignmentDA) assignmentDA).getClassSubjectPairs(this, new AssignmentDA.ClassSubjectCallback() {
            @Override
            public void onSuccess(List<JSONObject> pairs, List<String> labels) {
                classSubjectOptions.clear();
                classSubjectOptions.addAll(pairs);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(SendAssignmentActivity.this,
                        android.R.layout.simple_spinner_item, labels);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerClass.setAdapter(adapter);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(SendAssignmentActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

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

        btnSelectFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FILE_REQUEST_CODE);
        });

        btnSend.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();
            String details = editDetails.getText().toString().trim();
            String deadline = editDeadline.getText().toString().trim();
            String percentageStr = editPercentage.getText().toString().trim();
            int selectedIndex = spinnerClass.getSelectedItemPosition();

            if (title.isEmpty()) { editTitle.setError("Required"); return; }
            if (details.isEmpty()) { editDetails.setError("Required"); return; }
            if (selectedIndex <= 0 || selectedIndex > classSubjectOptions.size()) {
                Toast.makeText(this, "Please select a class-subject", Toast.LENGTH_SHORT).show(); return;
            }
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

            JSONObject selectedPair = classSubjectOptions.get(selectedIndex - 1);
            String className = selectedPair.optString("class");

            assignmentDA.sendAssignment(
                    title,
                    details,
                    className,
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
        spinnerClass.setSelection(0);
        textSelectedFiles.setText("");
        textSelectedFiles.setVisibility(View.GONE);
        selectedFileUris.clear();
    }
}
