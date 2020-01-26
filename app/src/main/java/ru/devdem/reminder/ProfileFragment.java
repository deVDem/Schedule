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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;

public class ProfileFragment extends Fragment {
    private Context mContext;
    private SharedPreferences mSettings;
    private String[] permissions;
    private TextView profileName;
    private TextView profileLogin;
    private TextView profileEmail;
    private TextView profilePermission;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.fragment_profile, null);
        mContext = Objects.requireNonNull(getContext());
        permissions = getResources().getStringArray(R.array.permissions);
        mSettings = mContext.getSharedPreferences("settings", Context.MODE_PRIVATE);
        profileName = v.findViewById(R.id.profileName);
        profileLogin = v.findViewById(R.id.profileLogin);
        profileEmail = v.findViewById(R.id.profileEmail);
        profilePermission = v.findViewById(R.id.profilePermission);
        setHasOptionsMenu(true);
        updateUI();
        return v;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_reload:
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
