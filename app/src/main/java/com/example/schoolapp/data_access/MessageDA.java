package com.example.schoolapp.data_access;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.example.schoolapp.models.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MessageDA implements  IMessageDA {
    private final RequestQueue queue;
    private final String BASE = "http://" + DA_Config.BACKEND_IP_ADDRESS + "/" + DA_Config.BACKEND_DIR + "/message.php";

    public MessageDA(Context ctx) {
        queue = Volley.newRequestQueue(ctx);
    }

    public void getMessageById(int messageId, IMessageDA.SingleMessageCallback cb) {
        String url = BASE + "?message_id=" + messageId;
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET, url, null,
                resp -> {
                    try {
                        cb.onSuccess(parseMessage(resp));
                    } catch (JSONException ex) {
                        cb.onError("Malformed data");
                    }
                },
                err -> cb.onError("Fetch failed")
        );
        queue.add(req);
    }

    public void getAllMessages(IMessageDA.MessageListCallback cb) {
        JsonArrayRequest req = new JsonArrayRequest(
                Request.Method.GET, BASE, null,
                resp -> {
                    try {
                        List<Message> list = new ArrayList<>();
                        for (int i = 0; i < resp.length(); i++) {
                            list.add(parseMessage(resp.getJSONObject(i)));
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

    public void sendMessage(Message m, IMessageDA.BaseCallback cb) {
        try {
            JSONObject b = new JSONObject();
            b.put("from_user_id", m.getFrom_user_id());
            b.put("to_user_id", m.getTo_user_id());
            b.put("title", m.getTitle());
            b.put("content", m.getContent());
            b.put("sent_date", m.getSentDate().toString());
            JsonObjectRequest req = new JsonObjectRequest(
                    Request.Method.POST, BASE, b,
                    resp -> handle(cb, resp),
                    err -> cb.onError("Send failed")
            );
            queue.add(req);
        } catch (JSONException ex) {
            cb.onError("Invalid JSON");
        }
    }

    public void deleteMessage(int messageId, IMessageDA.BaseCallback cb) {
        String url = BASE + "?message_id=" + messageId;
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.DELETE, url, null,
                resp -> handle(cb, resp),
                err -> cb.onError("Delete failed")
        );
        queue.add(req);
    }

    // Helpers

    private Message parseMessage(JSONObject o) throws JSONException {
        return new Message(
                o.getInt("message_id"),
                o.getInt("from_user_id"),
                o.getInt("to_user_id"),
                o.getString("title"),
                o.getString("content"),
                LocalDate.parse(o.getString("sent_date"))
        );
    }

    private void handle(BaseCallback cb, JSONObject resp) {
        boolean ok  = resp.optBoolean("success", false);
        String  msg = resp.optString("message", ok ? "OK" : "Error");
        if (ok) cb.onSuccess(msg);
        else    cb.onError(msg);
    }


}
