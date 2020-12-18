package ru.devdem.reminder.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.snackbar.Snackbar;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONObject;

import java.util.Objects;
import java.util.Random;

import ru.devdem.reminder.BuildConfig;
import ru.devdem.reminder.R;
import ru.devdem.reminder.controllers.NetworkController;

public class FirstActivity extends AppCompatActivity {

    private RelativeLayout helloRl;
    private RelativeLayout loginRl;
    private RelativeLayout registerRl;
    private static String nameRegex = "(^[A-Z]{1}[a-z]{1,30} [A-Z]{1}[a-z]{1,30}$)|(^[А-Я]{1}[а-я]{1,30} [А-Я]{1}[а-я]{1,30}$)";
    private static String emailRegex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    private static String loginRegex = "[A-Za-z0-9_]{4,255}";
    private TextView mLTextView;
    private Button mLoginButton;
    private TextView mLoginTVNotReg;

    private Context mContext;
    private SharedPreferences mSettings;

    private NetworkController mNetworkController;
    private MaterialEditText mLLoginEt;
    private MaterialEditText mREmailEt;
    private MaterialEditText mLPasswordEt;
    private MaterialEditText mRLoginEt;
    private MaterialCheckBox mRCheckSpam;
    private TextView mRTextView;
    private Button mRegisterButton;
    private MaterialEditText mRNameEt;
    private MaterialEditText mRPassEt;
    private MaterialEditText mRConPassEt;

    private int ANIM_DURATION = 700;
    private String PREFS_FIRST = "first";
    private static final String TAG = "FirstActivity";

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
        mNetworkController = NetworkController.get();
        helloRl = findViewById(R.id.hello);
        loginRl = findViewById(R.id.relativeLayoutLogin);
        registerRl = findViewById(R.id.relativeLayoutRegister);
        mLoginTVNotReg = findViewById(R.id.loginTVNotReg);
        mLoginTVNotReg.setOnClickListener(v1 -> onClickNotReg());
        findViewById(R.id.firstBtNext).setOnClickListener(v1 -> onClickNext());
        mLoginButton = findViewById(R.id.loginBtn);
        mLLoginEt = findViewById(R.id.loginETLogin);
        mLPasswordEt = findViewById(R.id.loginETPassword);
        mLPasswordEt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                LoginFuncs();
            }
            return false;
        });
        mLTextView = findViewById(R.id.textViewLogin);
        mRLoginEt = findViewById(R.id.registerEtLogin);
        mRNameEt = findViewById(R.id.registerEtName);
        mREmailEt = findViewById(R.id.registerEtEmail);
        mRPassEt = findViewById(R.id.registerEtPassword);
        mRConPassEt = findViewById(R.id.registerEtConfirm);
        mRCheckSpam = findViewById(R.id.registerChBxSpam);
        mRegisterButton = findViewById(R.id.registerBtnRegister);
        mRTextView = findViewById(R.id.textViewRegister);

        mContext = this;
        mLoginButton.setOnClickListener(v -> LoginFuncs());
        mRegisterButton.setOnClickListener(v -> RegisterFuncs());
    }

    private void controlViews(boolean enable) {
        mRLoginEt.setEnabled(enable);
        mRNameEt.setEnabled(enable);
        mRCheckSpam.setEnabled(enable);
        mRConPassEt.setEnabled(enable);
        mREmailEt.setEnabled(enable);
        mRLoginEt.setEnabled(enable);
        mRPassEt.setEnabled(enable);
        mLLoginEt.setEnabled(enable);
        mLPasswordEt.setEnabled(enable);
        mLoginButton.setEnabled(enable);
        mRegisterButton.setEnabled(enable);
        mLoginTVNotReg.setEnabled(enable);
    }

    private void RegisterFuncs() {
        controlViews(false);
        String login;
        String name;
        String email;
        String password;
        String confirmPassword;
        String spam;
        if(!BuildConfig.DEBUG) {
            login = Objects.requireNonNull(mRLoginEt.getText()).toString();
            name = Objects.requireNonNull(mRNameEt.getText()).toString();
            email = Objects.requireNonNull(mREmailEt.getText()).toString();
            password = Objects.requireNonNull(mRPassEt.getText()).toString();
            confirmPassword = Objects.requireNonNull(mRConPassEt.getText()).toString();
            spam = mRCheckSpam.isChecked() ? "Yes" : "No";
        } else {
            login = "debug"+new Random().nextInt(1000);
            name = "Debug"+" "+"Debugovich";
            email = "debug"+new Random().nextInt(1000)+"@devdem.ru";
            password = "testpassfordebug";
            confirmPassword = password;
            spam = new Random().nextBoolean() ? "Yes" : "No";
            mRLoginEt.setText(login);
            mRNameEt.setText(name);
            mREmailEt.setText(email);
            mRPassEt.setText(password);
            mRConPassEt.setText(confirmPassword);
            mRCheckSpam.setChecked(spam.equals("Yes"));
            mLLoginEt.setText(login);
            mLPasswordEt.setText(password);
        }
        if (mRLoginEt.validate(loginRegex, getString(R.string.login_must_be)) && mRNameEt.validate(nameRegex, getString(R.string.type_first_and_last_name)) && mREmailEt.validate(emailRegex, getText(R.string.enter_the_correct_address)) && password.length() > 5 && password.equals(confirmPassword)) {
            Response.Listener<String> listener = response -> {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse.isNull("error") && !jsonResponse.isNull("response")) {
                        try {
                            JSONObject jsonResponseAll = jsonResponse.getJSONObject("response");
                            JSONObject jsonUserInfo = jsonResponseAll.getJSONObject("user_data");
                            int user_id = jsonUserInfo.getInt("id");
                            String name1 = jsonUserInfo.getString("names");
                            String email1 = jsonUserInfo.getString("email");
                            String login1 = jsonUserInfo.getString("login");
                            String group = jsonUserInfo.getString("groupId");
                            boolean spam1 = jsonUserInfo.getString("spam").equals("Yes");
                            int permission = jsonUserInfo.isNull("permission") ? 0 : jsonUserInfo.getInt("permission");
                            String token = jsonUserInfo.getString("token");
                            String password_hash = jsonUserInfo.getString("password");
                            mSettings.edit().clear().apply();
                            SharedPreferences.Editor editor = mSettings.edit();
                            editor.putInt("user_id", user_id);
                            editor.putString("name", name1);
                            editor.putString("email", email1);
                            editor.putString("login", login1);
                            editor.putString("group", group);
                            editor.putBoolean("spam", spam1);
                            editor.putInt("permission", permission);
                            editor.putString("token", token);
                            editor.putString("password", password_hash);
                            editor.putBoolean(PREFS_FIRST, false);
                            editor.apply();
                            Toast.makeText(mContext, R.string.success_registration, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(FirstActivity.this, SplashActivity.class));
                            overridePendingTransition(R.anim.transition_out, R.anim.transition_in);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                            controlViews(true);
                            Toast.makeText(mContext, R.string.failed_get_user_info, Toast.LENGTH_SHORT).show();
                            showHide(mRTextView, registerRl, false);
                        }
                    } else {
                        JSONObject errorJson = jsonResponse.getJSONObject("error");
                        Toast.makeText(mContext, errorJson.getInt("code")+" "+errorJson.getString("text"), Toast.LENGTH_SHORT).show();
                        showHide(mRTextView, registerRl, false);
                        controlViews(true);
                        Log.e(TAG, "RegisterFuncs: Error "+errorJson.getInt("code"), new Exception());
                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, R.string.unknown_error, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    showHide(mRTextView, registerRl, false);
                    controlViews(true);
                }
            };
            showHide(mRTextView, registerRl, true);
            mNetworkController.Register(mContext, login, name, email, password, spam, listener);
        } else {
            Snackbar.make(registerRl, R.string.enter_data_correct, Snackbar.LENGTH_LONG).show();
            controlViews(true);
        }
    }

    private void showHide(View view, View relativeView, boolean show) {
        view.setVisibility(View.VISIBLE);
        Animator animator;
        if (show)
            animator = ViewAnimationUtils.createCircularReveal(view, Math.round(relativeView.getX()), Math.round(relativeView.getY()), 0, Math.max(relativeView.getWidth() * 2, relativeView.getHeight() * 2));
        else
            animator = ViewAnimationUtils.createCircularReveal(view, Math.round(relativeView.getX()), Math.round(relativeView.getY()), Math.max(relativeView.getWidth() * 2, relativeView.getHeight() * 2), 0);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(ANIM_DURATION);
        animator.start();
        if (!show)
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    view.setVisibility(View.INVISIBLE);
                }
            });
    }

    private void LoginFuncs() {
        controlViews(false);
        String login;
        String password;
        if(!BuildConfig.DEBUG) {
            login = Objects.requireNonNull(mLLoginEt.getText()).toString();
            password = Objects.requireNonNull(mLPasswordEt.getText()).toString();
        } else {
            login = "debugAcc";
            password = "testpassfordebug";
            mLLoginEt.setText(login);
            mLPasswordEt.setText(password);
        }
        if (mLLoginEt.validate(loginRegex, getString(R.string.login_must_be)) || password.length() >= 6) {
            Response.Listener<String> listener = response -> {
                try {
                    Log.d(TAG, "LoginFuncs response: "+response);
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse.isNull("error")) {
                        if (!jsonResponse.isNull("response")) {
                            try {
                                JSONObject jsonObjectResponse= jsonResponse.getJSONObject("response");
                                JSONObject jsonUserInfo = jsonObjectResponse.getJSONObject("user_data");
                                int user_id = jsonUserInfo.getInt("id");
                                String name = jsonUserInfo.getString("names");
                                String email = jsonUserInfo.getString("email");
                                String login1 = jsonUserInfo.getString("login");
                                String group = jsonUserInfo.getString("groupId");
                                String password_hash = jsonUserInfo.getString("password");
                                boolean spam = jsonUserInfo.getString("spam").equals("Yes");
                                int permission = jsonUserInfo.isNull("permission") ? 0 : jsonUserInfo.getInt("permission");
                                String token = jsonUserInfo.getString("token");
                                mSettings.edit().clear().apply();
                                SharedPreferences.Editor editor = mSettings.edit();
                                editor.putInt("user_id", user_id);
                                editor.putString("name", name);
                                editor.putString("email", email);
                                editor.putString("login", login1);
                                editor.putString("group", group);
                                editor.putBoolean("spam", spam);
                                editor.putInt("permission", permission);
                                editor.putString("token", token);
                                editor.putString("password", password_hash);
                                editor.putBoolean(PREFS_FIRST, false);
                                editor.apply();
                                Toast.makeText(mContext, R.string.success_login, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(FirstActivity.this, SplashActivity.class));
                                overridePendingTransition(R.anim.transition_out, R.anim.transition_in);
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(mContext, R.string.failed_get_user_info, Toast.LENGTH_SHORT).show();
                                showHide(mLTextView, loginRl, false);
                                controlViews(true);
                            }
                        } else {
                            Toast.makeText(mContext, R.string.wrong_password, Toast.LENGTH_SHORT).show();
                            showHide(mLTextView, loginRl, false);
                            controlViews(true);
                        }
                    } else {
                        JSONObject jsonError = jsonResponse.getJSONObject("error");
                        Toast.makeText(mContext, jsonError.getInt("code")+" "+jsonError.getString("text"), Toast.LENGTH_SHORT).show();
                        showHide(mLTextView, loginRl, false);
                        controlViews(true);
                    }
                } catch (Exception e) {
                    Toast.makeText(mContext, R.string.unknown_error, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    showHide(mLTextView, loginRl, false);
                    controlViews(true);
                }
            };
            controlViews(true);
            showHide(mLTextView, loginRl, true);
            mNetworkController.Login(mContext, login, password, listener, mNetworkController.getErrorListener(mContext));
        } else {
            Snackbar.make(loginRl, R.string.enter_username_and_pass, Snackbar.LENGTH_LONG).show();
            controlViews(true);
        }
    }

    private void onClickNotReg() {
        YoYo.with(Techniques.TakingOff).duration(ANIM_DURATION)
                .interpolate(new AccelerateDecelerateInterpolator())
                .onEnd(animator -> {
                    loginRl.setVisibility(View.INVISIBLE);
                    registerRl.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.Landing).duration(ANIM_DURATION).interpolate(new AccelerateDecelerateInterpolator()).playOn(registerRl);
                }).playOn(loginRl);
    }

    private void onClickNext() {
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
