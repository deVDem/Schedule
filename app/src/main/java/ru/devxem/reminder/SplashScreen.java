package ru.devxem.reminder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import ru.devxem.reminder.api.Error;

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        try {
            SharedPreferences settings = getSharedPreferences("settings", Context.MODE_PRIVATE);
            if (settings.getBoolean("nicht", false)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            super.onCreate(savedInstanceState);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            if (settings.getBoolean("fix", true)) {
                SharedPreferences.Editor editor = settings.edit();
                editor.remove("id");
                editor.putBoolean("fix", false);
                editor.apply();
            }
            if (settings.getBoolean("first", true)) {
                startActivity(new Intent(SplashScreen.this, FirstActivity.class));

            } else {
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
            }
            finish();
        } catch (Exception e) {
            Error.setErr(this, e.toString(), getSharedPreferences("settings", Context.MODE_PRIVATE).getString("email", null));
        }
    }
}
