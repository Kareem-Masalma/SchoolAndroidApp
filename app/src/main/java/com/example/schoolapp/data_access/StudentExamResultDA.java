package com.example.schoolapp.data_access;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolapp.models.StudentExamResult;
import com.example.schoolapp.models.Subject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StudentExamResultDA {

    private final RequestQueue queue;

    private final String BASE_URL = "http://" + DA_Config.BACKEND_IP_ADDRESS + "/" + DA_Config.BACKEND_DIR + "/student_exam_result.php";

    public StudentExamResultDA(Context ctx) {

        queue = Volley.newRequestQueue(ctx);
    }

    /* get exam results by Student_id and subject_id */
    public void getExamResults(int studentId, int subjectId, ExamResultCallback cb) {
        String url = BASE_URL + "?student_id=" + studentId + "&subject_id=" + subjectId;

        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, url, null,
                resp-> {
                    try {
                        List<StudentExamResult> list = new ArrayList<>();
                        for (int i = 0; i < resp.length(); i++) {
                            list.add(parseStudentExamMark(resp.getJSONObject(i)));
                        }
                        cb.onSuccess(list);
                    } catch (JSONException e) {
                        cb.onError("Malformed list");
                    }
                },
                error -> cb.onError("Fetch failed")
        );

        queue.add(req);
    }

    private StudentExamResult parseStudentExamMark(JSONObject o) throws JSONException {
        return new StudentExamResult(
                o.getInt("student_id"),
                o.getInt("exam_id"),
                (float) o.getDouble("score")
        );
    }

    public interface ExamResultCallback {
        void onSuccess(List<StudentExamResult> results);
        void onError(String error);
    }


}
