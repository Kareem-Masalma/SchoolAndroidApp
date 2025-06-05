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

public class AssignmentDA implements IAssignmentDA {

    private final Context context;
    private final RequestQueue queue;
    private final String BASE_URL = "http://" + DA_Config.BACKEND_IP_ADDRESS + "/" + DA_Config.BACKEND_DIR + "/assignment.php";

    public AssignmentDA(Context context) {
        this.context = context;
        this.queue = Volley.newRequestQueue(context);
    }

    @Override
    public void sendAssignment(String title, String details, Integer subjectId, String deadline, float percentage, List<Uri> files, BaseCallback callback) {
        JSONObject json = new JSONObject();
        try {
            json.put("mode", "add");
            json.put("title", title);
            json.put("details", details);
            json.put("subject", subjectId);
            json.put("deadline", deadline);
            json.put("percentage", percentage);

            // Add current date as start_date
            String startDate = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new java.util.Date());
            json.put("start_date", startDate);

            if (!files.isEmpty()) {
                Uri fileUri = files.get(0);
                byte[] fileBytes = readBytes(fileUri);
                if (fileBytes != null) {
                    String encoded = Base64.encodeToString(fileBytes, Base64.NO_WRAP);
                    String fileName = getFileName(fileUri);
                    json.put("file_data", encoded);
                    json.put("file_name", fileName);

                    // Add file_path as expected by backend (optional but helpful)
                    String filePath = "uploads/" + fileName;
                    json.put("file_path", filePath);
                }
            }

        } catch (JSONException e) {
            callback.onError("Failed to create JSON");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, BASE_URL, json,
                response -> callback.onSuccess("Assignment sent successfully"),
                error -> {
                    String errorMsg = "Unknown error";
                    if (error.networkResponse != null) {
                        errorMsg = "Error code: " + error.networkResponse.statusCode;
                        try {
                            errorMsg += ", " + new String(error.networkResponse.data);
                        } catch (Exception ignored) {}
                    }
                    callback.onError("Failed to send assignment: " + errorMsg);
                    Log.e("AssignmentDA", errorMsg, error);
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
                    Log.e("AssignmentDA", msg, error);
                    callback.onError(msg);
                }
        );
        queue.add(request);
    }

    @Override
    public void findAssignmentById(int id, SingleAssignmentCallback callback) {
        String url = BASE_URL + "?mode=find&id=" + id;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                callback::onSuccess,
                error -> callback.onError("Failed to fetch assignment by ID")
        );

        queue.add(request);
    }

    @Override
    public void updateAssignment(int id, String title, String details, String subjectName, String deadline, float percentage, BaseCallback callback) {
        JSONObject json = new JSONObject();
        try {
            json.put("mode", "update");
            json.put("id", id);
            json.put("title", title);
            json.put("details", details);
            json.put("subject", subjectName);
            json.put("deadline", deadline);
            json.put("percentage", percentage);

        } catch (JSONException e) {
            callback.onError("Failed to create update JSON");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, BASE_URL, json,
                response -> callback.onSuccess("Assignment updated"),
                error -> callback.onError("Update failed")
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
            callback.onError("Failed to create delete request");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, BASE_URL, json,
                response -> callback.onSuccess("Assignment deleted"),
                error -> callback.onError("Deletion failed")
        );

        queue.add(request);
    }

//    @Override
//    public void getSubjectPairs(Context context, SubjectCallback callback) {
//        String url = "http://" + DA_Config.BACKEND_IP_ADDRESS + "/" + DA_Config.BACKEND_DIR + "/assignment.php?mode=subject";
//
//        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
//                response -> {
//                    List<JSONObject> pairs = new ArrayList<>();
//                    List<String> labels = new ArrayList<>();
//                    labels.add("Select Subject");
//
//                    for (int i = 0; i < response.length(); i++) {
//                        try {
//                            JSONObject obj = response.getJSONObject(i);
//                            pairs.add(obj);
//                            labels.add(obj.getString("label"));
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    callback.onSuccess(pairs, labels);
//                },
//                error -> callback.onError("Failed to load subject list")
//        );
//
//        Volley.newRequestQueue(context).add(request);
//    }

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
