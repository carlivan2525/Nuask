package com.example.nuaskapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomePage extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView welcomeTextView;
    EditText messageEditText;
    ImageButton sendButton, menuButton;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    List<Message> messageList;
    MessageAdapter messageAdapter;
    FirebaseFirestore db;
    public static final MediaType JSON = MediaType.get("application/json");
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        db = FirebaseFirestore.getInstance();

        testFirestoreConnection();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuButton = findViewById(R.id.menu_button);
        messageList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        welcomeTextView = findViewById(R.id.welcome_text);
        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_btn);

        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sendButton.setOnClickListener((v) -> {
            String question = messageEditText.getText().toString().trim();
            if (!question.isEmpty()) {
                addToChat(question, Message.SENT_BY_ME);
                messageEditText.setText("");

                getFirestoreResponse(question);

                welcomeTextView.setVisibility(View.GONE);
            } else {
                messageEditText.setError("Please enter a message!");
            }
        });

        menuButton.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_about) {
                startActivity(new Intent(HomePage.this, about.class));
            } else if (id == R.id.nav_feedback) {
                startActivity(new Intent(HomePage.this, Feedback.class));
            } else if (id == R.id.nav_logout) {
                Intent intent = new Intent(HomePage.this, LoginPage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void testFirestoreConnection() {
        db.collection("responses")
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firestore", "🔥 Firestore is CONNECTED! ✅");
                    } else {
                        Log.e("Firestore", "❌ Firestore NOT connected! Error: " + task.getException());
                    }
                });
    }

    private void getFirestoreResponse(String question) {
        db.collection("responses")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String bestMatch = null;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String storedQuestion = document.getString("question");
                            List<String> variations = (List<String>) document.get("variations");

                            if (storedQuestion.equalsIgnoreCase(question) ||
                                    (variations != null && variations.contains(question.toLowerCase()))) {
                                bestMatch = document.getString("answer");
                                break;
                            }
                        }

                        if (bestMatch != null) {
                            addResponse(bestMatch);
                        } else {
                            callAPI(question);
                        }
                    } else {
                        callAPI(question);
                    }
                });
    }

    void addToChat(String message, String sentBy) {
        runOnUiThread(() -> {
            messageList.add(new Message(message, sentBy));
            messageAdapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
        });
    }

    void addResponse(String message) {
        runOnUiThread(() -> {
            removeTypingMessage();
            messageList.add(new Message(message, Message.SENT_BY_BOT));
            messageAdapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
        });
    }

    void callAPI(String question) {
        runOnUiThread(() -> {
            messageList.add(new Message("Typing...", Message.SENT_BY_BOT));
            messageAdapter.notifyDataSetChanged();
        });

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "gemini-1.5-flash");

            JSONArray contents = new JSONArray();
            JSONObject content = new JSONObject();
            JSONArray parts = new JSONArray();
            JSONObject part = new JSONObject();

            part.put("text", question);
            parts.put(part);
            content.put("parts", parts);
            contents.put(content);

            jsonBody.put("contents", contents);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=AIzaSyDz45E3eD8K8A9OkmOCbHeeRxFg4T8Ivrw")
                .header("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                removeTypingMessage();
                addResponse("❌ API Request Failed: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseData);

                        JSONArray candidates = jsonObject.getJSONArray("candidates");
                        if (candidates.length() > 0) {
                            JSONObject firstCandidate = candidates.getJSONObject(0);
                            JSONObject content = firstCandidate.getJSONObject("content");
                            JSONArray parts = content.getJSONArray("parts");

                            if (parts.length() > 0) {
                                String result = parts.getJSONObject(0).getString("text");
                                addResponse(result.trim());
                            } else {
                                removeTypingMessage();
                                addResponse("❌ No response found.");
                            }
                        } else {
                            removeTypingMessage();
                            addResponse("❌ No candidates found.");
                        }
                    } catch (JSONException e) {
                        removeTypingMessage();
                        addResponse("❌ JSON Parsing Error: " + e.getMessage());
                    }
                } else {
                    removeTypingMessage();
                    addResponse("❌ API Error " + response.code() + ": " + response.message());
                }
            }
        });
    }

    void removeTypingMessage() {
        runOnUiThread(() -> {
            if (!messageList.isEmpty() && messageList.get(messageList.size() - 1).getMessage().equals("Typing...")) {
                messageList.remove(messageList.size() - 1);
                messageAdapter.notifyDataSetChanged();
            }
        });
    }
}