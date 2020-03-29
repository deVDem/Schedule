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
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.snackbar.Snackbar;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONObject;

public class FirstActivity extends AppCompatActivity {

    private RelativeLayout helloRl;
    private RelativeLayout loginRl;
    private RelativeLayout registerRl;
    private EditText mLLoginEt;
    private EditText mLPasswordEt;
    private TextView mLTextView;
    private Button mLoginButton;
    private TextView mLoginTVNotReg;

    private Context mContext;
    private SharedPreferences mSettings;

    private NetworkController mNetworkController;

    private EditText mRLoginEt;
    private EditText mRNameEt;
    private MaterialEditText mREmailEt;
    private EditText mRPassEt;
    private EditText mRConPassEt;
    private CheckBox mRCheckSpam;
    private TextView mRTextView;
    private Button mRegisterButton;

    private String emailRegex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";


    private int ANIM_DURATION = 700;
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
        String login = mRLoginEt.getText().toString();
        String name = mRNameEt.getText().toString();
        String email = mREmailEt.getText().toString();
        String password = mRPassEt.getText().toString();
        String confirmPassword = mRConPassEt.getText().toString();
        String spam = mRCheckSpam.isChecked() ? "1" : "0";
        if (login.length() > 5 && name.length() > 5 && mREmailEt.validate(emailRegex, getText(R.string.enter_the_correct_address)) && password.length() > 5 && password.equals(confirmPassword)) {
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
                            Toast.makeText(mContext, R.string.user_already_registered, Toast.LENGTH_SHORT).show();
                            showHide(mRTextView, registerRl, false);
                            controlViews(true);
                        }
                    } else {
                        Toast.makeText(mContext, R.string.unknown_error, Toast.LENGTH_SHORT).show();
                        showHide(mRTextView, registerRl, false);
                        controlViews(true);
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
                                String password_hash = jsonUserInfo.getString("password");
                                boolean spam = jsonUserInfo.getString("spam").equals("1");
                                int permission = jsonUserInfo.getInt("permission");
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
                        Toast.makeText(mContext, R.string.wrong_login_email, Toast.LENGTH_SHORT).show();
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
