package ru.devxem.reminder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import ru.devxem.reminder.api.Error;

public class MainActivity extends AppCompatActivity {
    public static List<String> sss = new ArrayList<>();


    public static List<String> getSss() { return sss;}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            final SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
            String id = "5";
            String group = settings.getString("group", null);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setContentView(R.layout.activity_main);
            BottomNavigationView navView = findViewById(R.id.nav_view);
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                    .build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(navView, navController);
            if (settings.getString("email", null) == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater inflater = this.getLayoutInflater();
                @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.email_confirm, null);
                builder.setView(view);
                final EditText emaileT = view.findViewById(R.id.editText3);
                Button confirmbt = view.findViewById(R.id.button2);
                confirmbt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String email = emaileT.getText().toString();
                        if (email.length() <= 9) {
                            Toast.makeText(MainActivity.this, "Введите Email", Toast.LENGTH_LONG).show();
                        } else {
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("email", email);
                            editor.apply();
                        }
                    }
                });
                builder.create();
            }
            sss.add(0, id);
            sss.add(1, group);
        } catch (Exception e) {
            Error.setErr(this, e.toString(), getSharedPreferences("settings", Context.MODE_PRIVATE).getString("email", null));
        }
    }
}
