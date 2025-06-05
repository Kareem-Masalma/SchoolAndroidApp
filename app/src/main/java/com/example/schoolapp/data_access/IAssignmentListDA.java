package com.example.schoolapp.data_access;

import org.json.JSONObject;
import java.util.List;

public interface IAssignmentListDA {
    interface AssignmentListCallback {
        void onSuccess(List<JSONObject> assignments);
        void onError(String error);
    }

    void getAllAssignments(AssignmentListCallback callback);
}
