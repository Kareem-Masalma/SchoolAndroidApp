package com.example.schoolapp.data_access;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.schoolapp.models.Class;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassDA implements IClassDA {
    private final RequestQueue queue;
    private final String BASE = "http://" + DA_Config.BACKEND_IP_ADDRESS + "/" + DA_Config.BACKEND_DIR + "/class.php";

    public ClassDA(Context ctx) {
        queue = Volley.newRequestQueue(ctx);
    }

    public void getClassById(int id, SingleClassCallback cb) {
        String url = BASE + "?class_id=" + id;
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                resp -> {
                    try {
                        cb.onSuccess(parseClass(resp));
                    } catch (JSONException ex) {
                        cb.onError("Malformed data");
                    }
                },
                err -> cb.onError("Fetch failed")
        );
        queue.add(req);
    }

    public void getTeacherClasses(int id, ClassListCallback cb) {
        String url = BASE + "?user_id=" + id;
        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, BASE, null,
                resp -> {
                    try {
                        List<Class> list = new ArrayList<>();
                        for (int i = 0; i < resp.length(); i++) {
                            list.add(parseClass(resp.getJSONObject(i)));
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

    public void getAllClasses(ClassListCallback cb) {
        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, BASE, null,
                resp -> {
                    try {
                        List<Class> list = new ArrayList<>();
                        for (int i = 0; i < resp.length(); i++) {
                            list.add(parseClass(resp.getJSONObject(i)));
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

    public void addClass(Class c, BaseCallback cb) {
        StringRequest req = new StringRequest(Request.Method.POST, BASE,
                resp -> cb.onSuccess("Class added successfully"),
                err -> cb.onError("Add failed")
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("class_name", c.getClassName());
                params.put("class_manager", String.valueOf(c.getClassManagerId()));
                params.put("schedule_id", String.valueOf(c.getScheduleId()));
                return params;
            }
        };
        queue.add(req);
    }

    public void updateClass(Class c, BaseCallback cb) {
        StringRequest req = new StringRequest(Request.Method.PUT, BASE,
                resp -> cb.onSuccess("Class updated successfully"),
                err -> cb.onError("Update failed")
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("class_id", String.valueOf(c.getClassId()));
                params.put("class_name", c.getClassName());
                params.put("class_manager", String.valueOf(c.getClassManagerId()));
                params.put("schedule_id", String.valueOf(c.getScheduleId()));
                return params;
            }
        };
        queue.add(req);
    }

    public void deleteClass(int id, BaseCallback cb) {
        String url = BASE + "?class_id=" + id;
        StringRequest req = new StringRequest(Request.Method.DELETE, url,
                resp -> cb.onSuccess("Deleted"),
                err -> cb.onError("Delete failed")
        );
        queue.add(req);
    }

    private Class parseClass(JSONObject o) throws JSONException {
        int schedule_id = o.isNull("schedule_id") ? 0 : o.getInt("schedule_id");
        return new Class(
                o.getInt("class_id"),
                o.getString("class_name"),
                o.getInt("class_manager"),
                o.optString("manager_name", ""),
                schedule_id
        );
    }

    public interface SingleClassCallback {
        void onSuccess(Class c);

        void onError(String error);
    }

    public interface ClassListCallback {
        void onSuccess(List<Class> list);

        void onError(String error);
    }

    public interface BaseCallback {
        void onSuccess(String msg);

        void onError(String error);
    }
}
