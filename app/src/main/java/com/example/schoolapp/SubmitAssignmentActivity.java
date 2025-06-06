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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.schoolapp.data_access.ISubmitAssignmentDA;
import com.example.schoolapp.data_access.SubmitAssignmentDA;
import com.example.schoolapp.json_helpers.LocalDateAdapter;
import com.example.schoolapp.models.Assignment;
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

            SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
            int studentId = prefs.getInt("user_id", -1);

            if (studentId == -1) {
                showErrorDialog("Student ID not found. Please log in again.");
                return;
            }

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


        btnCancel.setOnClickListener(v -> finish());
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
    private void showErrorDialog(String message) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Submission Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

}
