package com.example.nuaskapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginPage extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private ImageView logoLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginpage);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        Button loginBtn = findViewById(R.id.login_btn);
        TextView forgotPasswordTextView = findViewById(R.id.forgot_password);
        Button registerBtn = findViewById(R.id.register_btn);
        logoLoading = findViewById(R.id.logo_loading);
        logoLoading.setVisibility(View.GONE);


        loginBtn.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(LoginPage.this, "Email is required!", Toast.LENGTH_SHORT).show();
                return;
            } else if (password.isEmpty()) {
                Toast.makeText(LoginPage.this, "Password is required!", Toast.LENGTH_SHORT).show();
                return;
            }


            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {

                                usersRef.child(user.getUid()).child("lastLogin").setValue(System.currentTimeMillis());

                                Toast.makeText(LoginPage.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginPage.this, HomePage.class));
                                finish();
                            }
                        } else {
                            Toast.makeText(LoginPage.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });


        forgotPasswordTextView.setOnClickListener(v -> {
            startActivity(new Intent(LoginPage.this, ForgotPassPage.class));
        });


        registerBtn.setOnClickListener(v -> {
            startActivity(new Intent(LoginPage.this, RegisterPage.class));
        });
    }
}
