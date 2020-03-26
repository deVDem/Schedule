package ru.devdem.reminder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.Objects;

public class ProfileFragment extends Fragment {
    private Context mContext;
    private SharedPreferences mSettings;
    private String[] permissions;
    private TextView profileName;
    private TextView profileLogin;
    private TextView profileEmail;
    private TextView profilePermission;
    private MainActivity mMainActivity;
    private NetworkController networkController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.fragment_profile, null);
        mContext = Objects.requireNonNull(getContext());
        mMainActivity = (MainActivity) Objects.requireNonNull(getActivity());
        networkController = NetworkController.get();
        permissions = getResources().getStringArray(R.array.permissions);
        mSettings = mContext.getSharedPreferences("settings", Context.MODE_PRIVATE);
        profileName = v.findViewById(R.id.profileName);
        profileLogin = v.findViewById(R.id.profileLogin);
        profileEmail = v.findViewById(R.id.profileEmail);
        profilePermission = v.findViewById(R.id.profilePermission);
        setHasOptionsMenu(true);
        updateUI();
        Button mLeaveButton = v.findViewById(R.id.buttonLeaveGroup);
        mLeaveButton.setOnClickListener(v1 -> {
            Toast.makeText(mContext, R.string.loading, Toast.LENGTH_LONG).show();
            v1.setEnabled(false);
            Response.Listener<String> listener = response -> {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String status = jsonResponse.getString("status");
                    String error = jsonResponse.getString("error");
                    if (status.equals("error")) {
                        if (error.equals("WRONG_TOKEN")) {
                            mMainActivity.checkAccount();
                            mContext.getSharedPreferences("jsondata", Context.MODE_PRIVATE).edit().clear().apply();
                        }
                        Toast.makeText(mContext, error, Toast.LENGTH_LONG).show();
                    } else if (status.equals("JOINED")) {
                        mMainActivity.checkAccount();
                        mContext.getSharedPreferences("jsondata", Context.MODE_PRIVATE).edit().clear().apply();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    v1.setEnabled(true);
                }
            };
            Response.ErrorListener errorListener = error -> {
                v1.setEnabled(true);
                Toast.makeText(mContext, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            };
            networkController.joinToGroup(mContext, listener, errorListener, "0", mSettings.getString("token", "null"));
        });
        return v;
    }

    private void restart() {
        mMainActivity.startActivity(new Intent(mContext, SplashActivity.class));
        mMainActivity.overridePendingTransition(R.anim.transition_out, R.anim.transition_in);
        mMainActivity.finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_reload:
                mMainActivity.checkAccount();
                updateUI();
                return true;
            case R.id.menu_edit:
                startActivityForResult(new Intent(mContext, EditProfileActivity.class), 228);
                Objects.requireNonNull(getActivity()).overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    private void updateUI() {
        String name = mSettings.getString("name", "null");
        String login = "@" + mSettings.getString("login", "null");
        String email = mSettings.getString("email", "null");
        String permission = permissions[mSettings.getInt("permission", 0)];
        profileName.setText(name);
        profileLogin.setText(login);
        profileEmail.setText(email);
        profilePermission.setText(permission);
    }
}
