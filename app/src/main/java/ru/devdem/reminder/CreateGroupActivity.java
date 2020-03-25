package ru.devdem.reminder;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class CreateGroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = View.inflate(this, R.layout.activity_create_group, null);
        setContentView(v);
        Toolbar toolbar = v.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Название группы");
        toolbar.setSubtitle("Название строения группы");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.white));
    }
}
