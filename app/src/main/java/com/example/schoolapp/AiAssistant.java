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
 * Activity for AI Assistant chat using Volley to call OpenAI Chat Completions.
 */
public class AiAssistant extends AppCompatActivity {

    private static final String TAG_CHAT_REQUEST = "CHAT_REQUEST";
    private static final String OPENAI_API_KEY = "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";

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


        // Optionally enable back button:
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // toolbar.setNavigationOnClickListener(v -> finish());

        rvMessages = findViewById(R.id.rv_messages);
        messageList = new ArrayList<>();
        adapter = new MessagesAdapter(messageList);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setStackFromEnd(true); // show latest at bottom
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
            jsonBody.put("model", "gpt-3.5-turbo");
            JSONArray messagesArray = new JSONArray();

            // system prompt
            JSONObject systemObj = new JSONObject();
            systemObj.put("role", "system");
            systemObj.put("content", "You are a helpful AI assistant.");
            messagesArray.put(systemObj);


            int windowSize = 12;
            int start = Math.max(0, messageList.size() - windowSize);
            for (int i = start; i < messageList.size(); i++) {
                AiMessage msg = messageList.get(i);
                if (msg.isTypingIndicator()) {
                    continue;
                }
                JSONObject obj = new JSONObject();
                if (msg.isUser()) {
                    obj.put("role", "user");
                } else if (msg.isAssistant()) {
                    obj.put("role", "assistant");
                } else if (msg.isSystem()) {
                    obj.put("role", "system");
                } else {

                    obj.put("role", "assistant");
                }
                String content = msg.getContent();
                if (content == null) {
                    content = ""; //
                }
                obj.put("content", content);
                messagesArray.put(obj);
            }
            jsonBody.put("messages", messagesArray);

        } catch (JSONException e) {

            removeTypingIndicator(typingMsg);
            Toast.makeText(this, "JSON error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        String url = "https://api.openai.com/v1/chat/completions";
        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> {

                    removeTypingIndicator(typingMsg);


                    try {
                        JSONArray choices = response.optJSONArray("choices");
                        if (choices != null && choices.length() > 0) {
                            JSONObject firstChoice = choices.getJSONObject(0);
                            JSONObject messageObj = firstChoice.getJSONObject("message");
                            String reply = messageObj.optString("content", "").trim();
                            if (!reply.isEmpty()) {
                                AiMessage botMsg = AiMessage.createAssistantMessage(reply);
                                messageList.add(botMsg);
                                adapter.notifyItemInserted(messageList.size() - 1);
                                rvMessages.scrollToPosition(messageList.size() - 1);
                            } else {
                                Toast.makeText(AiAssistant.this, "Empty response from AI.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AiAssistant.this, "No choices in AI response.", Toast.LENGTH_SHORT).show();
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
                headers.put("Authorization", "Bearer " + OPENAI_API_KEY);
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
