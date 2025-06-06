package com.example.schoolapp.data_access;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolapp.models.Role;
import com.example.schoolapp.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserDA implements IUserDA {
    private final RequestQueue queue;
    private final String BASE = "http://" + DA_Config.BACKEND_IP_ADDRESS + "/" + DA_Config.BACKEND_DIR + "/user.php";

    public UserDA(Context ctx) {
        queue = Volley.newRequestQueue(ctx);
    }

    @Override
    public void getUserById(int id, SingleUserCallback callback) {
        String url = BASE + "?user_id=" + id;
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                resp -> {
                    try {
                        callback.onSuccess(parseUser(resp));
                    } catch (JSONException ex) {
                        callback.onError("Malformed data");
                    }
                },
                err -> callback.onError("Fetch failed")
        );
        queue.add(req);
    }

    @Override
    public void getAllUsers(UserListCallback callback) {
        JsonArrayRequest req = new JsonArrayRequest(
                Request.Method.GET,
                BASE,
                null,
                resp -> {
                    try {
                        List<User> list = new ArrayList<>();
                        for (int i = 0; i < resp.length(); i++) {
                            list.add(parseUser(resp.getJSONObject(i)));
                        }
                        callback.onSuccess(list);
                    } catch (JSONException ex) {
                        callback.onError("Malformed list");
                    }
                },
                err -> callback.onError("Fetch failed")
        );
        queue.add(req);
    }

    @Override
    public void addUser(User user, BaseCallback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("first_name", user.getFirstName());
            body.put("last_name",  user.getLastName());
            body.put("birth_date", user.getBirthDate().toString());
            body.put("address",    user.getAddress());
            body.put("phone",      user.getPhone());
            body.put("role",       user.getRole().name());
//            body.put("password", user.getPassword());
            JsonObjectRequest req = new JsonObjectRequest(
                    Request.Method.POST,
                    BASE,
                    body,
                    resp -> handle(callback, resp),
                    err -> callback.onError("Add failed")
            );
            queue.add(req);
        } catch (JSONException ex) {
            callback.onError("Invalid JSON");
        }
    }

    @Override
    public void updateUser(User user, BaseCallback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("user_id",   user.getUser_id());
            body.put("first_name", user.getFirstName());
            body.put("last_name",  user.getLastName());
            body.put("birth_date", user.getBirthDate().toString());
            body.put("address",    user.getAddress());
            body.put("phone",      user.getPhone());
            body.put("role",       user.getRole().name());
//            body.put("password", user.getPassword());
            JsonObjectRequest req = new JsonObjectRequest(
                    Request.Method.PUT,
                    BASE,
                    body,
                    resp -> handle(callback, resp),
                    err -> callback.onError("Update failed")
            );
            queue.add(req);
        } catch (JSONException ex) {
            callback.onError("Invalid JSON");
        }
    }

    @Override
    public void deleteUser(int id, BaseCallback callback) {
        String url = BASE + "?user_id=" + id;
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                resp -> handle(callback, resp),
                err -> callback.onError("Delete failed")
        );
        queue.add(req);
    }

    private User parseUser(JSONObject o) throws JSONException {
        User user = new User(
                o.getInt("user_id"),
                o.getString("first_name"),
                o.getString("last_name"),
                LocalDate.parse(o.getString("birth_date")),
                o.getString("address"),
                o.getString("phone"),
                Role.valueOf(o.getString("role"))
        );
        return user;
    }

    private void handle(BaseCallback cb, JSONObject resp) {
        boolean ok = resp.optBoolean("success", false);
        String msg = resp.optString("message", ok ? "OK" : "Error");
        if (ok) cb.onSuccess(msg);
        else    cb.onError(msg);
    }
}
