package ru.devdem.reminder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class ChangeDialogFragment extends DialogFragment {
    private static final String ARG_STATE = "state";
    private static final String EXTRA_NEW_DATA = "ru.devdem.reminder.new_data_string";
    private static final String EXTRA_STATE = "ru.devdem.reminder.state";
    private View v;
    private int state;

    private ChangeDialogFragment() {
    }

    static ChangeDialogFragment newInstance(int state) {
        Bundle args = new Bundle();
        args.putInt(ARG_STATE, state);
        ChangeDialogFragment fragment = new ChangeDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Dialog);
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        switch (state) {
            case 0: {
                EditText oldPassEditText = v.findViewById(R.id.passET);
                EditText newPassEditText = v.findViewById(R.id.newPassET);
                EditText confirmPassEditText = v.findViewById(R.id.confirmPassET);
                String oldPass = oldPassEditText.getText().toString();
                String newPass = newPassEditText.getText().toString();
                String confirmPass = confirmPassEditText.getText().toString();
                if (newPass.equals(confirmPass) && newPass.length() >= 6) {
                    sendResult(Activity.RESULT_OK, oldPass + "|+_**_+|" + newPass);
                } else sendResult(Activity.RESULT_CANCELED, null);
                break;
            }
            case 1: {
                EditText newEmailEditText = v.findViewById(R.id.newEmailET);
                String newEmail = newEmailEditText.getText().toString();
                if (newEmail.length() >= 9) sendResult(Activity.RESULT_OK, newEmail);
                else sendResult(Activity.RESULT_CANCELED, null);
                break;
            }
            case 2: {
                EditText newLoginEditText = v.findViewById(R.id.newLoginET);
                String newLogin = newLoginEditText.getText().toString();
                if (newLogin.length() >= 6) sendResult(Activity.RESULT_OK, newLogin);
                else sendResult(Activity.RESULT_CANCELED, null);
                break;
            }
        }
        super.onCancel(dialog);
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        state = Objects.requireNonNull(bundle).getInt(ARG_STATE);
        switch (state) {
            case 0:
                v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_change_pass, null);
                break;
            case 1:
                v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_change_email, null);
                break;
            case 2:
                v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_change_login, null);
                break;
            default:
                v = LayoutInflater.from(getActivity()).inflate(null, null);
        }
        return new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setView(v)
                .create();
    }

    private void sendResult(int resultCode, String newdata) {
        if (getTargetFragment() == null) return;

        Intent intent = new Intent();
        intent.putExtra(EXTRA_NEW_DATA, newdata);
        intent.putExtra(EXTRA_STATE, state);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
