package ru.devdem.reminder;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.squareup.picasso.Picasso;

import java.util.Objects;

class DialogFullImageFragment extends DialogFragment {

    private static String ARG_URL = "url_photo";

    static DialogFullImageFragment newInstance(String url) {
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);

        DialogFullImageFragment dialogFullImageFragment = new DialogFullImageFragment();
        dialogFullImageFragment.setArguments(args);
        return dialogFullImageFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Context context = Objects.requireNonNull(getContext());
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.dialog_full_image, null);
        ImageView imageView = view.findViewById(R.id.imageFull);
        Picasso.get().load(Objects.requireNonNull(getArguments()).getString(ARG_URL)).into(imageView);
        return new AlertDialog.Builder(context, R.style.Dialog).setPositiveButton(R.string.close, (dialog, which) -> {
            dialog.cancel();
            System.gc();
        }).setTitle(R.string.fullimage).setView(view).create();
    }
}
