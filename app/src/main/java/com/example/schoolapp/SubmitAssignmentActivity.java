package com.example.schoolapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.example.schoolapp.data_access.ISubmitAssignmentDA;
import com.example.schoolapp.data_access.SubmitAssignmentDA;
import com.example.schoolapp.json_helpers.LocalDateAdapter;
import com.example.schoolapp.models.Assignment;
import com.example.schoolapp.models.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.util.*;

public class SubmitAssignmentActivity extends AppCompatActivity {
    private TextView textAssignmentTitle, textSelectedFile;
    private EditText editDetails;
    private Button btnSelectFile, btnSend, btnCancel;
    private ISubmitAssignmentDA submitAssignmentDA;
    private List<Uri> selectedFileUris = new ArrayList<>();
    private Assignment assignment;
    private String classTitle; // NEW: get it from intent

    private ActivityResultLauncher<Intent> filePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_assignment);


        submitAssignmentDA = new SubmitAssignmentDA(this);

        // Bind views
        textAssignmentTitle = findViewById(R.id.textAssignmentTitle);
        editDetails = findViewById(R.id.editDetails);
        textSelectedFile = findViewById(R.id.textSelectedFile);
        btnSelectFile = findViewById(R.id.btnSelectFile);
        btnSend = findViewById(R.id.btnSend);
        btnCancel = findViewById(R.id.btnCancel);

        // Get assignment and classTitle separately
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();

        assignment = gson.fromJson(getIntent().getStringExtra("ASSIGNMENT_JSON"), Assignment.class);

        classTitle = getIntent().getStringExtra("CLASS_TITLE"); // pass this from previous screen

        // Show assignment title
        textAssignmentTitle.setText("Submitting: " + assignment.getTitle());

        // File picker setup
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
            String details = editDetails.getText().toString().trim();

            // Validate both fields are not empty
            if (details.isEmpty() && selectedFileUris.isEmpty()) {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("Missing Submission")
                        .setMessage("Please fill in the assignment details or attach a file (or both) before submitting.")
                        .setPositiveButton("OK", null)
                        .show();
                return;
            }

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String userJson = prefs.getString(Login.LOGGED_IN_USER, null);

            if (userJson == null) {
                Toast.makeText(this, "User not found. Please login again.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            Gson gsonUser = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .create();
            User loggedInUser = gsonUser.fromJson(userJson, User.class);
            int studentId = loggedInUser.getUser_id();  // or getId() depending on your model


            // Update DA with method that accepts studentId
            if (submitAssignmentDA instanceof SubmitAssignmentDA) {
                ((SubmitAssignmentDA) submitAssignmentDA).submitAssignment(
                        studentId,
                        assignment.getTitle(),
                        details,
                        selectedFileUris,
                        new ISubmitAssignmentDA.BaseCallback() {
                            @Override
                            public void onSuccess(String message) {
                                Toast.makeText(SubmitAssignmentActivity.this, message, Toast.LENGTH_SHORT).show();
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("submission_success", true);
                                setResult(RESULT_OK, resultIntent);
                                finish();
                            }

                            @Override
                            public void onError(String error) {
                                Toast.makeText(SubmitAssignmentActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                showErrorDialog("Data access error. Please try again.");
            }
        });

        btnCancel.setOnClickListener(v -> {
            if (hasUnsavedInput()) {
                showDiscardChangesDialog();
            } else {
                finish();
            }
        });
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
    @Override
    public void onBackPressed() {
        if (hasUnsavedInput()) {
            new android.app.AlertDialog.Builder(this)
                    .setTitle("Discard Changes?")
                    .setMessage("You have unsaved input. Are you sure you want to go back and lose your data?")
                    .setPositiveButton("Yes, Discard", (dialog, which) -> super.onBackPressed())
                    .setNegativeButton("No", null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            boolean submitted = data != null && data.getBooleanExtra("submission_success", false);
            if (submitted) {
                Toast.makeText(this, "Assignment submitted successfully", Toast.LENGTH_SHORT).show();
                // Optionally refresh list or finish this activity
            }
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

    private boolean hasUnsavedInput() {
        return !editDetails.getText().toString().trim().isEmpty()
                || !selectedFileUris.isEmpty();
    }


    private void showErrorDialog(String message) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Submission Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

}
