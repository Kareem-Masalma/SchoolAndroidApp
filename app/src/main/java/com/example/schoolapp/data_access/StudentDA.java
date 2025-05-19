package com.example.schoolapp.data_access;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolapp.models.Role;
import com.example.schoolapp.models.Student;
import com.example.schoolapp.models.Teacher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StudentDA implements IStudentDA {
    private RequestQueue queue;
    private final String BASE_URL = "http://localhost/phpmyadmin/index.php"; // change "student/" if needed


    public StudentDA(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    @Override
    public void getStudentById(int id, SingleStudentCallback callback) {
        String url = BASE_URL + "getStudent.php?id=" + id;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject obj = response.getJSONObject(0);
                    Student student = new Student(
                            obj.getInt("user_id"), obj.getString("first_name"),
                            obj.getString("last_name"), LocalDate.parse("birth_date"),
                            obj.getString("address"), obj.getString("phone"), Role.STUDENT,
                            obj.getInt("class_id"));
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
        String url = BASE_URL + "addTeacher.php";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    List<Student> students = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(0);
                        Student student = new Student(
                                obj.getInt("user_id"), obj.getString("first_name"),
                                obj.getString("last_name"), LocalDate.parse("birth_date"),
                                obj.getString("address"), obj.getString("phone"), Role.STUDENT,
                                obj.getInt("class_id"));
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
    public void addStudent(Student student, BaseCallback callback) {

    }

    @Override
    public void updateStudent(Student student, BaseCallback callback) {

    }

    @Override
    public void deleteStudent(int id, BaseCallback callback) {

    }

    public interface SingleStudentCallback {
        void onSuccess(Student student);

        void onError(String error);
    }

    public interface StudentListCallback {
        void onSuccess(List<Student> teachers);

        void onError(String error);
    }

    public interface BaseCallback {
        void onSuccess(String message);

        void onError(String error);
    }
}
