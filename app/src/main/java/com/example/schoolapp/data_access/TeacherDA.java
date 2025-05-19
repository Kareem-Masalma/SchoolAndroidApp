package com.example.schoolapp.data_access;

import android.content.Context;

import com.android.volley.*;
import com.android.volley.toolbox.*;
import com.example.schoolapp.models.Role;
import com.example.schoolapp.models.Teacher;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.*;

public class TeacherDA implements ITeacherDA {

    private RequestQueue queue;

    private final String BASE_URL = "http://localhost/phpmyadmin/index.php"; // change "school/" if needed
    private final Gson gson = new Gson();

    public TeacherDA() {
    }

    @Override
    public void findTeacherById(int id, SingleTeacherCallback callback) {

        String url = BASE_URL + "addTeacher.php?id=" + id;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject obj = response.getJSONObject(0);
                    Teacher teacher = new Teacher(
                            obj.getInt("user_id"), obj.getString("first_name"),
                            obj.getString("last_name"), LocalDate.parse("birth_date"),
                            obj.getString("address"), obj.getString("phone"), Role.TEACHER,
                            obj.getString("speciality"), obj.getInt("schedule_id"));
                    callback.onSuccess(teacher);
                } catch (JSONException e) {
                    callback.onError("Teacher Not Found");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError("Teacher Not Found");
            }
        });
        queue.add(request);
    }

    @Override
    public void getAllTeachers(TeacherListCallback callback) {
        String url = BASE_URL + "addTeacher.php";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    List<Teacher> teachers = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(0);
                        Teacher teacher = new Teacher(
                                obj.getInt("user_id"), obj.getString("first_name"),
                                obj.getString("last_name"), LocalDate.parse("birth_date"),
                                obj.getString("address"), obj.getString("phone"), Role.TEACHER,
                                obj.getString("speciality"), obj.getInt("schedule_id"));
                        teachers.add(teacher);
                    }
                    callback.onSuccess(teachers);
                } catch (JSONException e) {
                    callback.onError("No teachers found");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError("No teachers found");
            }
        });
        queue.add(request);


    }

    @Override
    public void addTeacher(Teacher teacher, BaseCallback callback) {
//        String url = BASE_URL + "addTeacher.php";
//        postJson(url, teacher, callback);
    }

    @Override
    public void updateTeacher(Teacher teacher, BaseCallback callback) {
//        String url = BASE_URL + "addTeacher.php";
//        postJson(url, teacher, callback);
    }

    @Override
    public void deleteTeacher(int id, BaseCallback callback) {
//        String url = BASE_URL + "addTeacher.php";
//        Map<String, Integer> body = new HashMap<>();
//        body.put("id", id);
//        postJson(url, body, callback);
    }

    public interface SingleTeacherCallback {
        void onSuccess(Teacher teacher);

        void onError(String error);
    }

    public interface TeacherListCallback {
        void onSuccess(List<Teacher> teachers);

        void onError(String error);
    }

    public interface BaseCallback {
        void onSuccess(String message);

        void onError(String error);
    }
}
