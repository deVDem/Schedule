package ru.devxem.reminder.api;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import ru.devxem.reminder.R;

public class Error {
    public static void setError(Context context, String id) {
        if(id==null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(context.getString(R.string.error_text))
                    .setTitle(context.getString(R.string.error))
                    .setNegativeButton(context.getString(R.string.Exit), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            System.exit(0);
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(context.getString(R.string.error_text)+"\nID: "+id)
                    .setTitle(context.getString(R.string.error))
                    .setNegativeButton(context.getString(R.string.Exit), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            System.exit(0);
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        }
    }
}
