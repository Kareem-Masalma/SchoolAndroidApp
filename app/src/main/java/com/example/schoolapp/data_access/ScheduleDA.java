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
import com.example.schoolapp.models.ScheduleSubject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleDA implements IScheduleDA {
    private final String BASE_URL = "http://localhost/school/schedule_subject.php";
    private final RequestQueue queue;

    public ScheduleDA(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    public interface SingleScheduleCallback {
        void onSuccess(ScheduleSubject schedule);

        void onError(String error);
    }

    public interface ScheduleListCallback {
        void onSuccess(List<ScheduleSubject> schedules);

        void onError(String error);
    }

    public interface ScheduleCallback {
        void onSuccess(String message);

        void onError(String error);
    }

    @Override
    public void getAllSchedules(ScheduleListCallback callback) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, BASE_URL, null,
                response -> {
                    try {
                        List<ScheduleSubject> list = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            ScheduleSubject schedule = new ScheduleSubject(
                                    obj.getInt("schedule_subject_id"),
                                    obj.getInt("schedule_id"),
                                    obj.getInt("class_id"),
                                    obj.getInt("subject_id"),
                                    obj.getString("day"),
                                    obj.getString("start_time"),
                                    obj.getString("end_time")
                            );
                            list.add(schedule);
                        }
                        callback.onSuccess(list);
                    } catch (JSONException e) {
                        callback.onError("Failed to parse schedule data");
                    }
                },
                error -> callback.onError("Connection error: " + error.getMessage())
        );
        queue.add(request);
    }

    public void getScheduleById(int id, SingleScheduleCallback callback) {
        String url = BASE_URL + "?schedule_subject_id=" + id;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.length() > 0) {
                            JSONObject obj = response.getJSONObject(0);
                            ScheduleSubject schedule = new ScheduleSubject(
                                    obj.getInt("schedule_subject_id"),
                                    obj.getInt("schedule_id"),
                                    obj.getInt("class_id"),
                                    obj.getInt("subject_id"),
                                    obj.getString("day"),
                                    obj.getString("start_time"),
                                    obj.getString("end_time")
                            );
                            callback.onSuccess(schedule);
                        } else {
                            callback.onError("Schedule not found");
                        }
                    } catch (JSONException e) {
                        callback.onError("Parse error: " + e.getMessage());
                    }
                },
                error -> callback.onError("Connection error: " + error.getMessage())
        );

        queue.add(request);
    }


    @Override
    public void addScheduleSubject(ScheduleSubject schedule, ScheduleCallback callback) {
        StringRequest request = new StringRequest(Request.Method.POST, BASE_URL,
                response -> callback.onSuccess("Schedule added successfully"),
                error -> callback.onError("Failed to add: " + error.getMessage())
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("schedule_id", String.valueOf(schedule.getScheduleId()));
                params.put("class_id", String.valueOf(schedule.getClassId()));
                params.put("subject_id", String.valueOf(schedule.getSubjectId()));
                params.put("day", schedule.getDay());
                params.put("start_time", schedule.getStartTime());
                params.put("end_time", schedule.getEndTime());
                return params;
            }
        };
        queue.add(request);
    }

    @Override
    public void updateSchedule(ScheduleSubject schedule, ScheduleCallback callback) {
        StringRequest request = new StringRequest(Request.Method.PUT, BASE_URL,
                response -> callback.onSuccess("Schedule updated successfully"),
                error -> callback.onError("Failed to update: " + error.getMessage())
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("schedule_subject_id", String.valueOf(schedule.getScheduleId()));
                params.put("schedule_id", String.valueOf(schedule.getScheduleId()));
                params.put("class_id", String.valueOf(schedule.getClassId()));
                params.put("subject_id", String.valueOf(schedule.getSubjectId()));
                params.put("day", schedule.getDay());
                params.put("start_time", schedule.getStartTime());
                params.put("end_time", schedule.getEndTime());
                return params;
            }
        };

        queue.add(request);
    }


    @Override
    public void deleteScheduleSubject(int id, ScheduleCallback callback) {
        String url = BASE_URL + "?schedule_subject_id=" + id;
        StringRequest request = new StringRequest(Request.Method.DELETE, url,
                response -> callback.onSuccess("Schedule deleted"),
                error -> callback.onError("Failed to delete: " + error.getMessage())
        );
        queue.add(request);
    }
}
