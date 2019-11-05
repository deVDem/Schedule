package ru.devxem.reminder.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import ru.devxem.reminder.R;

public class SettingsFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        // findViewById() делать через root! root.findViewById(...);
        Switch switchs = root.findViewById(R.id.switch_not);
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
        return root;
    }
}