package com.example.schoolapp.data_access;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.*;
import com.android.volley.toolbox.*;
import com.example.schoolapp.AddTeacherActivity;
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
    private final Context context;

    private final String BASE_URL = "http://" + DA_Config.BACKEND_IP_ADDRESS + "/" + DA_Config.BACKEND_DIR + "/teacher.php";

    private final Gson gson = new Gson();

    public TeacherDA(Context context) {
        queue = Volley.newRequestQueue(context);
        this.context = context;
    }

    @Override
    public void findTeacherById(int id, SingleTeacherCallback callback) {
        String url = BASE_URL + "?user_id=" + id;
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject obj) {
                        try {
                            if (obj == null || !obj.has("user_id")) {
                                callback.onError("Teacher Not Found");
                                return;
                            }

                            int schedule_id = obj.isNull("schedule_id") ? 0 : obj.getInt("schedule_id");

                            int userId = obj.getInt("user_id");
                            String firstName = obj.getString("first_name");
                            String lastName = obj.getString("last_name");
                            LocalDate birthDate = LocalDate.parse(obj.getString("birth_date"));
                            String address = obj.getString("address");
                            String phone = obj.getString("phone");
                            String speciality = obj.getString("speciality");

                            Teacher teacher = new Teacher(
                                    userId,
                                    firstName,
                                    lastName,
                                    birthDate,
                                    address,
                                    phone,
                                    Role.TEACHER,
                                    speciality,
                                    schedule_id
                            );

                            callback.onSuccess(teacher);
                        } catch (JSONException e) {
                            callback.onError("Teacher Not Found");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError("Teacher Not Found");
                    }
                }
        );

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
                        int schedule_id = obj.isNull("schedule_id") ? 0 : obj.getInt("schedule_id");
                        Teacher teacher = new Teacher(
                                obj.getInt("user_id"), obj.getString("first_name"),
                                obj.getString("last_name"), LocalDate.parse(obj.getString("birth_date")),
                                obj.getString("address"), obj.getString("phone"), Role.TEACHER,
                                obj.getString("speciality"), schedule_id);
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
        try {
            JSONObject json = new JSONObject();
            json.put("first_name", teacher.getFirstName());
            json.put("last_name", teacher.getLastName());
            json.put("birth_date", teacher.getBirthDate().toString());
            json.put("address", teacher.getAddress());
            json.put("phone", teacher.getPhone());
            json.put("role", teacher.getRole().toString());
            json.put("password", teacher.getPassword());
            json.put("speciality", teacher.getSpeciality());

            Log.d("AddTeacherJSON", json.toString());

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    BASE_URL,
                    json,
                    response -> Toast.makeText(context, "Teacher added successfully", Toast.LENGTH_SHORT).show(),
                    error -> {
                        error.printStackTrace();
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            try {
                                String body = new String(error.networkResponse.data, "UTF-8");
                                Log.e("VolleyErrorBody", body);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                        Toast.makeText(context, "Failed to add teacher", Toast.LENGTH_SHORT).show();
                    }
            );

            queue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error creating request JSON", Toast.LENGTH_SHORT).show();
        }
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
                params.put("password", teacher.getPassword());

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
