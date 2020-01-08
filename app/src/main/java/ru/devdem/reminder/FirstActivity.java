package ru.devdem.reminder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

public class FirstActivity extends AppCompatActivity {

    RelativeLayout helloRl;
    RelativeLayout loginRl;
    RelativeLayout registerRl;
    TextView loginTVNotReg;
    Button mLoginButton;
    Button mRegisterButton;
    EditText mLLoginEt;
    EditText mLPasswordEt;
    Context mContext;
    Spinner mSpinner;
    SharedPreferences mSettings;

    EditText mRLoginEt;
    EditText mRNameEt;
    EditText mREmailEt;
    EditText mRPassEt;
    EditText mRConPassEt;
    CheckBox mRCheckSpam;

    private int ANIM_DURATION = 500;
    private String PREFS_FIRST = "first";

    @Override
    public void onBackPressed() {
        if (registerRl.getVisibility() == View.VISIBLE) {
            YoYo.with(Techniques.TakingOff).duration(ANIM_DURATION).interpolate(new AccelerateDecelerateInterpolator()).onEnd(animator -> {
                registerRl.setVisibility(View.INVISIBLE);
                loginRl.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.Landing).duration(ANIM_DURATION).interpolate(new AccelerateDecelerateInterpolator()).playOn(loginRl);
            }).playOn(registerRl);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        String NAME_PREFS = "settings";
        mSettings = getSharedPreferences(NAME_PREFS, Context.MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        helloRl = findViewById(R.id.hello);
        loginRl = findViewById(R.id.relativeLayoutLogin);
        registerRl = findViewById(R.id.relativeLayoutRegister);
        loginTVNotReg = findViewById(R.id.loginTVNotReg);
        loginTVNotReg.setOnClickListener(this::onClickNotReg);
        findViewById(R.id.firstBtNext).setOnClickListener(this::onClickNext);
        mLoginButton = findViewById(R.id.loginBtn);
        mLLoginEt = findViewById(R.id.loginETLogin);
        mLPasswordEt = findViewById(R.id.loginETPassword);

        mSpinner = findViewById(R.id.registerSpGroups);
        mRLoginEt = findViewById(R.id.registerEtLogin);
        mRNameEt = findViewById(R.id.registerEtName);
        mREmailEt = findViewById(R.id.registerEtEmail);
        mRPassEt = findViewById(R.id.registerEtPassword);
        mRConPassEt = findViewById(R.id.registerEtConfirm);
        mRCheckSpam = findViewById(R.id.registerChBxSpam);
        mRegisterButton = findViewById(R.id.registerBtnRegister);

        mContext = this;
        LoginFuncs();
        RegisterFuncs();
    }

    private void RegisterFuncs() {
        NetworkController.GetGroups(mContext, mSpinner);
        mRegisterButton.setOnClickListener(v -> {
            String login = mRLoginEt.getText().toString();
            String name = mRNameEt.getText().toString();
            String email = mREmailEt.getText().toString();
            String password = mRPassEt.getText().toString();
            String confirmPassword = mRConPassEt.getText().toString();
            int group_id = mSpinner.getSelectedItemPosition();
            String spam;
            if (mRCheckSpam.isChecked()) spam = "1";
            else spam = "0";
            if (login.length() > 5 && name.length() > 5 && email.length() > 8 && password.length() > 5 && password.equals(confirmPassword) && group_id != 0) {
                Response.Listener<String> listener = response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean ok = jsonResponse.getBoolean("ok");
                        if (ok) {
                            boolean password_ok = jsonResponse.getBoolean("not_registed");
                            if (password_ok) {
                                try {
                                    JSONObject jsonUserInfo = jsonResponse.getJSONObject("user_info");
                                    int user_id = jsonUserInfo.getInt("id");
                                    String name1 = jsonUserInfo.getString("name");
                                    String email1 = jsonUserInfo.getString("email");
                                    String login1 = jsonUserInfo.getString("login");
                                    String group = jsonUserInfo.getString("groups");
                                    boolean spam1 = jsonUserInfo.getString("spam").equals("1");
                                    int permission = jsonUserInfo.getInt("permission");
                                    String token = jsonUserInfo.getString("token");
                                    SharedPreferences.Editor editor = mSettings.edit();
                                    editor.putInt("user_id", user_id);
                                    editor.putString("name", name1);
                                    editor.putString("email", email1);
                                    editor.putString("login", login1);
                                    editor.putString("group", group);
                                    editor.putBoolean("spam", spam1);
                                    editor.putInt("permission", permission);
                                    editor.putString("token", token);
                                    editor.putBoolean(PREFS_FIRST, false);
                                    editor.apply();
                                    Toast.makeText(mContext, "Успешная регистрация.", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(FirstActivity.this, MainActivity.class));
                                    overridePendingTransition(R.anim.transition_out, R.anim.transition_in);
                                    finish();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(mContext, "Неудалось получить информацию о пользователе.", Toast.LENGTH_SHORT).show();
                                }
                            } else
                                Toast.makeText(mContext, "Такой пользователь уже зарегистрирован", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(mContext, "Произошла неизвестная ошибка", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(mContext, "Произошла неизвестная ошибка", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                };
                NetworkController.Register(mContext, login, name, email, password, String.valueOf(group_id), spam, listener);
            } else
                Snackbar.make(registerRl, "Укажите все данные верно", Snackbar.LENGTH_LONG).show();
        });
    }

    private void LoginFuncs() {
        mLoginButton.setOnClickListener(v -> {
            String login = mLLoginEt.getText().toString();
            String password = mLPasswordEt.getText().toString();
            if (login.length() >= 6 || password.length() >= 6) {
                Response.Listener<String> listener = response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean ok = jsonResponse.getBoolean("ok");
                        if (ok) {
                            boolean password_ok = jsonResponse.getBoolean("password_ok");
                            if (password_ok) {
                                try {
                                    JSONObject jsonUserInfo = jsonResponse.getJSONObject("user_info");
                                    int user_id = jsonUserInfo.getInt("id");
                                    String name = jsonUserInfo.getString("name");
                                    String email = jsonUserInfo.getString("email");
                                    String login1 = jsonUserInfo.getString("login");
                                    String group = jsonUserInfo.getString("groups");
                                    boolean spam = jsonUserInfo.getString("spam").equals("1");
                                    int permission = jsonUserInfo.getInt("permission");
                                    String token = jsonUserInfo.getString("token");
                                    SharedPreferences.Editor editor = mSettings.edit();
                                    editor.putInt("user_id", user_id);
                                    editor.putString("name", name);
                                    editor.putString("email", email);
                                    editor.putString("login", login1);
                                    editor.putString("group", group);
                                    editor.putBoolean("spam", spam);
                                    editor.putInt("permission", permission);
                                    editor.putString("token", token);
                                    editor.putBoolean(PREFS_FIRST, false);
                                    editor.apply();
                                    Toast.makeText(mContext, "Успешный вход.", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(FirstActivity.this, MainActivity.class));
                                    overridePendingTransition(R.anim.transition_out, R.anim.transition_in);
                                    finish();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(mContext, "Неудалось получить информацию о пользователе.", Toast.LENGTH_SHORT).show();
                                }
                            } else
                                Toast.makeText(mContext, "Wrong password.", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(mContext, "Wrong login or email.", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(mContext, "Произошла неизвестная ошибка", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                };
                NetworkController.Login(mContext, login, password, listener);
            } else {
                Snackbar.make(loginRl, "Введите логин и\\или пароль.", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void onClickNotReg(View v) {
        YoYo.with(Techniques.TakingOff).duration(ANIM_DURATION)
                .interpolate(new AccelerateDecelerateInterpolator())
                .onEnd(animator -> {
                    loginRl.setVisibility(View.INVISIBLE);
                    registerRl.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.Landing).duration(ANIM_DURATION).interpolate(new AccelerateDecelerateInterpolator()).playOn(registerRl);
                }).playOn(loginRl);
    }

    public void onClickNext(View v) {
        int cx = helloRl.getWidth() / 2;
        int cy = helloRl.getHeight() / 2;
        float radius = helloRl.getWidth();
        Animator anim = ViewAnimationUtils.createCircularReveal(helloRl, cx, cy, radius, 0);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                helloRl.setVisibility(View.INVISIBLE);
                new CountDownTimer(600, 100) {

                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        loginRl.setVisibility(View.VISIBLE);
                    }
                }.start();
                YoYo.with(Techniques.Landing).duration(ANIM_DURATION).interpolate(new AccelerateDecelerateInterpolator())
                        .delay(500)
                        .playOn(loginRl);
            }
        });
        anim.start();
    }
}
