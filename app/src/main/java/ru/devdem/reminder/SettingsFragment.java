package ru.devdem.reminder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;

public class SettingsFragment extends Fragment {

    private SharedPreferences mSettings;
    private boolean can = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_settings, null);
        Context context = Objects.requireNonNull(getContext());
        String NAME_PREFS = "settings";
        mSettings = context.getSharedPreferences(NAME_PREFS, Context.MODE_PRIVATE);
        Switch switchNight = view.findViewById(R.id.switchNightTheme);
        Switch switchNotification = view.findViewById(R.id.switchNotification);
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_checked},
                new int[]{-android.R.attr.state_checked}
        };
        int[] colors = new int[]{
                getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.white)
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            switchNight.setThumbTintList(new ColorStateList(states, colors));
            switchNotification.setThumbTintList(new ColorStateList(states, colors));
        }
        switchNight.setChecked(mSettings.getBoolean("night", false));
        switchNotification.setChecked(mSettings.getBoolean("notification", true));
        switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (can)
                mSettings.edit().putBoolean("notification", isChecked).apply();
            if (mSettings.getBoolean("notification", true))
                Objects.requireNonNull(getActivity()).startService(new Intent(getContext(), NotificationService.class));
            else {
                Objects.requireNonNull(getActivity()).stopService(new Intent(getContext(), NotificationService.class));
            }
        });
        switchNight.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (can) {
                mSettings.edit().putBoolean("night", isChecked).apply();
                restart();
            }
        });
        Button mLogOffButton = view.findViewById(R.id.buttonLogOff);
        mLogOffButton.setOnClickListener(v -> {
            can = false;
            mSettings.edit().clear().apply();
            context.getSharedPreferences("jsondata", Context.MODE_PRIVATE).edit().clear().apply();
            restart();
        });
        return view;
    }

    private void restart() {
        Activity activity = Objects.requireNonNull(getActivity());
        activity.stopService(new Intent(getContext(), NotificationService.class));
        activity.finish();
        activity.startActivity(new Intent(activity, SplashActivity.class));
        activity.overridePendingTransition(R.anim.transition_out, R.anim.transition_in);
    }
}
