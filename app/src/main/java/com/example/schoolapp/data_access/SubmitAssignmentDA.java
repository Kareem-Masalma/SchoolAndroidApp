package com.example.schoolapp.data_access;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;

import com.android.volley.*;
import com.android.volley.toolbox.*;

import org.json.*;

import java.io.*;
import java.util.*;

public class SubmitAssignmentDA implements ISubmitAssignmentDA {

    private final Context context;
    private final RequestQueue queue;
    private final String BASE_URL = "http://" + DA_Config.BACKEND_IP_ADDRESS + "/" + DA_Config.BACKEND_DIR + "/student_assignment_result.php";

    public SubmitAssignmentDA(Context context) {
        this.context = context;
        this.queue = Volley.newRequestQueue(context);
    }

    @Override
    public void submitAssignment(String classTitle, String title, String details, List<Uri> files, BaseCallback callback) {
        JSONObject json = new JSONObject();
        try {
            json.put("mode", "submit");
            json.put("class_title", classTitle);
            json.put("title", title);
            json.put("details", details);

            if (!files.isEmpty()) {
                Uri fileUri = files.get(0);
                byte[] fileBytes = readBytes(fileUri);
                if (fileBytes != null) {
                    String encoded = Base64.encodeToString(fileBytes, Base64.NO_WRAP);
                    json.put("file_data", encoded);
                    json.put("file_name", getFileName(fileUri));
                }
            }

        } catch (JSONException e) {
            callback.onError("Failed to create JSON");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, BASE_URL, json,
                response -> callback.onSuccess("Assignment submitted successfully"),
                error -> {
                    String errorMsg = "Unknown error";
                    if (error.networkResponse != null) {
                        errorMsg = "Error code: " + error.networkResponse.statusCode;
                        try {
                            errorMsg += ", " + new String(error.networkResponse.data);
                        } catch (Exception ignored) {}
                    }
                    callback.onError("Failed to submit assignment: " + errorMsg);
                    Log.e("SubmitAssignmentDA", errorMsg, error);
                }) {
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
    public void findSubmissionById(int id, SingleSubmissionCallback callback) {
        String url = BASE_URL + "?mode=find&id=" + id;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                callback::onSuccess,
                error -> {
                    String errorMsg = "Failed to fetch submission by ID";
                    if (error.networkResponse != null) {
                        errorMsg += ": " + error.networkResponse.statusCode;
                    }
                    callback.onError(errorMsg);
                });

        queue.add(request);
    }

    @Override
    public void getAllSubmissions(SubmissionListCallback callback) {
        String url = BASE_URL + "?mode=all";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<JSONObject> submissions = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            submissions.add(response.getJSONObject(i));
                        } catch (JSONException e) {
                            callback.onError("Invalid JSON at index " + i);
                            return;
                        }
                    }
                    callback.onSuccess(submissions);
                },
                error -> {
                    String errorMsg = "Failed to fetch submissions";
                    if (error.networkResponse != null) {
                        errorMsg += ": " + error.networkResponse.statusCode;
                    }
                    callback.onError(errorMsg);
                });

        queue.add(request);
    }

    @Override
    public void updateSubmission(int id, String className, String assignmentTitle, String details, BaseCallback callback) {
        JSONObject json = new JSONObject();
        try {
            json.put("mode", "update");
            json.put("id", id);
            json.put("class_title", className);
            json.put("assignment_title", assignmentTitle);
            json.put("details", details);
        } catch (JSONException e) {
            callback.onError("Failed to create JSON for update");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, BASE_URL, json,
                response -> callback.onSuccess("Submission updated"),
                error -> {
                    String errorMsg = "Update failed";
                    if (error.networkResponse != null) {
                        errorMsg += ": " + error.networkResponse.statusCode;
                    }
                    callback.onError(errorMsg);
                });

        queue.add(request);
    }

    @Override
    public void deleteSubmission(int id, BaseCallback callback) {
        JSONObject json = new JSONObject();
        try {
            json.put("mode", "delete");
            json.put("id", id);
        } catch (JSONException e) {
            callback.onError("Failed to create JSON for delete");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, BASE_URL, json,
                response -> callback.onSuccess("Submission deleted"),
                error -> {
                    String errorMsg = "Deletion failed";
                    if (error.networkResponse != null) {
                        errorMsg += ": " + error.networkResponse.statusCode;
                    }
                    callback.onError(errorMsg);
                });

        queue.add(request);
    }


    private byte[] readBytes(Uri uri) {
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

            byte[] data = new byte[1024];
            int nRead;
            while ((nRead = inputStream.read(data)) != -1) {
                buffer.write(data, 0, nRead);
            }

            return buffer.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getFileName(Uri uri) {
        String result = "file";
        try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex >= 0) result = cursor.getString(nameIndex);
            }
        } catch (Exception ignored) {}
        return result;
    }
}
