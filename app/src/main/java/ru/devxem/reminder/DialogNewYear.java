package ru.devxem.reminder;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class DialogNewYear extends DialogFragment {

    static DialogNewYear newInstance() {
        Bundle args = new Bundle();
        DialogNewYear fragment = new DialogNewYear();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_new_year, null);

        return new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setView(v)
                .create();
    }
}
