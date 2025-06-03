package com.example.schoolapp.data_access;

import android.net.Uri;

import java.util.List;

public interface ISubmitAssignmentDA {
    void submitAssignment(String className, String assignmentTitle, String details, List<Uri> fileUris, BaseCallback callback);
    void findSubmissionById(int id, SubmitAssignmentDA.SingleSubmissionCallback callback);
    void getAllSubmissions(SubmitAssignmentDA.SubmissionListCallback callback);
    void updateSubmission(int id, String className, String assignmentTitle, String details, SubmitAssignmentDA.BaseCallback callback);
    void deleteSubmission(int id, SubmitAssignmentDA.BaseCallback callback);
    interface BaseCallback {
        void onSuccess(String message);
        void onError(String error);
    }
}
