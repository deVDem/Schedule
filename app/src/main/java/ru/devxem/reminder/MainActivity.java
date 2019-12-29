package ru.devxem.reminder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ru.devxem.reminder.api.Error;

public class MainActivity extends AppCompatActivity {


    private static final String DIALOG = "DialogNewYear2020";

    public static List<String> sss = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    static Snackbar snackbar;

    public static void onSnack() {
        snackbar.show();
    }

    public static List<String> getSss() {
        return sss;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            final SharedPreferences settings = getSharedPreferences("settings", Context.MODE_PRIVATE);
            String id = "5";
            String group = settings.getString("groups", null);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setTheme(R.style.AppTheme_NoActionBar);
            FragmentManager manager = getSupportFragmentManager();
            DialogNewYear dialog = DialogNewYear.newInstance();
            dialog.show(Objects.requireNonNull(manager), DIALOG);
            /*setContentView(R.layout.activity_main);
            Context context = this;
            View view_s = findViewById(R.id.nav_host_fragment);
            snackbar = Snackbar.make(view_s, "Данные в оффлайн режиме. Потяните вниз для обновления.", Snackbar.LENGTH_LONG);
            if (settings.getString("email", "null").equals("null")) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = this.getLayoutInflater();
                @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.email_confirm, null);
                final EditText emaileT = view.findViewById(R.id.editText3);
                Button confirmbt = view.findViewById(R.id.button2);
                builder.setView(view);
                final Dialog dialog = builder.create();
                dialog.show();
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
                            Toast.makeText(MainActivity.this, "Спасибо", Toast.LENGTH_LONG).show();
                            dialog.cancel();
                        }
                    }
                });
            }
            FragmentTransaction transaction;
            transaction = getFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            transaction.commit();
            BottomNavigationView navView = findViewById(R.id.nav_view);
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_settings)
                    .build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(navView, navController);
            startService(new Intent(this, UpdateNotes.class));
            sss.add(0, id);
            sss.add(1, group); */
        } catch (Exception e) {
            e.printStackTrace();
            Error.setErr(this, e.toString(), getSharedPreferences("settings", Context.MODE_PRIVATE).getString("email", null));
        }
    }
}
