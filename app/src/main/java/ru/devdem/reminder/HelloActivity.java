package ru.devdem.reminder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class HelloActivity extends AppCompatActivity {
    private boolean exit = false;
    private CountDownTimer mExitTimer;
    private SharedPreferences mSettings;
    private LessonsController mLessonController;


    @Override
    public void onBackPressed() {
        if (exit) {
            super.onBackPressed();
            if (mExitTimer != null) mExitTimer.cancel();
        } else {
            Toast.makeText(this, "Click the back button again to exit", Toast.LENGTH_LONG).show();
            exit = true;
            mExitTimer = new CountDownTimer(1500, 1500) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    exit = false;
                }
            }.start();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mSettings = getSharedPreferences("settings", MODE_PRIVATE);
        mLessonController = LessonsController.get(this);
        mLessonController.removeLessons();
        this.getSharedPreferences("jsondata", Context.MODE_PRIVATE).edit().clear().apply();
        super.onCreate(savedInstanceState);
        // TODO: темная тема говно, поменяй
        View view = View.inflate(this, R.layout.activity_hello, null);
        setContentView(view);
        Button btnJoinGroup = view.findViewById(R.id.btnjoingroup);
        btnJoinGroup.setOnClickListener(v -> {
            startActivity(new Intent(this, GroupListActivity.class));
            overridePendingTransition(R.anim.transition_out, R.anim.transition_in);
            finish();
        });
    }

    public void logoff(View view) {
        mSettings.edit().clear().apply();
        mSettings.edit().putBoolean("notification", false).apply();
        this.getSharedPreferences("jsondata", Context.MODE_PRIVATE).edit().clear().apply();
        restart();
    }

    private void restart() {
        finish();
        startActivity(new Intent(this, SplashActivity.class));
        overridePendingTransition(R.anim.transition_out, R.anim.transition_in);
    }
}
