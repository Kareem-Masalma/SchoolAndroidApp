package com.example.schoolapp.data_access;

import android.content.Context;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import com.example.schoolapp.models.Exam;
import com.example.schoolapp.models.StudentExamResult;
import org.json.*;
import java.util.*;

public class ExamDA implements IExamDA {

    private final Context context;
    private final RequestQueue queue;
    private final String BASE_URL = "http://" + DA_Config.BACKEND_IP_ADDRESS + "/" + DA_Config.BACKEND_DIR + "/exam.php";

    public ExamDA(Context context) {
        this.context = context;
        this.queue = Volley.newRequestQueue(context);
    }

    @Override
    public void sendExam(Exam exam, List<StudentExamResult> results, ExamCallback callback) {
        try {
            JSONObject examObject = new JSONObject();
            examObject.put("title", exam.getTitle());
            examObject.put("subject_id", exam.getSubject());
            examObject.put("date", exam.getDate().toString());
            examObject.put("duration", exam.getDuration());
            examObject.put("percentage", exam.getPercentage());

            JSONArray studentsArray = new JSONArray();
            for (StudentExamResult res : results) {
                JSONObject obj = new JSONObject();
                obj.put("student_id", res.getStudentId());
                obj.put("mark", res.getMark());
                studentsArray.put(obj);
            }

            JSONObject finalJson = new JSONObject();
            finalJson.put("exam", examObject);
            finalJson.put("students", studentsArray);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    BASE_URL,
                    finalJson,
                    response -> {
                        List<JSONObject> result = new ArrayList<>();
                        result.add(response);
                        callback.onSuccess(result);
                    },
                    error -> callback.onError("Failed to publish results: " + error.toString())
            );

            queue.add(request);

        } catch (JSONException e) {
            callback.onError("JSON Error: " + e.getMessage());
        }
    }

    @Override
    public void getAllExams(int studentId, ExamCallback callback) {
        String url = BASE_URL + "?mode=list&student_id=" + studentId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<JSONObject> examList = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        examList.add(response.optJSONObject(i));
                    }
                    callback.onSuccess(examList);
                },
                error -> callback.onError("Error: " + error.getMessage())
        );

        queue.add(request);
    }

    @Override
    public void findExamById(int examId, ExamCallback callback) {
        String url = BASE_URL + "?mode=find&exam_id=" + examId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    List<JSONObject> result = new ArrayList<>();
                    result.add(response);
                    callback.onSuccess(result);
                },
                error -> callback.onError("Error: " + error.getMessage())
        );
        queue.add(request);
    }

    @Override
    public void updateExam(Exam exam, ExamCallback callback) {
        try {
            JSONObject examObject = new JSONObject();
            examObject.put("mode", "update");
            examObject.put("exam_id", exam.getExamId());
            examObject.put("title", exam.getTitle());
            examObject.put("subject_id", exam.getSubject());
            examObject.put("date", exam.getDate().toString());
            examObject.put("duration", exam.getDuration());
            examObject.put("percentage", exam.getPercentage());

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    BASE_URL,
                    examObject,
                    response -> {
                        List<JSONObject> result = new ArrayList<>();
                        result.add(response);
                        callback.onSuccess(result);
                    },
                    error -> callback.onError("Update failed: " + error.toString())
            );

            queue.add(request);
        } catch (JSONException e) {
            callback.onError("JSON Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteExam(int examId, ExamCallback callback) {
        String url = BASE_URL + "?mode=delete&exam_id=" + examId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    List<JSONObject> result = new ArrayList<>();
                    result.add(response);
                    callback.onSuccess(result);
                },
                error -> callback.onError("Delete failed: " + error.toString())
        );

        queue.add(request);
    }

    @Override
    public void publishExamResults(Exam exam, List<StudentExamResult> results, PublishCallback callback) {
        try {
            JSONObject examObject = new JSONObject();
            examObject.put("title", exam.getTitle());
            examObject.put("subject_id", exam.getSubject());
            examObject.put("date", exam.getDate().toString());
            examObject.put("duration", exam.getDuration());
            examObject.put("percentage", exam.getPercentage());

            JSONArray studentsArray = new JSONArray();
            for (StudentExamResult res : results) {
                JSONObject obj = new JSONObject();
                obj.put("student_id", res.getStudentId());
                obj.put("mark", res.getMark());
                studentsArray.put(obj);
            }

            JSONObject finalJson = new JSONObject();
            finalJson.put("exam", examObject);
            finalJson.put("students", studentsArray);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    BASE_URL,
                    finalJson,
                    response -> callback.onSuccess("Results published"),
                    error -> callback.onError("Failed to publish results: " + error.toString())
            );

            queue.add(request);

        } catch (JSONException e) {
            callback.onError("JSON Error: " + e.getMessage());
        }
    }
}
