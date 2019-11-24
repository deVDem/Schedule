package ru.devxem.reminder;

import android.content.Context;
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

public class SplashScreen extends AppCompatActivity {
    private SharedPreferences settings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        settings = getSharedPreferences("settings", Context.MODE_PRIVATE);
        if (settings.getBoolean("nicht", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        //setTheme(R.style.AppTheme_NoActionBar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_splash);
        TextView appname = findViewById(R.id.text_appname);
        appname.setAnimation(AnimationUtils.loadAnimation(this, R.anim.text));
        Animation anim_count = AnimationUtils.loadAnimation(this, R.anim.count);
        ImageView button = findViewById(R.id.img_button);
        button.setAnimation(AnimationUtils.loadAnimation(this, R.anim.button));
        ImageView count = findViewById(R.id.img_count);
        count.setAnimation(anim_count);
        ImageView arrow = findViewById(R.id.img_arrow);
        arrow.setAnimation(AnimationUtils.loadAnimation(this, R.anim.arrow));
        ImageView circle = findViewById(R.id.img_circle);
        circle.setAnimation(AnimationUtils.loadAnimation(this, R.anim.circle));
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
        if (settings.getBoolean("fix", true)) {
            SharedPreferences.Editor editor = settings.edit();
            editor.remove("id");
            editor.putBoolean("fix", false);
            editor.apply();
        }
    }

    private void start() {
        if (settings.getBoolean("first", true)) {
            startActivity(new Intent(SplashScreen.this, FirstActivity.class));
            overridePendingTransition(R.anim.transition_out, R.anim.transition_in);
            finish();

        } else {
            startActivity(new Intent(SplashScreen.this, MainActivity.class));
            overridePendingTransition(R.anim.transition_out, R.anim.transition_in);
            finish();
        }
    }
}
