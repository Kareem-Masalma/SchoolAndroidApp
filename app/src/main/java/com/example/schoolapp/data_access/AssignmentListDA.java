package com.example.schoolapp.data_access;

import android.content.Context;
import android.util.Log;

import com.android.volley.*;
import com.android.volley.toolbox.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class AssignmentListDA implements IAssignmentListDA {

    private final Context context;
    private final RequestQueue queue;
    private final String BASE_URL = "http://" + DA_Config.BACKEND_IP_ADDRESS + "/" + DA_Config.BACKEND_DIR + "/assignment.php";

    public AssignmentListDA(Context context) {
        this.context = context;
        this.queue = Volley.newRequestQueue(context);
    }

    @Override
    public void getAllAssignments(AssignmentListCallback callback) {
        String url = BASE_URL + "?mode=all";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<JSONObject> result = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            result.add(response.getJSONObject(i));
                        } catch (JSONException e) {
                            callback.onError("Invalid JSON at index " + i);
                            return;
                        }
                    }
                    callback.onSuccess(result);
                },
                error -> {
                    String msg = "Failed to fetch assignments";
                    Log.e("AssignmentListDA", msg, error);
                    callback.onError(msg);
                }
        );

        queue.add(request);
    }
}
