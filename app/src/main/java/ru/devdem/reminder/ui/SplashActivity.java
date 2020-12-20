package ru.devdem.reminder.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import ru.devdem.reminder.BuildConfig;
import ru.devdem.reminder.R;
import ru.devdem.reminder.ui.main.MainActivity;

public class SplashActivity extends AppCompatActivity {

    private static final String FILE_PREFS_NAME = "settings";
    private static final String PREFS_NIGHT = "night";
    private static final String PREFS_FIRST = "first";
    private static final String PREFS_SYSTEM_THEME = "system_theme";

    private SharedPreferences mSettings;

    @SuppressLint({"SourceLockedOrientationActivity", "SetTextI18n"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mSettings = getSharedPreferences(FILE_PREFS_NAME, MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        if (mSettings.getBoolean(PREFS_SYSTEM_THEME, true)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else {
            if (mSettings.getBoolean(PREFS_NIGHT, false))
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_splash);
        if (BuildConfig.DEBUG) {
            TextView mSplashText = findViewById(R.id.splashMotd);
            mSplashText.setText("debug version" + BuildConfig.VERSION_CODE);
        }
        ImageView arrow = findViewById(R.id.arrow);
        ImageView button = findViewById(R.id.button);
        ImageView count = findViewById(R.id.count);
        ImageView circle = findViewById(R.id.circle);
        View textView = findViewById(R.id.textViewAppName);
        Animation anim_text = AnimationUtils.loadAnimation(this, R.anim.text);
        Animation anim_arrow = AnimationUtils.loadAnimation(this, R.anim.arrow);
        Animation anim_button = AnimationUtils.loadAnimation(this, R.anim.button);
        Animation anim_count = AnimationUtils.loadAnimation(this, R.anim.count);
        anim_count.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                StartApp();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        Animation anim_circle = AnimationUtils.loadAnimation(this, R.anim.circle);
        textView.setAnimation(anim_text);
        arrow.setAnimation(anim_arrow);
        button.setAnimation(anim_button);
        count.setAnimation(anim_count);
        circle.setAnimation(anim_circle);
    }

    private void StartApp() {
        if (mSettings.getBoolean(PREFS_FIRST, true)) {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        } else {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        }
        overridePendingTransition(R.anim.transition_out, R.anim.transition_in);
        finish();
    }
}
