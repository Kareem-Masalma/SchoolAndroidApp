package com.example.schoolapp.data_access;

import android.content.Context;

import com.android.volley.*;
import com.android.volley.toolbox.*;
import org.json.*;
import java.util.*;

public class AssignmentDetailsDA implements IAssignmentDetailsDA {
    private final Context context;
    private final RequestQueue queue;
    private final String BASE_URL = "http://" + DA_Config.BACKEND_IP_ADDRESS + "/" + DA_Config.BACKEND_DIR + "/assignment.php";

    public AssignmentDetailsDA(Context context) {
        this.context = context;
        this.queue = Volley.newRequestQueue(context);
    }

    @Override
    public void getAssignmentById(int assignmentId, SingleAssignmentCallback callback) {
        String url = BASE_URL + "?mode=find&id=" + assignmentId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                callback::onSuccess,
                error -> {
                    String errorMsg = "Failed to fetch assignment details";
                    if (error.networkResponse != null) {
                        errorMsg += ": " + error.networkResponse.statusCode;
                    }
                    callback.onError(errorMsg);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        queue.add(request);
    }

    @Override
    public void getAllAssignments(AssignmentListCallback callback) {
        String url = BASE_URL + "?mode=all";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<JSONObject> assignments = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            assignments.add(response.getJSONObject(i));
                        } catch (JSONException e) {
                            callback.onError("Invalid data at index " + i);
                            return;
                        }
                    }
                    callback.onSuccess(assignments);
                },
                error -> callback.onError("Failed to fetch assignments")
        );

        queue.add(request);
    }

    @Override
    public void deleteAssignment(int id, BaseCallback callback) {
        JSONObject json = new JSONObject();
        try {
            json.put("mode", "delete");
            json.put("id", id);
        } catch (JSONException e) {
            callback.onError("Failed to build delete request");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, BASE_URL, json,
                response -> callback.onSuccess("Assignment deleted successfully"),
                error -> callback.onError("Failed to delete assignment")) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        queue.add(request);
    }
}
