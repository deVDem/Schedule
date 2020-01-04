package ru.devdem.reminder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.google.android.material.snackbar.Snackbar;

public class FirstActivity extends AppCompatActivity {

    RelativeLayout helloRl;
    RelativeLayout loginRl;
    RelativeLayout registerRl;
    TextView loginTVNotReg;
    Button mLoginButton;
    EditText mLLoginEt;
    EditText mLPasswordEt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
        LoginFuncs();

    }

    private void LoginFuncs() {
        mLoginButton.setOnClickListener(v -> {
            String login = mLLoginEt.getText().toString();
            String password = mLPasswordEt.getText().toString();
            if (login.length() >= 6 || password.length() >= 6) {
                Response.Listener<String> listener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                };
                NetworkController.Login(login, password, listener);
            } else {
                Snackbar.make(loginRl, "Введите логин и\\или пароль.", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void onClickNotReg(View v) {
        int cx = loginRl.getWidth() / 2;
        int cy = loginRl.getHeight() / 2;
        float radius = loginRl.getWidth();
        Animator animator = ViewAnimationUtils.createCircularReveal(loginRl, cx, cy, radius, 0);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                loginRl.setVisibility(View.INVISIBLE);
                registerRl.setVisibility(View.VISIBLE);
                registerRl.setAnimation(AnimationUtils.loadAnimation(FirstActivity.this, R.anim.transition_out));
            }
        });
        animator.start();
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
                loginRl.setVisibility(View.VISIBLE);
                loginRl.setAnimation(AnimationUtils.loadAnimation(FirstActivity.this, R.anim.transition_out));
            }
        });
        anim.start();
    }
}
