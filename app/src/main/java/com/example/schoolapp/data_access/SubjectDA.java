package com.example.schoolapp.data_access;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.example.schoolapp.models.Role;

import com.example.schoolapp.models.Subject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SubjectDA implements ISubjectDA{

    private final RequestQueue queue;

    private final String BASE = "";

    public SubjectDA(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    @Override
    public void findSubjectById(int subjectID, SingleSubjectCallback callback) {
        String url = BASE + "?subject_id=" + subjectID;

    }

    @Override
    public void getAllSubjects(SubjectListCallback callback) {

    }

    @Override
    public void addSubject(Subject subject) {

    }

    @Override
    public void updateSubject(Subject subject) {

    }

    @Override
    public void deleteSubject(int id) {

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
