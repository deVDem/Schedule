package ru.devdem.reminder.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.rengwuxian.materialedittext.MaterialEditText;

import ru.devdem.reminder.R;
import ru.devdem.reminder.controllers.NetworkController;

public class LoginActivity extends AppCompatActivity {

    private static final String nameRegex = "(^[A-Z]{1}[a-z]{1,30} [A-Z]{1}[a-z]{1,30}$)|(^[А-Я]{1}[а-я]{1,30} [А-Я]{1}[а-я]{1,30}$)";
    private static final String emailRegex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    private static final String loginRegex = "[A-Za-z0-9_]{4,255}";

    private static final int ANIM_DURATION = 350;
    private static final String PREFS_FIRST = "first";
    private static final String NAME_PREFS = "settings";
    private static final String TAG = "FirstActivity";

    private NetworkController mNetworkController;

    private RelativeLayout mRelativeLogin;
    private MaterialEditText mLETLogin;
    private MaterialEditText mLETPass;
    private Button mLLoginBtn;
    private TextView mLRegBtn;

    private RelativeLayout mRelativeRegister;
    private MaterialEditText mRETLogin;
    private MaterialEditText mRETName;
    private MaterialEditText mRETEmail;
    private MaterialEditText mRETPass;
    private Button mRRegBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_new);
        mRelativeLogin = findViewById(R.id.loginRl);
        mLETLogin = findViewById(R.id.loginETLogin);
        mLETPass = findViewById(R.id.loginETPassword);
        mLLoginBtn = findViewById(R.id.loginBtn);
        mLRegBtn = findViewById(R.id.loginTVNotReg);

        mLRegBtn.setOnClickListener((l) -> {
            hideKeyboard();
            ChangeRelativeLayoutView(mRelativeRegister, mRelativeLogin, false);
        });

        mRelativeRegister = findViewById(R.id.registerRl);
        mRETLogin = findViewById(R.id.registerEtLogin);
        mRETName = findViewById(R.id.registerEtName);
        mRETEmail = findViewById(R.id.registerEtEmail);
        mRETPass = findViewById(R.id.registerEtPassword);
        mRRegBtn = findViewById(R.id.registerBtn);
    }

    @Override
    public void onBackPressed() {
        if (mRelativeRegister.getVisibility() == View.VISIBLE) {
            ChangeRelativeLayoutView(mRelativeLogin, mRelativeRegister, true);
        } else {
            super.onBackPressed();
        }
    }

    public void ChangeRelativeLayoutView(RelativeLayout in, RelativeLayout out, boolean reverse) {
        YoYo.with(reverse ? Techniques.FadeOutRight : Techniques.FadeOutLeft).duration(ANIM_DURATION)
                .interpolate(new AccelerateDecelerateInterpolator()).onEnd(animator -> {
            out.setVisibility(View.INVISIBLE);
            in.setVisibility(View.VISIBLE);
            YoYo.with(reverse ? Techniques.FadeInLeft : Techniques.FadeInRight).duration(ANIM_DURATION)
                    .interpolate(new AccelerateDecelerateInterpolator()).playOn(in);
        }).playOn(out);
    }

    private void controlViews(boolean enable) {
        mRelativeLogin.setEnabled(enable);
        mRETName.setEnabled(enable);
        mRETEmail.setEnabled(enable);
        mRETPass.setEnabled(enable);
        mRETLogin.setEnabled(enable);
        mLETLogin.setEnabled(enable);
        mLETPass.setEnabled(enable);
        mLLoginBtn.setEnabled(enable);
        mRRegBtn.setEnabled(enable);
        mLRegBtn.setEnabled(enable);
    }

    public void hideKeyboard() {
        View view = findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
