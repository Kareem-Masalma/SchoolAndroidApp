package com.example.schoolapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.schoolapp.data_access.ISubmitAssignmentDA;
import com.example.schoolapp.data_access.SubmitAssignmentDA;

import java.util.ArrayList;
import java.util.List;

public class SubmitAssignmentActivity extends AppCompatActivity {

    private Spinner spinnerClass, spinnerAssignment;
    private EditText editDetails;
    private Button btnSelectFile, btnSend, btnCancel;
    private TextView textSelectedFile;
    private ISubmitAssignmentDA submitAssignmentDA;

    private List<Uri> selectedFileUris = new ArrayList<>();

    private ActivityResultLauncher<Intent> filePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_assignment);

        submitAssignmentDA = new SubmitAssignmentDA(this);

        // Bind views
        spinnerClass = findViewById(R.id.spinnerClass);
        spinnerAssignment = findViewById(R.id.spinnerAssignment);
        editDetails = findViewById(R.id.editDetails);
        btnSelectFile = findViewById(R.id.btnSelectFile);
        btnSend = findViewById(R.id.btnSend);
        btnCancel = findViewById(R.id.btnCancel);
        textSelectedFile = findViewById(R.id.textSelectedFile);

        // Dummy spinner data (replace with real API later)
        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Select Class", "10-A", "10-B"});
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(classAdapter);

        ArrayAdapter<String> assignmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Select Assignment", "Homework 1", "Project X"});
        assignmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAssignment.setAdapter(assignmentAdapter);

        // File picker
        filePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                selectedFileUris.clear();
                Uri uri = result.getData().getData();
                if (uri != null) {
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    selectedFileUris.add(uri);
                    textSelectedFile.setText("Selected File:\n• " + getFileName(uri));
                    textSelectedFile.setVisibility(View.VISIBLE);
                }
            }
        });

        btnSelectFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            filePickerLauncher.launch(intent);
        });

        btnSend.setOnClickListener(v -> {
            String classSelected = spinnerClass.getSelectedItem().toString();
            String assignmentSelected = spinnerAssignment.getSelectedItem().toString();
            String details = editDetails.getText().toString().trim();

            if (classSelected.equals("Select Class") || assignmentSelected.equals("Select Assignment")) {
                Toast.makeText(this, "Please select class and assignment", Toast.LENGTH_SHORT).show();
                return;
            }

            submitAssignmentDA.submitAssignment(classSelected, assignmentSelected, details, selectedFileUris, new ISubmitAssignmentDA.BaseCallback() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(SubmitAssignmentActivity.this, message, Toast.LENGTH_SHORT).show();
                    clearForm();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(SubmitAssignmentActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });

        });

        btnCancel.setOnClickListener(v -> finish());
    }

    private void clearForm() {
        spinnerClass.setSelection(0);
        spinnerAssignment.setSelection(0);
        editDetails.setText("");
        textSelectedFile.setText("");
        textSelectedFile.setVisibility(View.GONE);
        selectedFileUris.clear();
    }

    private String getFileName(Uri uri) {
        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (idx >= 0) return cursor.getString(idx);
            }
        } catch (Exception ignored) {}
        return uri.getLastPathSegment();
    }
}
