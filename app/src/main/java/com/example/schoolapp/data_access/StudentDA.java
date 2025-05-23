package com.example.schoolapp.data_access;

import android.content.Context;
import android.util.Log;

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

public class StudentDA implements IStudentDA {
    private final RequestQueue queue;
    private final String BASE = "http://10.0.0.11/androidBackend/student.php"; // the emulator needs the pc's local ip address,
    // using localhost here won't work because it would refer to the emulator's internal ip

    public StudentDA(Context ctx) {
        queue = Volley.newRequestQueue(ctx);
    }

    @Override
    public void getStudentById(int userId, SingleStudentCallback cb) {
        String url = BASE + "?user_id=" + userId;
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET, url, null,
                resp -> {
                    try {
                        Log.i("resp" , resp.toString());
                        cb.onSuccess(parseStudent(resp));
                    } catch (JSONException ex) {
                        cb.onError("Malformed data");
                    }
                },
                err -> cb.onError("Fetch failed")
        );
        queue.add(req);
    }

    @Override
    public void getAllStudents(StudentListCallback cb) {
        JsonArrayRequest req = new JsonArrayRequest(
                Request.Method.GET, BASE, null,
                resp -> {
                    try {
                        List<Student> list = new ArrayList<>();
                        for (int i = 0; i < resp.length(); i++) {
                            list.add(parseStudent(resp.getJSONObject(i)));
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

    @Override
    public void addStudent(Student s, BaseCallback cb) {
        try {
            JSONObject b = new JSONObject();
            b.put("first_name",  s.getFirstName());
            b.put("last_name",   s.getLastName());
            b.put("birth_date",  s.getBirthDate().toString());
            b.put("address",     s.getAddress());
            b.put("phone",       s.getPhone());
            b.put("role",        s.getRole().name());
            b.put("class_id",    s.getClass_id());
            b.put("password" , s.getPassword());
            JsonObjectRequest req = new JsonObjectRequest(
                    Request.Method.POST, BASE, b,
                    resp -> handle(cb, resp),
                    err -> cb.onError("Add failed")
            );
            queue.add(req);

        } catch (JSONException ex) {
            cb.onError("Invalid JSON");
        }
    }

    @Override
    public void updateStudent(Student s, BaseCallback cb) {
        try {
            JSONObject b = new JSONObject();
            b.put("user_id",     s.getUser_id());
            b.put("first_name",  s.getFirstName());
            b.put("last_name",   s.getLastName());
            b.put("birth_date",  s.getBirthDate().toString());
            b.put("address",     s.getAddress());
            b.put("phone",       s.getPhone());
            b.put("role",        s.getRole().name());
            b.put("class_id",    s.getClass_id());
            b.put("password" , s.getPassword());
            JsonObjectRequest req = new JsonObjectRequest(
                    Request.Method.PUT, BASE, b,
                    resp -> handle(cb, resp),
                    err -> cb.onError("Update failed")
            );
            queue.add(req);

        } catch (JSONException ex) {
            cb.onError("Invalid JSON");
        }
    }

    @Override
    public void deleteStudent(int userId, BaseCallback cb) {
        String url = BASE + "?user_id=" + userId;
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.DELETE, url, null,
                resp -> handle(cb, resp),
                err -> cb.onError("Delete failed")
        );
        queue.add(req);
    }

    // Helpers

    private Student parseStudent(JSONObject o) throws JSONException {
      Student  student =  new Student(
                o.getInt("user_id"),
                o.getString("first_name"),
                o.getString("last_name"),
                LocalDate.parse(o.getString("birth_date")),
                o.getString("address"),
                o.getString("phone"),
                Role.valueOf(o.getString("role")),
                o.getInt("class_id"));
                student.setPassword(o.getString("password"));
        return student;
    }

    private void handle(BaseCallback cb, JSONObject resp) {
        boolean ok  = resp.optBoolean("success", false);
        String  msg = resp.optString("message", ok ? "OK" : "Error");
        if (ok) cb.onSuccess(msg);
        else    cb.onError(msg);
    }

    // Callback interfaces (unchanged)
    public interface SingleStudentCallback {
        void   onSuccess(Student s);
        void   onError(String error);
    }
    public interface StudentListCallback {
        void   onSuccess(List<Student> list);
        void   onError(String error);
    }
    public interface BaseCallback {
        void   onSuccess(String message);
        void   onError(String error);
    }
}