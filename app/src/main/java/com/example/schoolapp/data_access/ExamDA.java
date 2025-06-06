package com.example.schoolapp.data_access;

import android.content.Context;
import android.util.Log;

import com.android.volley.*;
import com.android.volley.toolbox.*;
import com.example.schoolapp.models.Exam;
import com.example.schoolapp.models.StudentExamResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExamDA {

    private final Context context;
    private final RequestQueue queue;
    private final String BASE_URL = "http://" + DA_Config.BACKEND_IP_ADDRESS + "/" + DA_Config.BACKEND_DIR + "/exam.php";

    public ExamDA(Context context) {
        this.context = context;
        this.queue = Volley.newRequestQueue(context);
    }

    public interface ExamCallback {
        void onSuccess(String message);

        void onError(String error);
    }

    public void publishExamResults(Exam exam, List<StudentExamResult> results, ExamCallback callback) {
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
