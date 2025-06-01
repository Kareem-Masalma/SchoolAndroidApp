package com.example.schoolapp.data_access;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;

import com.android.volley.*;
import com.android.volley.toolbox.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

public class AssignmentDA implements IAssignmentDA {

    private final RequestQueue queue;
    private final Context context;

    private final String BASE_URL = "http://" + DA_Config.BACKEND_IP_ADDRESS + "/" + DA_Config.BACKEND_DIR + "/assignment.php";

    public AssignmentDA(Context context) {
        this.context = context;
        this.queue = Volley.newRequestQueue(context);
    }

    @Override
    public void sendAssignment(String title, String details, String className, String deadline, float percentage, List<Uri> files, BaseCallback callback) {
        JSONObject json = new JSONObject();
        try {
            json.put("title", title);
            json.put("details", details);
            json.put("class", className);
            json.put("deadline", deadline);
            json.put("percentage", percentage);


            // Encode file as Base64 string (only first file supported)
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
            e.printStackTrace();
            callback.onError("Failed to create JSON");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, BASE_URL, json,
                response -> callback.onSuccess("Assignment sent successfully"),
                error -> {
                    Log.e("ASSIGNMENT_ERR", error.toString());
                    callback.onError("Failed to send assignment");
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

    private byte[] readBytes(Uri uri) {
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

            int nRead;
            byte[] data = new byte[1024];

            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            return buffer.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getFileName(Uri uri) {
        String result = "uploaded_file";
        try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex >= 0) {
                    result = cursor.getString(nameIndex);
                }
            }
        } catch (Exception ignored) {}
        return result;
    }
    public void getClassSubjectPairs(Context context, ClassSubjectCallback callback) {
        String url = "http://" + DA_Config.BACKEND_IP_ADDRESS + "/" + DA_Config.BACKEND_DIR + "/assignment.php?mode=class_subject";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<JSONObject> pairs = new ArrayList<>();
                    List<String> labels = new ArrayList<>();
                    labels.add("Select Class - Subject");

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            pairs.add(obj);
                            labels.add(obj.getString("label"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    callback.onSuccess(pairs, labels);
                },
                error -> callback.onError("Failed to load class-subject list")
        );

        Volley.newRequestQueue(context).add(request);
    }

    public interface ClassSubjectCallback {
        void onSuccess(List<JSONObject> pairs, List<String> labels);
        void onError(String errorMessage);
    }


    public interface BaseCallback {
        void onSuccess(String message);
        void onError(String error);
    }
}
