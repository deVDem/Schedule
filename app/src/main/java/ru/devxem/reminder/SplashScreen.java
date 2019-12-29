package ru.devxem.reminder;

import android.app.Dialog;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SplashScreen extends AppCompatActivity {
    SharedPreferences settings;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        settings = getSharedPreferences("settings", Context.MODE_PRIVATE);
        if (settings.getBoolean("nicht", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_splash);
        Context context = this;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final Dialog dialog = builder.setMessage("Каким-то чудом вы получили тестовую версию приложения. В нём отключена реклама и могут быть ошибки в коде.")
                .setTitle("Тестовая версия приложения")
                .setNegativeButton(context.getString(R.string.Exit), (dialogInterface, i) -> System.exit(0))
                .setPositiveButton("Продолжить", (dialog1, which) -> start())
                .setCancelable(false)
                .create();
        anim_count.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (BuildConfig.DEBUG) {
                    dialog.show();
                } else {
                    start();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
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
