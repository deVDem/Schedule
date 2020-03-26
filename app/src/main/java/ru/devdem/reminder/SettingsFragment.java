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
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
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
        if (BuildConfig.DEBUG) {
            Button button = new Button(context);
            button.setText("Reset notifications");
            button.setBackground(getResources().getDrawable(R.drawable.box_shape));
            button.setBackgroundColor(getResources().getColor(R.color.card_color_lessons));
            button.setOnClickListener(v -> {
                mSettings.edit().putInt("notifications_all_service", 0).apply();
            });
            LinearLayout ll = view.findViewById(R.id.llnSettings);
            ll.addView(button);
        }
        Switch switchTheme = view.findViewById(R.id.switchTheme);
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
        switchTheme.setChecked(mSettings.getBoolean("system_theme", true));
        switchNight.setChecked(mSettings.getBoolean("night", false));
        switchNotification.setChecked(mSettings.getBoolean("notification", true));
        if (switchTheme.isChecked()) {
            switchNight.setEnabled(false);
            switchNight.setAlpha(0.5f);
        }
        switchTheme.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if (can) {
                mSettings.edit().putBoolean("system_theme", isChecked).apply();
                switchNight.setEnabled(!isChecked);
                switchNight.setAlpha(isChecked ? 0.5f : 1f);
                switchNight.setChecked(mSettings.getBoolean("night", false));
                AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM : switchNight.isChecked() ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
                restart();
            }
        }));
        switchNight.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (can) {
                mSettings.edit().putBoolean("night", isChecked).apply();
                AppCompatDelegate.setDefaultNightMode(switchTheme.isChecked() ? AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM : isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
                restart();
            }
        });
        switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (can)
                mSettings.edit().putBoolean("notification", isChecked).apply();
            if (mSettings.getBoolean("notification", true)) {
                Objects.requireNonNull(getActivity()).startService(new Intent(getContext(), NotificationService.class));
            } else {
                Objects.requireNonNull(getActivity()).stopService(new Intent(getContext(), NotificationService.class));
            }
        });
        Button mLogOffButton = view.findViewById(R.id.buttonLogOff);
        mLogOffButton.setOnClickListener(v -> {
            can = false;
            mSettings.edit().clear().apply();
            mSettings.edit().putBoolean("notification", false).apply();
            context.getSharedPreferences("jsondata", Context.MODE_PRIVATE).edit().clear().apply();
            Objects.requireNonNull(getActivity()).stopService(new Intent(getContext(), NotificationService.class));
            restart();
        });
        TextView versionInfo = view.findViewById(R.id.versionInfo);
        String verInf = BuildConfig.BUILD_TYPE + " | v: " + BuildConfig.VERSION_CODE + " name: " + BuildConfig.VERSION_NAME;
        versionInfo.setText(verInf);
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
