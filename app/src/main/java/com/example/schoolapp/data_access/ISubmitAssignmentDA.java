package com.example.schoolapp.data_access;

import android.net.Uri;

import java.util.List;

public interface ISubmitAssignmentDA {
    void submitAssignment(int studentId, String assignmentTitle, String details, List<Uri> files, BaseCallback callback);
    void findSubmissionById(int id, SingleSubmissionCallback callback);
    void getAllSubmissions(SubmitAssignmentDA.SubmissionListCallback callback);
    void updateSubmission(int id, String className, String assignmentTitle, String details, SubmitAssignmentDA.BaseCallback callback);
    void deleteSubmission(int id, SubmitAssignmentDA.BaseCallback callback);
    interface BaseCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    interface SingleSubmissionCallback {
        void onSuccess(String submission);
        void onError(String error);
    }

    interface SubmissionListCallback {
        void onSuccess(List<org.json.JSONObject> submissions);
        void onError(String error);
    }
}
