package com.example.schoolapp.data_access;

import android.net.Uri;
import org.json.JSONObject;
import java.util.List;

public interface IAssignmentDetailsDA {

    interface BaseCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    interface SingleAssignmentCallback {
        void onSuccess(JSONObject assignment);
        void onError(String error);
    }

    interface AssignmentListCallback {
        void onSuccess(List<JSONObject> assignments);
        void onError(String error);
    }

    void getAssignmentById(int id, SingleAssignmentCallback callback);

    void getAllAssignments(AssignmentListCallback callback);


    void deleteAssignment(int id, BaseCallback callback);
}
