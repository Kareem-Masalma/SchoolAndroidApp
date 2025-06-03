package com.example.schoolapp.data_access;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Base64;

import com.android.volley.*;
import com.android.volley.toolbox.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

public class SubmitAssignmentDA implements ISubmitAssignmentDA {

    private final Context context;
    private final RequestQueue queue;
    private final String BASE_URL = "http://" + DA_Config.BACKEND_IP_ADDRESS + "/" + DA_Config.BACKEND_DIR + "/assignment_submission.php";

    public SubmitAssignmentDA(Context context) {
        this.context = context;
        this.queue = Volley.newRequestQueue(context);
    }

    @Override
    public void submitAssignment(String className, String assignmentTitle, String details, List<Uri> fileUris, ISubmitAssignmentDA.BaseCallback callback) {
        JSONObject data = new JSONObject();
        try {
            data.put("mode", "submit");
            data.put("class", className);
            data.put("assignment", assignmentTitle);
            data.put("details", details);

            if (!fileUris.isEmpty()) {
                Uri fileUri = fileUris.get(0);
                byte[] bytes = readBytes(fileUri);
                if (bytes != null) {
                    String encoded = Base64.encodeToString(bytes, Base64.NO_WRAP);
                    data.put("file_data", encoded);
                    data.put("file_name", getFileName(fileUri));
                }
            }
        } catch (Exception e) {
            callback.onError("Failed to build request");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, BASE_URL, data,
                response -> callback.onSuccess("Assignment submitted successfully"),
                error -> callback.onError("Submission failed")) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        queue.add(request);
    }

    @Override
    public void findSubmissionById(int id, SingleSubmissionCallback callback) {
        String url = BASE_URL + "?mode=find&id=" + id;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                callback::onSuccess,
                error -> callback.onError("Failed to fetch submission")) {
            @Override
            public Map<String, String> getHeaders() {
                return Collections.singletonMap("Content-Type", "application/json");
            }
        };
        queue.add(request);
    }

    @Override
    public void getAllSubmissions(SubmissionListCallback callback) {
        String url = BASE_URL + "?mode=all";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                callback::onSuccess,
                error -> callback.onError("Failed to load submissions"));
        queue.add(request);
    }

    @Override
    public void updateSubmission(int id, String className, String assignmentTitle, String details, BaseCallback callback) {
        JSONObject data = new JSONObject();
        try {
            data.put("mode", "update");
            data.put("id", id);
            data.put("class", className);
            data.put("assignment", assignmentTitle);
            data.put("details", details);
        } catch (JSONException e) {
            callback.onError("Invalid data");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, BASE_URL, data,
                response -> callback.onSuccess("Updated successfully"),
                error -> callback.onError("Update failed")) {
            @Override
            public Map<String, String> getHeaders() {
                return Collections.singletonMap("Content-Type", "application/json");
            }
        };
        queue.add(request);
    }

    @Override
    public void deleteSubmission(int id, BaseCallback callback) {
        String url = BASE_URL + "?mode=delete&id=" + id;
        StringRequest request = new StringRequest(Request.Method.DELETE, url,
                response -> callback.onSuccess("Deleted successfully"),
                error -> callback.onError("Delete failed"));
        queue.add(request);
    }

    private byte[] readBytes(Uri uri) throws IOException {
        try (InputStream in = context.getContentResolver().openInputStream(uri);
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            byte[] data = new byte[1024];
            int nRead;
            while ((nRead = in.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            return buffer.toByteArray();
        }
    }

    private String getFileName(Uri uri) {
        String result = "file";
        try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (idx >= 0) result = cursor.getString(idx);
            }
        }
        return result;
    }

    // === CALLBACK INTERFACES ===
    public interface BaseCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    public interface SingleSubmissionCallback {
        void onSuccess(JSONObject submission);
        void onError(String error);
    }

    public interface SubmissionListCallback {
        void onSuccess(JSONArray list);
        void onError(String error);
    }
}
