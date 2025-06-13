package com.example.schoolapp.data_access;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.android.volley.*;
import com.android.volley.toolbox.*;
import com.example.schoolapp.Login;
import com.example.schoolapp.json_helpers.LocalDateAdapter;
import com.example.schoolapp.models.Assignment;
import com.example.schoolapp.models.Teacher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.*;

import java.io.*;
import java.time.LocalDate;
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

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String userJson = prefs.getString(Login.LOGGED_IN_USER, null);
            if (userJson != null) {
                Gson gson = new GsonBuilder().registerTypeAdapter(java.time.LocalDate.class, new LocalDateAdapter()).create();
                Teacher teacher = gson.fromJson(userJson, Teacher.class);
                json.put("teacher_id", teacher.getUser_id());
            }

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
    public void getAllAssignments(int studentId, AssignmentListCallback callback) {

    }

    public void getAllAssignmentsWithTitles(int studentId, AssignmentListWithTitlesCallback callback) {
        String url = BASE_URL + "?mode=all&student_id=" + studentId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<Assignment> result = new ArrayList<>();
                    Map<Assignment, String> subjectMap = new HashMap<>();
                    Map<Assignment, String> classMap = new HashMap<>();

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            Assignment assignment = new Assignment(
                                    obj.getInt("assignment_id"),
                                    obj.getInt("subject_id"),
                                    obj.getString("title"),
                                    obj.optString("details", ""),
                                    LocalDate.parse(obj.getString("start_date")),
                                    obj.optString("file_path", ""),
                                    LocalDate.parse(obj.getString("end_date")),
                                    (float) obj.getDouble("percentage_of_grade")
                            );

                            result.add(assignment);
                            subjectMap.put(assignment, obj.optString("subject_title", "Unknown"));
                            classMap.put(assignment, obj.optString("class_title", "Unknown"));
                        } catch (JSONException e) {
                            callback.onError("Invalid JSON at index " + i);
                            return;
                        }
                    }

                    callback.onSuccess(result, subjectMap, classMap);
                },
                error -> {
                    String msg = "Failed to fetch assignments";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        msg += ": " + new String(error.networkResponse.data);
                    }
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

    public void getAssignmentsBySubject(int subjectId, AssignmentListTitleCallback callback) {
        String url = BASE_URL + "?mode=list_by_subject&subject_id=" + subjectId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<Assignment> list = new ArrayList<>();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            Assignment assignment = new Assignment(
                                    obj.getInt("assignment_id"),
                                    obj.getInt("subject_id"),
                                    obj.getString("title"),
                                    obj.optString("details", ""),
                                    LocalDate.parse(obj.getString("start_date")),
                                    obj.optString("file_path", null),
                                    LocalDate.parse(obj.getString("end_date")),
                                    (float) obj.getDouble("percentage_of_grade")
                            );
                            list.add(assignment);
                        }
                        callback.onSuccess(list);
                    } catch (JSONException e) {
                        callback.onError("Malformed data");
                    }
                },
                error -> callback.onError("Fetch failed")
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
    public interface AssignmentListTitleCallback {
        void onSuccess(List<Assignment> assignments);
        void onError(String error);
    }

    public interface AssignmentListWithTitlesCallback {
        void onSuccess(List<Assignment> assignments, Map<Assignment, String> subjectTitles, Map<Assignment, String> classTitles);
        void onError(String error);
    }



}
