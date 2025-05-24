package com.example.schoolapp.data_access;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolapp.models.Attendance_student;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class AttendanceStudentDA implements IAttendanceStudentDA {
    private final RequestQueue queue;
    private final String BASE = "http://10.0.0.11/androidBackend/attendance_student.php";

    public AttendanceStudentDA(Context ctx) {
        this.queue = Volley.newRequestQueue(ctx);
    }

    @Override
    public void getAttendanceStudent(int attendanceId, int studentId, SingleAttendance_StudentCallback cb) {
        String url = BASE + "?attendance_id=" + attendanceId + "&student_id=" + studentId;
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET, url, null,
                resp -> {
                    try { cb.onSuccess(parse(resp)); }
                    catch (JSONException ex) { cb.onError("Malformed data"); }
                },
                err -> cb.onError("Fetch failed")
        );
        queue.add(req);
    }

    @Override
    public void getAllAttendanceStudents(Attendance_StudentListCallback cb) {
        JsonArrayRequest req = new JsonArrayRequest(
                Request.Method.GET, BASE, null,
                resp -> {
                    try {
                        List<Attendance_student> list = new ArrayList<>();
                        for (int i = 0; i < resp.length(); i++) {
                            list.add(parse(resp.getJSONObject(i)));
                        }
                        cb.onSuccess(list);
                    } catch (JSONException ex) { cb.onError("Malformed list"); }
                },
                err -> cb.onError("Fetch failed")
        );
        queue.add(req);
    }

    @Override
    public void addAttendanceStudent(Attendance_student as, IAttendanceStudentDA.BaseCallback cb) {
        try {
            JSONObject b = new JSONObject();
            b.put("attendance_id", as.getAttendance_id());
            b.put("student_id", as.getStudent_id());
            b.put("attended", as.getAttended());
            b.put("excuse", as.getExcuse());
            JsonObjectRequest req = new JsonObjectRequest(
                    Request.Method.POST, BASE, b,
                    resp -> handle(cb, resp),
                    err -> cb.onError("Add failed")
            );
            queue.add(req);
        } catch (JSONException ex) { cb.onError("Invalid JSON"); }
    }

    @Override
    public void updateAttendanceStudent(Attendance_student as, IAttendanceStudentDA.BaseCallback cb) {
        try {
            JSONObject b = new JSONObject();
            b.put("attendance_id", as.getAttendance_id());
            b.put("student_id", as.getStudent_id());
            b.put("attended", as.getAttended());
            b.put("excuse", as.getExcuse());
            JsonObjectRequest req = new JsonObjectRequest(
                    Request.Method.PUT, BASE, b,
                    resp -> handle(cb, resp),
                    err -> cb.onError("Update failed")
            );
            queue.add(req);
        } catch (JSONException ex) { cb.onError("Invalid JSON"); }
    }

    @Override
    public void deleteAttendanceStudent(int attendanceId, int studentId, IAttendanceStudentDA.BaseCallback cb) {
        String url = BASE + "?attendance_id=" + attendanceId + "&student_id=" + studentId;
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.DELETE, url, null,
                resp -> handle(cb, resp),
                err -> cb.onError("Delete failed")
        );
        queue.add(req);
    }

    private Attendance_student parse(JSONObject o) throws JSONException {
        return new Attendance_student(
                o.getInt("attendance_id"),
                o.getInt("student_id"),
                o.getBoolean("attended"),
                o.optString("excuse", null)
        );
    }

    private void handle(BaseCallback cb, JSONObject resp) {
        boolean ok  = resp.optBoolean("success", false);
        String  msg = resp.optString("message", ok ? "OK" : "Error");
        if (ok) cb.onSuccess(msg); else cb.onError(msg);
    }
}