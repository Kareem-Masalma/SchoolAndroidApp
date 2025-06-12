// File: AiAssistant.java
package com.example.schoolapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolapp.adapters.MessagesAdapter;
import com.example.schoolapp.data_access.DA_Config;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.example.schoolapp.models.AiMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Activity for AI Assistant chat using Volley to call Google Gemini generateContent endpoint.
 */
public class AiAssistant extends AppCompatActivity {

    private static final String TAG_CHAT_REQUEST = "CHAT_REQUEST";

    private RecyclerView rvMessages;
    private MessagesAdapter adapter;
    private List<AiMessage> messageList;
    private TextInputEditText etQuery;
    private FloatingActionButton btnSend;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ai_assistant);

        requestQueue = Volley.newRequestQueue(this);

        View root = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets sysBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets imeBars = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(
                    sysBars.left,
                    sysBars.top,
                    sysBars.right,
                    sysBars.bottom + imeBars.bottom
            );
            return insets;
        });


        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        rvMessages = findViewById(R.id.rv_messages);
        messageList = new ArrayList<>();
        adapter = new MessagesAdapter(messageList);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setStackFromEnd(true);
        rvMessages.setLayoutManager(lm);
        rvMessages.setAdapter(adapter);


        etQuery = findViewById(R.id.et_query);
        btnSend = findViewById(R.id.btn_send);
        btnSend.setOnClickListener(v -> {
            String text = etQuery.getText() != null ? etQuery.getText().toString().trim() : "";
            if (!TextUtils.isEmpty(text)) {
                sendUserMessage(text);
            }
        });
        etQuery.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                String text = etQuery.getText() != null ? etQuery.getText().toString().trim() : "";
                if (!TextUtils.isEmpty(text)) {
                    sendUserMessage(text);
                }
                return true;
            }
            return false;
        });
    }

    /**
     * Sends a user message: appends to UI, shows typing indicator, builds Gemini JSON, and sends request.
     */
    private void sendUserMessage(String text) {
        // 1. Add user message to RecyclerView
        AiMessage userMsg = AiMessage.createUserMessage(text);
        messageList.add(userMsg);
        adapter.notifyItemInserted(messageList.size() - 1);
        rvMessages.scrollToPosition(messageList.size() - 1);
        etQuery.setText("");

        AiMessage typingMsg = AiMessage.createTypingIndicator();
        messageList.add(typingMsg);
        int typingIndex = messageList.size() - 1;
        adapter.notifyItemInserted(typingIndex);
        rvMessages.scrollToPosition(typingIndex);

        JSONObject jsonBody = new JSONObject();
        try {
            JSONArray contentsArray = new JSONArray();

            if (messageList.size() == 1) {
                JSONObject sysEntry = new JSONObject();
                sysEntry.put("role", "user");
                JSONArray partsSys = new JSONArray();
                JSONObject partSys = new JSONObject();
                partSys.put("text", "You are a helpful AI assistant. Your job is to help students improve their grades, provide study tips, and provide any help the students need.");
                partsSys.put(partSys);
                sysEntry.put("parts", partsSys);
                contentsArray.put(sysEntry);
            }
            int windowSize = 12;
            int start = Math.max(0, messageList.size() - windowSize);
            for (int i = start; i < messageList.size(); i++) {
                AiMessage msg = messageList.get(i);
                if (msg.isTypingIndicator()) {
                    continue;
                }
                JSONObject entry = new JSONObject();
                if (msg.isUser()) {
                    entry.put("role", "user");
                } else if (msg.isAssistant()) {
                    entry.put("role", "model");
                } else {
                    // system or others: treat as user
                    entry.put("role", "user");
                }
                JSONArray parts = new JSONArray();
                JSONObject part = new JSONObject();
                String content = msg.getContent();
                if (content == null) content = "";
                part.put("text", content);
                parts.put(part);
                entry.put("parts", parts);
                contentsArray.put(entry);
            }
            jsonBody.put("contents", contentsArray);

            JSONObject generationConfig = new JSONObject();
            generationConfig.put("temperature", 0.7);
            generationConfig.put("maxOutputTokens", 1024);
            generationConfig.put("topP", 0.95);
            generationConfig.put("topK", 40);
            jsonBody.put("generationConfig", generationConfig);

        } catch (JSONException e) {
            removeTypingIndicator(typingMsg);
            Toast.makeText(this, "JSON error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + DA_Config.AI_API_KEY;

        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> {
                    removeTypingIndicator(typingMsg);

                    try {
                        JSONArray candidates = response.optJSONArray("candidates");
                        if (candidates != null && candidates.length() > 0) {
                            JSONObject firstCand = candidates.getJSONObject(0);
                            JSONObject contentObj = firstCand.getJSONObject("content");
                            JSONArray parts = contentObj.optJSONArray("parts");
                            if (parts != null && parts.length() > 0) {
                                String reply = parts.getJSONObject(0).optString("text", "").trim();
                                if (!reply.isEmpty()) {
                                    AiMessage botMsg = AiMessage.createAssistantMessage(reply);
                                    messageList.add(botMsg);
                                    adapter.notifyItemInserted(messageList.size() - 1);
                                    rvMessages.scrollToPosition(messageList.size() - 1);
                                } else {
                                    Toast.makeText(AiAssistant.this, "Empty response from AI.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(AiAssistant.this, "No parts in AI response.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AiAssistant.this, "No candidates in AI response.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(AiAssistant.this, "Response parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    removeTypingIndicator(typingMsg);
                    String msgErr = "Network error";
                    if (error.networkResponse != null) {
                        int statusCode = error.networkResponse.statusCode;
                        msgErr += " (HTTP " + statusCode + ")";
                        if (error.networkResponse.data != null) {
                            String body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        }
                    } else if (error.getMessage() != null) {
                        msgErr += ": " + error.getMessage();
                    }
                    Toast.makeText(AiAssistant.this, msgErr, Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };


        jsonRequest.setTag(TAG_CHAT_REQUEST);
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                30_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));


        requestQueue.add(jsonRequest);
    }


    private void removeTypingIndicator(AiMessage typingMsg) {
        int idx = messageList.indexOf(typingMsg);
        if (idx >= 0) {
            messageList.remove(idx);
            adapter.notifyItemRemoved(idx);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG_CHAT_REQUEST);
        }
    }
}
