package com.example.schoolapp.data_access;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolapp.models.Attendance;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDA implements IAttendanceDA {
    private final RequestQueue queue;
    private final String BASE = "http://10.0.0.11/androidBackend/attendance.php";

    public AttendanceDA(Context ctx) {
        this.queue = Volley.newRequestQueue(ctx);
    }

    @Override
    public void getAttendanceById(int attendanceId, SingleAttendanceCallback cb) {
        String url = BASE + "?attendance_id=" + attendanceId;
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
    public void getAllAttendance(AttendanceListCallback cb) {
        JsonArrayRequest req = new JsonArrayRequest(
                Request.Method.GET, BASE, null,
                resp -> {
                    try {
                        List<Attendance> list = new ArrayList<>();
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
    public void addAttendance(Attendance a, BaseCallback cb) {
        try {
            JSONObject b = new JSONObject();
            b.put("date", a.getDate().toString());
            b.put("class_id", a.getClassId());

            JsonObjectRequest req = new JsonObjectRequest(
                    Request.Method.POST,
                    BASE,
                    b,
                    resp -> {
                        boolean ok = resp.optBoolean("success", false);
                        if (ok) {
                            // parse the newly-inserted ID from the JSON payload
                            int newId = resp.optInt("attendance_id", -1);
                            if (newId > 0) {
                                cb.onSuccess(String.valueOf(newId));
                            } else {
                                cb.onError("Created but no ID returned");
                            }
                        } else {
                            // your PHP returns 'error' on failure
                            cb.onError(resp.optString("error", "Unknown error"));
                        }
                    },
                    err -> cb.onError("Add failed: " + err.getMessage())
            );

            queue.add(req);

        } catch (JSONException ex) {
            cb.onError("Invalid JSON");
        }
    }
    @Override
    public void updateAttendance(Attendance a, BaseCallback cb) {
        try {
            JSONObject b = new JSONObject();
            b.put("attendance_id", a.getAttendance_id());
            b.put("date", a.getDate().toString());
            b.put("class_id", a.getClassId());
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
    public void deleteAttendance(int attendanceId, BaseCallback cb) {
        String url = BASE + "?attendance_id=" + attendanceId;
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.DELETE, url, null,
                resp -> handle(cb, resp),
                err -> cb.onError("Delete failed")
        );
        queue.add(req);
    }

    private Attendance parse(JSONObject o) throws JSONException {
        return new Attendance(
                o.getInt("attendance_id"),
                LocalDate.parse(o.getString("date")),
                o.getInt("class_id")
        );
    }

    private void handle(BaseCallback cb, JSONObject resp) {
        boolean ok  = resp.optBoolean("success", false);
        String  msg = resp.optString("message", ok ? "OK" : "Error");
        if (ok) cb.onSuccess(msg); else cb.onError(msg);
    }
}