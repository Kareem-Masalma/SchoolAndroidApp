package com.example.schoolapp.data_access;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolapp.models.Role;
import com.example.schoolapp.models.Student;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentDA implements IStudentDA {
    private final RequestQueue queue;
    private final String BASE_URL = "http://localhost/phpmyadmin/index.php/student.php";


    public StudentDA(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    @Override
    public void getStudentById(int id, SingleStudentCallback callback) {
        String url = BASE_URL + "?id=" + id;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject obj = response.getJSONObject(0);
                    Student student = new Student(
                            obj.getInt("user_id"), obj.getString("first_name"),
                            obj.getString("last_name"), LocalDate.parse("birth_date"),
                            obj.getString("address"), obj.getString("phone"), Role.STUDENT,
                            obj.getInt("class_id"), obj.getString("password"));
                    callback.onSuccess(student);
                } catch (JSONException e) {
                    callback.onError("Student Not Found");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError("Student Not Found");
            }
        });
        queue.add(request);
    }

    @Override
    public void getAllStudents(StudentListCallback callback) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, BASE_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    List<Student> students = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(0);
                        Student student = new Student(
                                obj.getInt("user_id"), obj.getString("first_name"),
                                obj.getString("last_name"), LocalDate.parse(obj.getString("birth_date")),
                                obj.getString("address"), obj.getString("phone"), Role.STUDENT,
                                obj.getInt("class_id"), obj.getString("password"));
                        students.add(student);
                    }
                    callback.onSuccess(students);
                } catch (JSONException e) {
                    callback.onError("No students found");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError("No students found");
            }
        });
        queue.add(request);
    }

    @Override
    public void addStudent(Student student) {
        StringRequest request = new StringRequest(Request.Method.POST, BASE_URL, response -> Log.d("POST_SUCCESS", response),
                error -> Log.e("POST_ERROR", error.toString())) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("first_name", student.getFirstName());
                params.put("last_name", student.getLastName());
                params.put("birth_date", student.getBirthDate().toString());
                params.put("address", student.getAddress());
                params.put("phone", student.getPhone());
                params.put("role", Role.STUDENT.toString());
                params.put("class_id", student.getClass_id().toString());
                return params;
            }
        };
        queue.add(request);
    }

    @Override
    public void updateStudent(Student student) {
        StringRequest request = new StringRequest(Request.Method.PUT, BASE_URL, response -> Log.d("PUT_SUCCESS", response),
                error -> Log.e("PUT_ERROR", error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", student.getUser_id().toString());
                params.put("first_name", student.getFirstName());
                params.put("last_name", student.getLastName());
                params.put("birth_date", student.getBirthDate().toString());
                params.put("address", student.getAddress());
                params.put("phone", student.getPhone());
                params.put("role", Role.STUDENT.toString());
                params.put("class_id", student.getClass_id().toString());
                return params;
            }
        };
        queue.add(request);
    }

    @Override
    public void deleteStudent(int id) {
        StringRequest request = new StringRequest(Request.Method.DELETE, BASE_URL, response -> Log.d("DELETE_SUCCESS", response),
                error -> Log.e("DELETE_ERROR", error.toString())) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", Integer.toString(id));
                return params;
            }
        };
        queue.add(request);
    }

    public interface SingleStudentCallback {
        void onSuccess(Student student);

        void onError(String error);
    }

    public interface StudentListCallback {
        void onSuccess(List<Student> students);

        void onError(String error);
    }

    public interface BaseCallback {
        void onSuccess(String message);

        void onError(String error);
    }
}
