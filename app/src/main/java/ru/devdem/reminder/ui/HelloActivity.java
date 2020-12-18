package ru.devdem.reminder.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import ru.devdem.reminder.controllers.LessonsController;
import ru.devdem.reminder.R;
import ru.devdem.reminder.ui.group.search.GroupListActivity;

public class HelloActivity extends AppCompatActivity {
    private boolean exit = false;
    private CountDownTimer mExitTimer;
    private SharedPreferences mSettings;
    AlertDialog dialog;


    @Override
    public void onBackPressed() {
        if(dialog!=null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        dialog = new AlertDialog.Builder(this)
                .setMessage(R.string.sure_exit)
                .setTitle(R.string.reg_not_complete)
                .setNegativeButton(R.string.yes, ((dialog1, which) -> {
                    super.onBackPressed();
                }))
                .setPositiveButton(R.string.no, (((dialog1, which) -> {
                    dialog1.cancel();
                })))
                .create();
        mSettings = getSharedPreferences("settings", MODE_PRIVATE);
        LessonsController lessonController = LessonsController.get(this);
        lessonController.removeLessons();
        this.getSharedPreferences("jsondata", Context.MODE_PRIVATE).edit().clear().apply();
        super.onCreate(savedInstanceState);
        View view = View.inflate(this, R.layout.activity_hello, null);
        setContentView(view);
        Button btnJoinGroup = view.findViewById(R.id.btnjoingroup);
        btnJoinGroup.setOnClickListener(v -> {
            startActivity(new Intent(this, GroupListActivity.class));
            overridePendingTransition(R.anim.transition_out, R.anim.transition_in);
            finish();
        });
        Button btnCreateGroup = view.findViewById(R.id.btncreategroup);
        btnCreateGroup.setOnClickListener(v1->{
            startActivity(new Intent(this, CreateGroupActivity.class));
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
