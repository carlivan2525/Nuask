package com.example.nuaskapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class Feedback extends AppCompatActivity {

    private EditText etFeedback;
    private RatingBar ratingBar;
    private Button btnSubmit;
    private FirebaseFirestore db;  // Firestore instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        etFeedback = findViewById(R.id.et_feedback);
        ratingBar = findViewById(R.id.rating_bar);
        btnSubmit = findViewById(R.id.btn_submit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedback = etFeedback.getText().toString().trim();
                float rating = ratingBar.getRating();

                if (feedback.isEmpty()) {
                    Toast.makeText(Feedback.this, "Please enter your feedback.", Toast.LENGTH_SHORT).show();
                } else {

                    Map<String, Object> feedbackData = new HashMap<>();
                    feedbackData.put("feedback", feedback);
                    feedbackData.put("rating", rating);
                    feedbackData.put("timestamp", System.currentTimeMillis());

                    // Save data to Firestore
                    db.collection("feedbacks")
                            .add(feedbackData)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(Feedback.this, "Feedback Submitted!", Toast.LENGTH_LONG).show();
                                etFeedback.setText("");
                                ratingBar.setRating(3);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(Feedback.this, "Failed to submit feedback", Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });
    }
}
