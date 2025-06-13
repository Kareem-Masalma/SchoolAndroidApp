package com.example.schoolapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private ImageView logoImage;
    private TextView titleText, subtitleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        logoImage = findViewById(R.id.logoImage);
        titleText = findViewById(R.id.splashText);
        subtitleText = findViewById(R.id.splashSubtitle);

        Animation logoAnim = AnimationUtils.loadAnimation(this, R.anim.splash_logo_anim);
        Animation textAnim = AnimationUtils.loadAnimation(this, R.anim.splash_text_anim);

        logoImage.startAnimation(logoAnim);

        new Handler().postDelayed(() -> {
            titleText.startAnimation(textAnim);
            subtitleText.startAnimation(textAnim);
        }, 20);


        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, Login.class));
            finish();
        }, 3000);
    }


}
