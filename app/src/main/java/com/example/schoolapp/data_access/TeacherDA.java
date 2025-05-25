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

    private final String BASE_URL = "http://192.168.1.102/school/teacher.php";
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
                    Log.d("Teacher", "Teacher: " + obj.toString());
                    Teacher teacher = new Teacher(
                            obj.getInt("user_id"), obj.getString("first_name"),
                            obj.getString("last_name"), LocalDate.parse(obj.getString("birth_date")),
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
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, BASE_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    List<Teacher> teachers = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        Teacher teacher = new Teacher(
                                obj.getInt("user_id"), obj.getString("first_name"),
                                obj.getString("last_name"), LocalDate.parse(obj.getString("birth_date")),
                                obj.getString("address"), obj.getString("phone"), Role.TEACHER,
                                obj.getString("speciality"));
                        Log.d("Teacher", "Teacher test: " + obj.toString());
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
    public void addTeacher(Teacher teacher) {
        StringRequest request = new StringRequest(Request.Method.POST, BASE_URL, response -> Log.d("POST_SUCCESS", response),
                error -> Log.e("POST_ERROR", error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
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
    public void updateTeacher(Teacher teacher) {
        StringRequest request = new StringRequest(Request.Method.PUT, BASE_URL, response -> Log.d("PUT_SUCCESS", response),
                error -> Log.e("PUT_ERROR", error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", teacher.getUser_id().toString());
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
