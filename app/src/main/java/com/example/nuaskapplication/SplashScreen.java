package com.example.nuaskapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    private ImageView splashLogo;
    private ProgressBar loadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        splashLogo = findViewById(R.id.splash_logo);
        loadingProgress = findViewById(R.id.loading_progress);
        loadingProgress.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, LoginPage.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}
