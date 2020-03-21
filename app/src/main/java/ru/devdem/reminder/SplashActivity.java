package ru.devdem.reminder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences mSettings;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        String NAME_PREFS = "settings";
        mSettings = getSharedPreferences(NAME_PREFS, MODE_PRIVATE);
        String PREFS_NIGHT = "night";
        super.onCreate(savedInstanceState);
        if (mSettings.getBoolean(PREFS_NIGHT, false))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_splash);
        ImageView arrow = findViewById(R.id.arrow);
        ImageView button = findViewById(R.id.button);
        ImageView count = findViewById(R.id.count);
        ImageView circle = findViewById(R.id.circle);
        TextView textView = findViewById(R.id.textViewAppName);
        Animation anim_text = AnimationUtils.loadAnimation(this, R.anim.text);
        textView.setAnimation(anim_text);
        Animation anim_arrow = AnimationUtils.loadAnimation(this, R.anim.arrow);
        arrow.setAnimation(anim_arrow);
        Animation anim_button = AnimationUtils.loadAnimation(this, R.anim.button);
        button.setAnimation(anim_button);
        Animation anim_count = AnimationUtils.loadAnimation(this, R.anim.count);
        count.setAnimation(anim_count);
        anim_count.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        Animation anim_circle = AnimationUtils.loadAnimation(this, R.anim.circle);
        circle.setAnimation(anim_circle);
    }

    private void start() {
        String PREFS_FIRST = "first";
        if (mSettings.getBoolean(PREFS_FIRST, true)) {
            startActivity(new Intent(SplashActivity.this, FirstActivity.class));
            overridePendingTransition(R.anim.transition_out, R.anim.transition_in);
            finish();
        } else {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            overridePendingTransition(R.anim.transition_out, R.anim.transition_in);
            finish();
        }
    }
}
