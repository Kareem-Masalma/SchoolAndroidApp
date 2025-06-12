package com.example.schoolapp.data_access;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolapp.models.StudentAssignmentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StudentAssignmentResultDA {

    private final RequestQueue queue;

    private final String BASE_URL = "http://" + DA_Config.BACKEND_IP_ADDRESS + "/" + DA_Config.BACKEND_DIR + "/student_assignment_result.php";

    public StudentAssignmentResultDA(Context ctx) {

        queue = Volley.newRequestQueue(ctx);
    }

    /* get assignment results by student_id and subject_id */
    public void getAssignmentResults(int studentId, int subjectId, AssignmentResultCallback cb) {
        String url = BASE_URL + "?student_id=" + studentId + "&subject_id=" + subjectId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                resp -> {
                    try {
                        List<StudentAssignmentResult> list = new ArrayList<>();
                        for (int i = 0; i < resp.length(); i++) {
                            list.add(parseStudentAssignmentMark(resp.getJSONObject(i)));
                        }
                        cb.onSuccess(list);
                    } catch (JSONException e) {
                        cb.onError("Malformed list");
                    }
                },
                error -> cb.onError("Fetch failed")
        );

        queue.add(request);
    }

    private StudentAssignmentResult parseStudentAssignmentMark(JSONObject o) throws JSONException {
        return new StudentAssignmentResult(
                o.getInt("student_id"),
                o.getInt("assignment_id"),
                (float) o.getDouble("score")
        );
    }

    public interface AssignmentResultCallback {
        void onSuccess(List<StudentAssignmentResult> results);
        void onError(String error);
    }
}
