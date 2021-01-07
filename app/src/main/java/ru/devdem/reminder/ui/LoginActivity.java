package ru.devdem.reminder.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.snackbar.Snackbar;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONObject;

import java.util.Objects;
import java.util.Random;

import ru.devdem.reminder.BuildConfig;
import ru.devdem.reminder.R;
import ru.devdem.reminder.controllers.NetworkController;
import ru.devdem.reminder.ui.main.MainActivity;
import ru.devdem.reminder.ui.view.HoldButton;

public class LoginActivity extends AppCompatActivity {

    private static final String nameRegex =
            "(^[A-Z]{1}[a-z]{1,30} [A-Z]{1}[a-z]{1,30}$)|(^[А-Я]{1}[а-я]{1,30}" + " " +
                    "[А-Я]{1}[а-я]{1,30}$)";
    private static final String emailRegex =
            "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:" +
                    "[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[" +
                    "\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9]" +
                    ")?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?" +
                    "[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]" +
                    ":(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09" +
                    "\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    private static final String loginRegex = "[A-Za-z0-9_]{4,255}";

    private static final int ANIM_DURATION = 350;
    private static final String PREFS_FIRST = "first";
    private static final String NAME_PREFS = "settings";
    private static final String TAG = "FirstActivity";

    private NetworkController mNetworkController;
    private SharedPreferences mSettings;
    private Context mContext;

    // Login Views
    private RelativeLayout mRelativeLogin;
    private MaterialEditText mLETLogin;
    private MaterialEditText mLETPass;
    private Button mLLoginBtn;
    private TextView mLRestorePass;

    // Forgot pass views
    private RelativeLayout mRelativeForgot;
    private MaterialEditText mFEmail;
    private Button mFRestoreBtn;

    // Register Views
    private RelativeLayout mRelativeRegister;
    private MaterialEditText mRETLogin;
    private MaterialEditText mRETName;
    private MaterialEditText mRETEmail;
    private MaterialEditText mRETPass;
    private HoldButton mRRegBtn;
    private TextView mRTVHaveAcc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mSettings = getSharedPreferences(NAME_PREFS, MODE_PRIVATE);
        mNetworkController = NetworkController.get();
        mContext = this;

        loadingDialog = new AlertDialog.Builder(this)
                .setMessage(R.string.loading)
                .setCancelable(false)
                .create();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mRelativeForgot = findViewById(R.id.forgotPassRl);
        mFEmail = findViewById(R.id.forgotEmail);
        mFRestoreBtn = findViewById(R.id.forgotPassBtn);
        mFRestoreBtn.setOnClickListener(v -> {
            hideKeyboard();
            String email = Objects.requireNonNull(mFEmail.getText()).toString();
            if (mFEmail.validate(emailRegex, getText(R.string.enter_the_correct_address))) {
                Toast.makeText(this, R.string.wait, Toast.LENGTH_SHORT).show();
                mNetworkController.restorePassRequest(this, response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject responseObject = object.getJSONObject("response");
                        if(responseObject.isNull("error")) {
                            Toast.makeText(this, R.string.check_mailbox, Toast.LENGTH_LONG).show();
                            finish();
                        }
                    } catch (Exception e) {
                        controlViews(true);
                        Log.d(TAG, "forgotPass Error: response="+response);
                        Toast.makeText(this, R.string.unknown_error, Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }, error -> {
                    controlViews(true);
                    Toast.makeText(this, R.string.errorNetwork, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "forgotPass Error: ", error);
                }, email);
                controlViews(false);
            }

        });

        mRelativeLogin = findViewById(R.id.loginRl);
        mLETLogin = findViewById(R.id.loginETLogin);
        mLETPass = findViewById(R.id.loginETPassword);
        mLLoginBtn = findViewById(R.id.loginBtn);
        mLRestorePass = findViewById(R.id.loginRestorePass);

        mLRestorePass.setOnClickListener((v) -> {
            hideKeyboard();
            ChangeRelativeLayoutView(mRelativeForgot, mRelativeLogin, false);
        });
        mLLoginBtn.setOnClickListener((l) -> Login());

        mRelativeRegister = findViewById(R.id.registerRl);
        mRETLogin = findViewById(R.id.registerEtLogin);
        mRETName = findViewById(R.id.registerEtName);
        mRETEmail = findViewById(R.id.registerEtEmail);
        mRETPass = findViewById(R.id.registerEtPassword);
        mRRegBtn = findViewById(R.id.registerBtn);
        mRTVHaveAcc = findViewById(R.id.registerTVHaveAcc);

        mRRegBtn.setHoldDownListener((l) -> Register());
        mRTVHaveAcc.setOnClickListener((l) -> {
            hideKeyboard();
            ChangeRelativeLayoutView(mRelativeLogin, mRelativeRegister, false);
        });
    }

    @Override
    public void onBackPressed() {
        if (mRelativeLogin.getVisibility() == View.VISIBLE) {
            ChangeRelativeLayoutView(mRelativeRegister, mRelativeLogin, true);
        } else if (mRelativeForgot.getVisibility() == View.VISIBLE) {
            ChangeRelativeLayoutView(mRelativeLogin, mRelativeForgot, true);
        } else {
            super.onBackPressed();
        }
    }

    public void ChangeRelativeLayoutView(RelativeLayout in, RelativeLayout out, boolean reverse) {
        YoYo.with(reverse ? Techniques.FadeOutRight : Techniques.FadeOutLeft)
                .duration(ANIM_DURATION)
                .interpolate(new AccelerateDecelerateInterpolator())
                .onEnd(animator -> {
                    out.setVisibility(View.INVISIBLE);
                    in.setVisibility(View.VISIBLE);
                    YoYo.with(reverse ? Techniques.FadeInLeft : Techniques.FadeInRight)
                            .duration(ANIM_DURATION)
                            .interpolate(new AccelerateDecelerateInterpolator())
                            .playOn(in);
                }).playOn(out);
    }


    /**
     * Enable or disable views for lock from user
     *
     */

    private void controlViews(boolean enable) {
        mRelativeLogin.setEnabled(enable);
        mLETLogin.setEnabled(enable);
        mLETPass.setEnabled(enable);
        mLLoginBtn.setEnabled(enable);
        mLRestorePass.setEnabled(enable);
        mRelativeForgot.setEnabled(enable);
        mFEmail.setEnabled(enable);
        mFRestoreBtn.setEnabled(enable);
        mRelativeRegister.setEnabled(enable);
        mRETLogin.setEnabled(enable);
        mRETName.setEnabled(enable);
        mRETEmail.setEnabled(enable);
        mRETPass.setEnabled(enable);
        mRRegBtn.setEnabled(enable);
        mRTVHaveAcc.setEnabled(enable);
    }

    public void hideKeyboard() {
        View view = findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private AlertDialog loadingDialog;

    private void showHideLoadingDialog(boolean show) {
        if (show) loadingDialog.show();
        else loadingDialog.dismiss();
    }

    private void Login() {
        controlViews(false);
        String login;
        String password;
        login = Objects.requireNonNull(mLETLogin.getText()).toString();
        password = Objects.requireNonNull(mLETPass.getText()).toString();
        if (BuildConfig.DEBUG && login.equals("")) {
            login = "debugAcc";
            password = "testpassfordebug";
            mLETLogin.setText(login);
            mLETPass.setText(password);
        }
        if (mLETLogin.validate(loginRegex, getString(R.string.login_must_be))
                && password.length() >= 6 &&
                (BuildConfig.DEBUG || !(login.equals("debugAcc") &&
                        password.equals("testpassfordebug")))) {
            Response.Listener<String> listener = response -> {
                try {
                    Log.d(TAG, "Login response: " + response);
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse.isNull("error")) {
                        if (!jsonResponse.isNull("response")) {
                            try {
                                JSONObject jsonObjectResponse =
                                        jsonResponse.getJSONObject("response");
                                JSONObject jsonUserInfo =
                                        jsonObjectResponse.getJSONObject("user_data");
                                int user_id = jsonUserInfo.getInt("id");
                                String name = jsonUserInfo.getString("names");
                                String email = jsonUserInfo.getString("email");
                                String login1 = jsonUserInfo.getString("login");
                                String group = jsonUserInfo.getString("groupId");
                                String password_hash = jsonUserInfo.getString("password");
                                boolean spam = jsonUserInfo.getString("spam").equals("Yes");
                                int permission =
                                        jsonUserInfo.isNull("permission") ? 0 : jsonUserInfo
                                                .getInt("permission");
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
                                Toast.makeText(mContext, R.string.success_login, Toast.LENGTH_SHORT)
                                        .show();
                                startActivity(new Intent(LoginActivity.this,
                                        MainActivity.class));
                                overridePendingTransition(R.anim.transition_out,
                                        R.anim.transition_in);
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(mContext, R.string.failed_get_user_info,
                                        Toast.LENGTH_SHORT).show();
                                showHideLoadingDialog(false);
                                controlViews(true);
                            }
                        } else {
                            Toast.makeText(mContext, R.string.wrong_password, Toast.LENGTH_SHORT).show();
                            showHideLoadingDialog(false);
                            controlViews(true);
                        }
                    } else {
                        JSONObject jsonError = jsonResponse.getJSONObject("error");
                        Toast.makeText(mContext, jsonError.getInt("code") + " " +
                                jsonError.getString("text"), Toast.LENGTH_SHORT).show();
                        showHideLoadingDialog(false);
                        controlViews(true);
                    }
                } catch (Exception e) {
                    Toast.makeText(mContext, R.string.unknown_error, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    showHideLoadingDialog(false);
                    controlViews(true);
                }
            };
            controlViews(true);
            showHideLoadingDialog(true);
            mNetworkController.Login(mContext, login, password, listener, mNetworkController.getErrorListener(mContext));
        } else {
            Snackbar.make(mRelativeLogin, R.string.enter_username_and_pass, Snackbar.LENGTH_LONG)
                    .show();
            controlViews(true);
        }
    }

    private void Register() {
        controlViews(false);
        String login;
        String name;
        String email;
        String password;
        String spam;
        if (!BuildConfig.DEBUG) {
            login = Objects.requireNonNull(mRETLogin.getText()).toString();
            name = Objects.requireNonNull(mRETName.getText()).toString();
            email = Objects.requireNonNull(mRETEmail.getText()).toString();
            password = Objects.requireNonNull(mRETPass.getText()).toString();
            spam = "Yes";
        } else {
            login = "debug" + new Random().nextInt(1000);
            name = "Debug" + " " + "Debugovich";
            email = "debug" + new Random().nextInt(1000) + "@devdem.ru";
            password = "testpassfordebug";
            spam = new Random().nextBoolean() ? "Yes" : "No";
            mRETLogin.setText(login);
            mRETName.setText(name);
            mRETEmail.setText(email);
            mRETPass.setText(password);
            mLETLogin.setText(login);
            mLETPass.setText(password);
        }
        if (mRETLogin.validate(loginRegex, getString(R.string.login_must_be)) &&
                mRETName.validate(nameRegex, getString(R.string.type_first_and_last_name)) &&
                mRETEmail.validate(emailRegex, getText(R.string.enter_the_correct_address)) &&
                password.length() > 5) {
            Response.Listener<String> listener = response -> {
                try {
                    Log.d(TAG, "Register: "+response);
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
                            Toast.makeText(mContext, R.string.success_registration, Toast.LENGTH_SHORT)
                                    .show();
                            startActivity(new Intent(LoginActivity.this,
                                    MainActivity.class));
                            overridePendingTransition(R.anim.transition_out, R.anim.transition_in);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                            controlViews(true);
                            Toast.makeText(mContext, R.string.failed_get_user_info,
                                    Toast.LENGTH_SHORT).show();
                            showHideLoadingDialog(false);
                        }
                    } else {
                        JSONObject errorJson = jsonResponse.getJSONObject("error");
                        Toast.makeText(mContext, errorJson.getInt("code") + " " +
                                errorJson.getString("text"), Toast.LENGTH_SHORT).show();
                        showHideLoadingDialog(false);
                        controlViews(true);
                        Log.e(TAG, "RegisterFuncs: Error " + errorJson.getInt("code"),
                                new Exception());
                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, R.string.unknown_error, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    showHideLoadingDialog(false);
                    controlViews(true);
                }
            };
            showHideLoadingDialog(true);
            mNetworkController.Register(mContext, login, name, email, password, spam, listener);
        } else {
            Snackbar.make(mRelativeRegister, R.string.enter_data_correct, Snackbar.LENGTH_LONG)
                    .show();
            controlViews(true);
        }
    }

}
