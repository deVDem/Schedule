package ru.devxem.reminder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import ru.devxem.reminder.api.Account;
import ru.devxem.reminder.api.Error;
import ru.devxem.reminder.api.Groups;

public class FirstActivity extends AppCompatActivity {

    TextView hello1;
    RelativeLayout hello;
    Spinner list;
    Button acceptbt;
    EditText eTname;
    EditText eTemail;
    @SuppressLint("StaticFieldLeak")
    static Activity activity;
    @SuppressLint("StaticFieldLeak")
    static Context context;
    static SharedPreferences sharedPreferences;
    Switch swspam;
    Switch swprivacy;
    int object;
    static Dialog dialog_login;
    EditText etLastName;
    EditText etPassword;
    EditText etLogin;
    boolean register = false;
    RelativeLayout loginr;
    RelativeLayout registerr;

    public static void Registered() {
        Toast.makeText(context, "Успешная регистрация. Войдите", Toast.LENGTH_SHORT).show();
        context.startActivity(new Intent(context, SplashScreen.class));
        activity.finish();
    }

    public static void reloadDialog(boolean action) {
        if (action) dialog_login.show();
        else dialog_login.cancel();
    }

    public static void Logined(String response) {
        reloadDialog(false);
        try {
            JSONObject json = new JSONObject(response);
            String login = json.getString("login");
            String email = json.getString("email");
            String name = json.getString("name");
            String groups = json.getString("groups");
            String spam = json.getString("spam");
            String permisson = json.getString("permisson");
            String token = json.getString("token");
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("login", login);
            editor.putString("email", email);
            editor.putString("name", name);
            editor.putString("groups", groups);
            editor.putString("spam", spam);
            editor.putString("permisson", permisson);
            editor.putString("token", token);
            editor.putBoolean("first", false);
            editor.apply();
            context.startActivity(new Intent(activity, MainActivity.class));
            activity.overridePendingTransition(R.anim.transition_out, R.anim.transition_in);
            activity.finish();
        } catch (Exception e) {
            e.printStackTrace();
            Error.setError(context, "1");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
            super.onCreate(savedInstanceState);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setContentView(R.layout.activity_first);
            context = this;
            activity = this;
            eTname = findViewById(R.id.editText2);
            etLastName = findViewById(R.id.editText3);
            etPassword = findViewById(R.id.editText4);
            eTemail = findViewById(R.id.editText);
            etLogin = findViewById(R.id.editText5);
            swspam = findViewById(R.id.switch1);
            swprivacy = findViewById(R.id.switch2);
            hello = findViewById(R.id.hello_r);
            hello1 = findViewById(R.id.hello_t);
            list = findViewById(R.id.spinner);
            acceptbt = findViewById(R.id.button);
            ArrayAdapter<String> adp2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Groups.getGroups(this));
            adp2.add(this.getString(R.string.choose));
            list.setAdapter(adp2);
            object = R.id.hello_t;
            acceptbt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = eTname.getText().toString() + " " + etLastName.getText().toString();
                    String email = eTemail.getText().toString();
                    String group = list.getSelectedItem().toString();
                    String password = etPassword.getText().toString();
                    String spam = String.valueOf(swspam.isChecked());
                    String login = etLogin.getText().toString();
                    String ver = String.valueOf(BuildConfig.VERSION_CODE);
                    if (eTname.getText().toString().length() < 4) {
                        Toast.makeText(FirstActivity.this, R.string.namec, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (eTemail.getText().toString().length() < 10) {
                        Toast.makeText(FirstActivity.this, R.string.emailc, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (list.getSelectedItemPosition() == 0) {
                        Toast.makeText(FirstActivity.this, R.string.groupc, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!swprivacy.isChecked()) {
                        Toast.makeText(FirstActivity.this, R.string.spamc, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    final AlertDialog.Builder builder = new AlertDialog.Builder(FirstActivity.this);
                    builder.setMessage("Пожалуйста, подождите.")
                            .setTitle("Отправка данных..")
                            .setCancelable(false);
                    final Dialog dialog = builder.create();
                    dialog.show();
                    Account.Register(context, login, name, email, password, group, spam, ver);
                }
            });
            mainProcessing();
        } catch (Exception e) {
            Error.setErr(this, e.toString(), getSharedPreferences("settings", Context.MODE_PRIVATE).getString("email", null));
        }
    }

    void ok() {
        switch (object) {
            case R.id.hello_t:
                object = R.id.hello_r;
                mainProcessing();
                break;
            case R.id.hello_r:
                prepareLogin();
                break;
        }
    }

    private void prepareLogin() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loginr = findViewById(R.id.login);
                registerr = findViewById(R.id.data_r);
                final EditText eTlogin = findViewById(R.id.login_et);
                final EditText eTpassword = findViewById(R.id.password_et);
                TextView bTRegister = findViewById(R.id.register_bt);
                Button bTlogin = findViewById(R.id.login_bt);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                dialog_login = builder.setMessage("Пожалуйста, подождите")
                        .setTitle("Выполняется вход..")
                        .setCancelable(false)
                        .create();
                bTlogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String login = eTlogin.getText().toString();
                        String password = eTpassword.getText().toString();
                        if (login.length() < 6 || password.length() < 6) {
                            Toast.makeText(context, "Введите логин и пароль", Toast.LENGTH_SHORT).show();
                        } else {
                            reloadDialog(true);
                            Account.Login(context, login, password, String.valueOf(BuildConfig.VERSION_CODE));
                        }
                    }
                });
                bTRegister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        register = true;
                        registerr.setVisibility(View.VISIBLE);
                        Animation anim_in = AnimationUtils.loadAnimation(context, R.anim.transition_out);
                        anim_in.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                loginr.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        loginr.setAnimation(AnimationUtils.loadAnimation(context, R.anim.transition_in));
                        registerr.setAnimation(anim_in);
                    }
                });
            }
        });
    }

    private void mainProcessing() {
        Thread thread = new Thread(null, doBackgroundThreadProcessing,
                "Background");
        thread.start();
    }

    private Runnable doBackgroundThreadProcessing = new Runnable() {
        public void run() {
            backgroundThreadProcessing();
        }
    };

    private void backgroundThreadProcessing() {
        float b;
        float a;
        float c;
        float i;
        if (object != R.id.hello_r) {
            for (i = 0f; i <= 1000f; i++) {
                a = i / 1000f;
                final float finalA = a;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(object).setAlpha(finalA);
                    }
                });
                if (i % 2 == 0) {
                    try {
                        Thread.sleep(3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (c = 1000f; c > 0f; c--) {
            b = c / 1000f;
            final float finalB = b;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    findViewById(object).setAlpha(finalB);
                }
            });
            if (c % 2 == 0) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        ok();
    }
}
