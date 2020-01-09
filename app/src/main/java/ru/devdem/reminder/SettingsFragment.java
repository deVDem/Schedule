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
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;

public class SettingsFragment extends Fragment {

    private Switch mSwitchNight;
    private Switch mSwitchNotification;
    private SharedPreferences mSettings;
    private String NAME_PREFS = "settings";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_settings, null);
        Context context = Objects.requireNonNull(getContext());
        mSettings = context.getSharedPreferences(NAME_PREFS, Context.MODE_PRIVATE);
        mSwitchNight = view.findViewById(R.id.switchNightTheme);
        mSwitchNotification = view.findViewById(R.id.switchNotification);
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_checked},
                new int[]{-android.R.attr.state_checked}
        };
        int[] colors = new int[]{
                getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.white)
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mSwitchNight.setThumbTintList(new ColorStateList(states, colors));
            mSwitchNotification.setThumbTintList(new ColorStateList(states, colors));
        }
        mSwitchNight.setChecked(mSettings.getBoolean("night", false));
        mSwitchNotification.setChecked(mSettings.getBoolean("notification", true));
        mSwitchNight.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mSettings.edit().putBoolean("night", isChecked).apply();
            Activity activity = Objects.requireNonNull(getActivity());
            activity.startActivity(new Intent(activity, SplashActivity.class));
            activity.overridePendingTransition(R.anim.transition_out, R.anim.transition_in);
            activity.finish();
        });
        return view;
    }
}