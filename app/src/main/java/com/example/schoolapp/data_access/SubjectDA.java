package com.example.schoolapp.data_access;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import com.example.schoolapp.models.Subject;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SubjectDA implements ISubjectDA{

    private final RequestQueue queue;

    private final String BASE = "http://localhost/backend/subject.php";

    public SubjectDA(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    @Override
    public void findSubjectById(int subjectID, SingleSubjectCallback callback) {
        String url = BASE + "?subject_id=" + subjectID;
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET, url, null,
                resp -> {
                    try {
                        Subject subject = new Subject(
                        resp.getInt("subject_id"),
                        resp.getInt("class_id"),
                        resp.getString("title"));

                        callback.onSuccess(subject);

                    } catch (JSONException e) {
                        callback.onError("failed to read subject data");
                    }
                },
                err -> callback.onError("couldn't load subject")
        );
        queue.add(req);
    }

    @Override
    public void getAllSubjects(SubjectListCallback callback) {
        JsonArrayRequest req = new JsonArrayRequest(
                Request.Method.GET, BASE, null,
                resp -> {

                    try{
                        List<Subject> subList = new ArrayList<>();
                        for(int i=0; i<resp.length(); i++){
                            JSONObject subObj = resp.getJSONObject(i);

                            Subject subject = new Subject(
                                subObj.getInt("subject_id"),
                                subObj.getInt("class_id"),
                                subObj.getString("title")
                            );
                            subList.add(subject);
                        }
                        callback.onSuccess(subList);

                    } catch (JSONException e) {
                        callback.onError("failed to read subject data");
                    }
                },
                err -> callback.onError("couldn't load subjects")
        );
        queue.add(req);
    }

    @Override
    public void addSubject(Subject subject, BaseCallback callback) {
        try {
            JSONObject bdy = new JSONObject();
            bdy.put("class_id", subject.getClass_id());
            bdy.put("title" , subject.getTitle());
            JsonObjectRequest req = new JsonObjectRequest(
                    Request.Method.POST, BASE, bdy,
                    resp -> callback.onSuccess("Subject added successfully"),
                    err -> callback.onError("Add subject failed")
            );
            queue.add(req);

        } catch (JSONException e) {
            callback.onError("Invalid response");
        }
    }

    @Override
    public void updateSubject(Subject subject, BaseCallback callback) {
        try {
            JSONObject bdy = new JSONObject();
            bdy.put("subject_id", subject.getSubject_id());
            bdy.put("class_id", subject.getClass_id());
            bdy.put("title" , subject.getTitle());
            JsonObjectRequest req = new JsonObjectRequest(
                    Request.Method.PUT, BASE, bdy,
                    resp -> callback.onSuccess("Subject update successfully"),
                    err -> callback.onError("update subject failed")
            );
            queue.add(req);

        } catch (JSONException e) {
            callback.onError("Invalid response");
        }
    }

    @Override
    public void deleteSubject(int subjectID, BaseCallback callback) {
        String url = BASE + "?user_id=" + subjectID;
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.DELETE, url, null,
                resp -> callback.onSuccess("Subject delete successfully"),
                err -> callback.onError("delete subject failed")
        );
        queue.add(req);
    }


    public interface SingleSubjectCallback {
        void onSuccess(Subject subject);

        void onError(String error);
    }

    public interface SubjectListCallback {
        void onSuccess(List<Subject> subject);

        void onError(String error);
    }

    public interface BaseCallback {
        void onSuccess(String message);

        void onError(String error);
    }
}
