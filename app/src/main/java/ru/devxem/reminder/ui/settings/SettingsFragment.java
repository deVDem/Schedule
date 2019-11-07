package ru.devxem.reminder.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import ru.devxem.reminder.MainActivity;
import ru.devxem.reminder.R;

public class SettingsFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        // findViewById() делать через root! root.findViewById(...);
        Switch switchs = root.findViewById(R.id.switch_not);
        Switch switch_mills = root.findViewById(R.id.switch_millis);
        Context context = getContext();
        final SharedPreferences preferences = Objects.requireNonNull(context).getSharedPreferences("settings", Context.MODE_PRIVATE);
        switchs.setChecked(preferences.getBoolean("notification", true));
        switchs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("notification", isChecked);
                editor.apply();
            }
        });
        switch_mills.setChecked(preferences.getBoolean("millis", false));
        switch_mills.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("millis", isChecked);
                editor.apply();
            }
        });
        Button bt_cache = root.findViewById(R.id.clear_cache);
        bt_cache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean millis = preferences.getBoolean("millis", false);
                boolean notif = preferences.getBoolean("notification", true);
                String group = MainActivity.getSss().get(1);
                String id = MainActivity.getSss().get(0);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.putBoolean("first", false);
                editor.putBoolean("millis", millis);
                editor.putBoolean("notif", notif);
                editor.putString("group", group);
                editor.putString("id", id);
                editor.apply();
            }
        });
        return root;
    }
}