package ru.devdem.reminder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private static final String EXTRA_NEW_DATA = "ru.devdem.reminder.new_data_string";
    private static final String EXTRA_STATE = "ru.devdem.reminder.state";
    private static String DIALOG_TAG = "changeDialog";
    private static int DIALOG_REQUEST = 1337;
    Context mContext;
    private SharedPreferences mSettings;
    private String[] permissions;
    private TextView profileName;
    private TextView profileLogin;
    private TextView profileEmail;
    private TextView profilePermission;
    private Button buttonSpamToggle;

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
        Button buttonChangePass = v.findViewById(R.id.buttonProfileChangePass);
        Button buttonChangeEmail = v.findViewById(R.id.buttonProfileChangeEmail);
        Button buttonChangeLogin = v.findViewById(R.id.buttonProfileChangeLogin);
        buttonSpamToggle = v.findViewById(R.id.buttonProfileChangeSpam);
        buttonChangePass.setOnClickListener(v13 -> openDialog(0));
        buttonChangeEmail.setOnClickListener(v12 -> openDialog(1));
        buttonChangeLogin.setOnClickListener(v1 -> openDialog(2));
        buttonSpamToggle.setOnClickListener(v14 -> openDialog(3));
        updateUI();
        return v;
    }

    private void openDialog(int state) {
        Toast.makeText(mContext, R.string.temporarily_not_available, Toast.LENGTH_LONG).show();
        /*FragmentManager manager = getFragmentManager();
        ChangeDialogFragment dialog = ChangeDialogFragment.newInstance(state);
        dialog.setTargetFragment(this, DIALOG_REQUEST);
        dialog.show(Objects.requireNonNull(manager), DIALOG_TAG);*/
    }

    private void updateUI() {
        String name = mSettings.getString("name", "null");
        String login = "@" + mSettings.getString("login", "null");
        String email = mSettings.getString("email", "null");
        String permission = permissions[mSettings.getInt("permission", 0)];
        if (mSettings.getBoolean("spam", true))
            buttonSpamToggle.setText(R.string.disable_the_mailing_list);
        else buttonSpamToggle.setText(R.string.enable_the_mailing_list);
        profileName.setText(name);
        profileLogin.setText(login);
        profileEmail.setText(email);
        profilePermission.setText(permission);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == DIALOG_REQUEST && data != null) {
            Log.d(DIALOG_TAG, "ok");
            Toast.makeText(mContext, data.getStringExtra(EXTRA_NEW_DATA), Toast.LENGTH_SHORT).show();
            updateUI();
        }
    }
}
