package com.example.schoolapp.data_access;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolapp.models.Role;
import com.example.schoolapp.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;

public class LoginDA implements ILoginDA {
    private final RequestQueue queue;
    private final String LOGIN_URL = "http://" + DA_Config.BACKEND_IP_ADDRESS + "/" + DA_Config.BACKEND_DIR + "/login.php";

    public LoginDA(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    public void login(String userId, String password, ILoginDA.LoginCallback callback) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("user_id", userId);
            payload.put("password", password);

            JsonObjectRequest req = new JsonObjectRequest(
                    Request.Method.POST, LOGIN_URL, payload,
                    resp -> {
                        boolean success = resp.optBoolean("success", false);
                        if (success) {
                            try{
                                JSONObject userJson = resp.getJSONObject("user");

                                User user = new User(
                                        userJson.getInt("user_id"),
                                        userJson.getString("first_name"),
                                        userJson.getString("last_name"),
                                        LocalDate.parse(userJson.getString("birth_date")),
                                        userJson.getString("address"),
                                        userJson.getString("phone"),
                                        Role.valueOf(userJson.getString("role").toUpperCase()) // just in case
                                );
                            callback.onSuccess(user);
                            } catch (JSONException e) {
                                callback.onError("Malformed Data");

                            }
                        } else {
                            callback.onError(resp.optString("message", "Login failed"));
                        }
                    },
                    error -> callback.onError("Network error")
            );

            queue.add(req);
        } catch (JSONException e) {
            callback.onError("JSON error");
        }
    }

}
