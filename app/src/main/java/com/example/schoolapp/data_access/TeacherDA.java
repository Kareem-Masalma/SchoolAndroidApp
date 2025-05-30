package com.example.schoolapp.data_access;

import android.content.Context;
import android.util.Log;

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

    private final RequestQueue queue;

    private final String BASE_URL = "http://192.168.0.104/School/teacher.php";
    private final Gson gson = new Gson();

    public TeacherDA(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    @Override
    public void findTeacherById(int id, SingleTeacherCallback callback) {
        String url = BASE_URL + "?id=" + id;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject obj = response.getJSONObject(0);
                    Teacher teacher = new Teacher(
                            obj.getInt("user_id"), obj.getString("first_name"),
                            obj.getString("last_name"), LocalDate.parse(obj.getString("birth_date")),
                            obj.getString("address"), obj.getString("phone"), Role.TEACHER,
                            obj.getString("speciality"), obj.getInt("schedule_id"));
                    teacher.setPassword(obj.getString("password"));
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
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, BASE_URL, null, new Response.Listener<JSONArray>() {
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
                        teacher.setPassword(obj.getString("password"));
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
    public void addTeacher(Teacher teacher, BaseCallback baseCallback) {
        JSONObject json = new JSONObject();
        try {
            json.put("first_name", teacher.getFirstName());
            json.put("last_name", teacher.getLastName());
            json.put("birth_date", teacher.getBirthDate().toString());
            json.put("address", teacher.getAddress());
            json.put("phone", teacher.getPhone());
            json.put("role", teacher.getRole().toString());
            json.put("speciality", teacher.getSpeciality());
            json.put("password", teacher.getPassword());
        } catch (JSONException e) {
            e.printStackTrace();
            baseCallback.onError("Failed to create JSON payload");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, BASE_URL, json,
                response -> {
                    Log.d("POST_SUCCESS", response.toString());
                    baseCallback.onSuccess("Teacher added successfully");
                },
                error -> {
                    String body = "";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            body = new String(error.networkResponse.data, "UTF-8");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Log.e("POST_ERROR", error.toString());
                    Log.e("POST_ERROR_BODY", body);
                    baseCallback.onError("Failed to add teacher");
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
    public void updateTeacher(Teacher teacher) {
        StringRequest request = new StringRequest(Request.Method.PUT, BASE_URL, response -> Log.d("PUT_SUCCESS", response),
                error -> Log.e("PUT_ERROR", error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", teacher.getTeacher_id().toString());
                params.put("first_name", teacher.getFirstName());
                params.put("last_name", teacher.getLastName());
                params.put("birth_date", teacher.getBirthDate().toString());
                params.put("address", teacher.getAddress());
                params.put("phone", teacher.getPhone());
                params.put("role", teacher.getRole().toString());
                params.put("speciality", teacher.getSpeciality());
                params.put("password" , teacher.getPassword());

                return params;
            }
        };
        queue.add(request);
    }

    @Override
    public void deleteTeacher(int id) {
        StringRequest request = new StringRequest(Request.Method.DELETE, BASE_URL, response -> Log.d("PUT_SUCCESS", response),
                error -> Log.e("PUT_ERROR", error.toString())) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", Integer.toString(id));
                return params;
            }
        };
        queue.add(request);
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
