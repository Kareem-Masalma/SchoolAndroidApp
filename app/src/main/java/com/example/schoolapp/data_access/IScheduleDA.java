package com.example.schoolapp.data_access;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.schoolapp.models.ScheduleSubject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IScheduleDA {
    void getAllSchedules(ScheduleDA.ScheduleListCallback callback);

    void getScheduleById(int id, ScheduleDA.SingleScheduleCallback callback);

    void addScheduleSubject(ScheduleSubject schedule, ScheduleDA.ScheduleCallback callback);
    void updateSchedule(ScheduleSubject schedule, ScheduleDA.ScheduleCallback callback);

    void deleteScheduleSubject(int id, ScheduleDA.ScheduleCallback callback);
}
