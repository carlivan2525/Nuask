package com.example.nuaskapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterPage extends AppCompatActivity {

    private EditText fullnameEditText, emailEditText, passwordEditText;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registerpage);

        // Initialize Firebase Auth and Database Reference
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        fullnameEditText = findViewById(R.id.fullname_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        Button registerBtn = findViewById(R.id.register_btn);


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String fullname = fullnameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (fullname.isEmpty()) {
            Toast.makeText(RegisterPage.this, "Full Name is required!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (email.isEmpty()) {
            Toast.makeText(RegisterPage.this, "Email is required!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty() || password.length() < 6) {
            Toast.makeText(RegisterPage.this, "Password must be at least 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }


        Toast.makeText(RegisterPage.this, "Registering...", Toast.LENGTH_SHORT).show();


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterPage.this, "Registration Successful!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(RegisterPage.this, LoginPage.class));
                        finish();

                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            AppUser newUser = new AppUser(fullname, email);


                            usersRef.child(userId).setValue(newUser)
                                    .addOnCompleteListener(task1 -> {
                                        if (!task1.isSuccessful()) {
                                            Toast.makeText(RegisterPage.this, "Failed to save user data!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(RegisterPage.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}