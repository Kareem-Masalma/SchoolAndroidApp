package com.example.schoolapp.data_access;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.example.schoolapp.models.Role;
import com.example.schoolapp.models.Student;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SubjectDA implements ISubjectDA{

    private final RequestQueue queue;

    private final String BASE_URL = "";

    public SubjectDA(Context context) {
        queue = Volley.newRequestQueue(context);
    }





}
