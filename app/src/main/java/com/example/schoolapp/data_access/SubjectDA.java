package com.example.schoolapp.data_access;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolapp.models.Subject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubjectDA {
    private final RequestQueue queue;
    private final String BASE = "http://192.168.1.102/school/subject.php";

    public SubjectDA(Context ctx) {
        queue = Volley.newRequestQueue(ctx);
    }

    public void getAllSubjects(SubjectListCallback cb) {
        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, BASE, null,
                resp -> {
                    try {
                        List<Subject> list = new ArrayList<>();
                        for (int i = 0; i < resp.length(); i++) {
                            list.add(parseSubject(resp.getJSONObject(i)));
                        }
                        cb.onSuccess(list);
                    } catch (JSONException ex) {
                        cb.onError("Malformed list");
                    }
                },
                err -> cb.onError("Fetch failed")
        );
        queue.add(req);
    }

    public void getSubjectById(int id, SingleSubjectCallback cb) {
        String url = BASE + "?subject_id=" + id;
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                resp -> {
                    try {
                        cb.onSuccess(parseSubject(resp));
                    } catch (JSONException ex) {
                        cb.onError("Malformed data");
                    }
                },
                err -> cb.onError("Fetch failed")
        );
        queue.add(req);
    }

    public void addSubject(Subject s, BaseCallback cb) {
        StringRequest req = new StringRequest(Request.Method.POST, BASE,
                resp -> cb.onSuccess("Subject added"),
                err -> cb.onError("Add failed")
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("title", s.getTitle());
                map.put("class_id", String.valueOf(s.getClassId()));
                return map;
            }
        };
        queue.add(req);
    }

    public void updateSubject(Subject s, BaseCallback cb) {
        StringRequest req = new StringRequest(Request.Method.PUT, BASE,
                resp -> cb.onSuccess("Subject updated"),
                err -> cb.onError("Update failed")
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("subject_id", String.valueOf(s.getSubjectId()));
                map.put("title", s.getTitle());
                map.put("class_id", String.valueOf(s.getClassId()));
                return map;
            }
        };
        queue.add(req);
    }

    public void deleteSubject(int id, BaseCallback cb) {
        String url = BASE + "?subject_id=" + id;
        StringRequest req = new StringRequest(Request.Method.DELETE, url,
                resp -> cb.onSuccess("Deleted"),
                err -> cb.onError("Delete failed")
        );
        queue.add(req);
    }

    private Subject parseSubject(JSONObject o) throws JSONException {
        return new Subject(
                o.getInt("subject_id"),
                o.getString("title"),
                o.getInt("class_id"),
                o.optString("class_name", "")
        );
    }

    public interface SubjectListCallback {
        void onSuccess(List<Subject> list);

        void onError(String error);
    }

    public interface SingleSubjectCallback {
        void onSuccess(Subject s);

        void onError(String error);
    }

    public interface BaseCallback {
        void onSuccess(String msg);

        void onError(String error);
    }
}
