package ru.devdem.reminder;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONObject;

import java.util.Objects;

public class NewNotificationActivity extends AppCompatActivity {

    private SharedPreferences mSettings;
    private AlertDialog dialog;
    private NetworkController mNetworkController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettings = getSharedPreferences("settings", MODE_PRIVATE);
        setTheme(R.style.EditProfile);
        setTitle("New notification");
        mNetworkController = NetworkController.get();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog = builder.setMessage(R.string.loading).create();
        dialog.show();
        Response.Listener<String> listener = response -> {
            try {
                Log.d("noti", "onCreate: " + response);
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
                            editor.apply();
                            start();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Неудалось получить информацию о пользователе.", Toast.LENGTH_SHORT).show();
                            exit();
                        }
                    } else {
                        Toast.makeText(this, "Wrong password.", Toast.LENGTH_SHORT).show();
                        exit();
                    }
                } else {
                    Toast.makeText(this, "Wrong login or email.", Toast.LENGTH_SHORT).show();
                    exit();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Произошла неизвестная ошибка", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        };
        Response.ErrorListener errorListener = error -> {
            Toast.makeText(this, R.string.errorNetwork, Toast.LENGTH_SHORT).show();
            exit();
        };
        mNetworkController.Login(this, mSettings.getString("login", ""), mSettings.getString("password", ""), listener, errorListener);
    }

    private void start() {
        dialog.cancel();
        View view = View.inflate(this, R.layout.activity_new_notification, null);
        MaterialEditText etTitle = view.findViewById(R.id.etTitle);
        MaterialEditText etText = view.findViewById(R.id.etText);
        MaterialEditText etUrl = view.findViewById(R.id.etUrlImage);
        FloatingActionButton actionButton = view.findViewById(R.id.floatingActionButton);
        actionButton.setOnClickListener(v -> {
            String title = Objects.requireNonNull(etTitle.getText()).toString();
            String message = Objects.requireNonNull(etText.getText()).toString();
            String urlImage = Objects.requireNonNull(etUrl.getText()).toString();
            Response.Listener<String> listener = response -> {
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
            };
            Response.ErrorListener errorListener = error -> {
                Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
            };
            mNetworkController.addNotification(this, listener, errorListener, mSettings.getString("token", ""), mSettings.getString("group", ""), title, message, urlImage);
        });
        setContentView(view);
    }

    private void exit() {
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }
}
