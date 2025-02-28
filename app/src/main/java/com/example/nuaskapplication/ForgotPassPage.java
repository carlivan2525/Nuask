package com.example.nuaskapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ForgotPassPage extends AppCompatActivity {

    private EditText emailEditText;
    private Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotpasspage);

        emailEditText = findViewById(R.id.email_edit_text);
        submitBtn = findViewById(R.id.submit_btn);


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailEditText.getText().toString().trim();
                if (email.isEmpty()) {
                    Toast.makeText(ForgotPassPage.this, "Please enter your email address", Toast.LENGTH_SHORT).show();
                } else {

                    sendRecoveryCode(email);
                }
            }
        });
    }


    private void sendRecoveryCode(String email) {

        Toast.makeText(ForgotPassPage.this, "Recovery code sent! Please check your email.", Toast.LENGTH_SHORT).show();


        Intent intent = new Intent(ForgotPassPage.this, LoginPage.class);
        startActivity(intent);
        finish();
    }
}
