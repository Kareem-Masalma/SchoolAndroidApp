package com.example.schoolapp.data_access;

import android.content.Context;
import android.net.Uri;

import org.json.JSONObject;

import java.util.List;

public interface IAssignmentDA {

    // Create (Send)
    void sendAssignment(String title, String details, Integer subjectId, String deadline, float percentage,
                        List<Uri> files, IAssignmentDA.BaseCallback callback);

    // Read
    void getAllAssignments(AssignmentListCallback callback);
    void findAssignmentById(int id, SingleAssignmentCallback callback);

    // Update
    void updateAssignment(int id, String title, String details, String subjectName, String deadline, float percentage,
                          BaseCallback callback);

    // Delete
    void deleteAssignment(int id, BaseCallback callback);

    // Spinner class-subject list
    //void getSubjectPairs(Context context, SubjectCallback callback);


    // ===== CALLBACK INTERFACES =====
    interface BaseCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    interface AssignmentListCallback {
        void onSuccess(List<JSONObject> assignments);
        void onError(String error);
    }

    interface SingleAssignmentCallback {
        void onSuccess(JSONObject assignment);
        void onError(String error);
    }

    interface ClassSubjectCallback {
        void onSuccess(List<JSONObject> pairs, List<String> labels);
        void onError(String errorMessage);
    }
}
