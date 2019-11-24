package ru.devxem.reminder.ui.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import ru.devxem.reminder.BuildConfig;
import ru.devxem.reminder.MainActivity;
import ru.devxem.reminder.R;
import ru.devxem.reminder.SplashScreen;
import ru.devxem.reminder.UpdateNotes;
import ru.devxem.reminder.api.Error;

public class SettingsFragment extends Fragment {

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        final Context context = getContext();
        try {
            // findViewById() делать через root! root.findViewById(...);
            TextView textView = root.findViewById(R.id.textNowVer);
            textView.setText("Версия приложения: " + BuildConfig.VERSION_NAME + "\nКод версии: " + BuildConfig.VERSION_CODE);
            Switch switchs = root.findViewById(R.id.switch_not);
            Switch switch_mills = root.findViewById(R.id.switch_nicht);
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
            switch_mills.setChecked(preferences.getBoolean("nicht", false));
            switch_mills.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("nicht", isChecked);
                    editor.apply();
                    Toast.makeText(context, "Перезапуск..", Toast.LENGTH_LONG).show();
                    Activity activity = getActivity();
                    context.startActivity(new Intent(context, SplashScreen.class));
                    if (activity != null) {
                        activity.overridePendingTransition(R.anim.transition_out, R.anim.transition_in);
                        context.stopService(new Intent(context, UpdateNotes.class));
                        activity.finish();
                    }
                }
            });
            Button bt_cache = root.findViewById(R.id.clear_cache);
            bt_cache.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean nicht = preferences.getBoolean("nicht", false);
                    boolean notif = preferences.getBoolean("notification", true);
                    String group = MainActivity.getSss().get(1);
                    String id = MainActivity.getSss().get(0);
                    String email = preferences.getString("email", "null");
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.putBoolean("first", false);
                    editor.putBoolean("nicht", nicht);
                    editor.putBoolean("notif", notif);
                    String login = preferences.getString("login", null);
                    String name = preferences.getString("name", null);
                    String groups = preferences.getString("groups", null);
                    String spam = preferences.getString("spam", null);
                    String permisson = preferences.getString("permisson", null);
                    String token = preferences.getString("token", null);
                    editor.putString("login", login);
                    editor.putString("email", email);
                    editor.putString("name", name);
                    editor.putString("groups", groups);
                    editor.putString("spam", spam);
                    editor.putString("permisson", permisson);
                    editor.putString("token", token);
                    editor.putBoolean("first", false);
                    editor.apply();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Error.setErr(context, e.toString(), Objects.requireNonNull(context).getSharedPreferences("settings", Context.MODE_PRIVATE).getString("email", null));
        }
        return root;
    }
}