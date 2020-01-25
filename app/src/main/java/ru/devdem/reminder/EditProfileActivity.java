package ru.devdem.reminder;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONObject;

import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {

    private MaterialEditText mEtName;
    private MaterialEditText mEtEmail;
    private MaterialEditText mEtLogin;
    private SharedPreferences mPreferences;
    private FloatingActionButton mActionButton;
    private String name;
    private String email;
    private String login;
    private String emailRegex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        View v = View.inflate(this, R.layout.activity_edit_profile, null);
        setTheme(R.style.EditProfile);
        setContentView(v);
        mEtName = v.findViewById(R.id.etName);
        mEtEmail = v.findViewById(R.id.etEmail);
        mEtLogin = v.findViewById(R.id.etLogin);
        mActionButton = v.findViewById(R.id.floatingActionButton);
        mActionButton.setOnClickListener(v1 -> {
            if (mEtName.validate(".{6,1024}$", getResources().getString(R.string.type_first_and_last_name)) && mEtEmail.validate(emailRegex, getResources().getString(R.string.enter_the_correct_address)) && mEtLogin.validate(".{6,255}$", getResources().getString(R.string.helperLogin))) {
                name = Objects.requireNonNull(mEtName.getText()).toString();
                email = Objects.requireNonNull(mEtEmail.getText()).toString();
                login = Objects.requireNonNull(mEtLogin.getText()).toString();
                // TODO: 25.01.2020 Add visual
                updateProfile();
            } else Toast.makeText(this, "NOT OK", Toast.LENGTH_SHORT).show();
        });
        setTitle(R.string.editing_a_profile);
        name = mPreferences.getString("name", "null");
        email = mPreferences.getString("email", "null");
        login = mPreferences.getString("login", "null");
        mEtLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                check();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                check();
            }

            @Override
            public void afterTextChanged(Editable s) {
                check();
            }
        });
        mEtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                check();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                check();
            }

            @Override
            public void afterTextChanged(Editable s) {
                check();
            }
        });
        mEtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                check();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                check();
            }

            @Override
            public void afterTextChanged(Editable s) {
                check();
            }
        });
        mEtName.setText(name);
        mEtEmail.setText(email);
        mEtLogin.setText(login);
        check();
    }

    private void updateProfile() {
        Response.Listener<String> listener = response -> {
            try {
                JSONObject object = new JSONObject(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        Response.ErrorListener errorListener = error -> {
            // TODO: 25.01.2020 Add visual
        };
        NetworkController.editProfile(this, listener, errorListener, name, email, login, mPreferences.getString("token", "null"));
    }

    private void check() {
        if (mEtName.validate(".{6,1024}$", getResources().getString(R.string.type_first_and_last_name)) && mEtEmail.validate(emailRegex, getResources().getString(R.string.enter_the_correct_address)) && mEtLogin.validate(".{6,255}$", getResources().getString(R.string.helperLogin))) {
            mActionButton.show();
        } else mActionButton.hide();
    }
}
