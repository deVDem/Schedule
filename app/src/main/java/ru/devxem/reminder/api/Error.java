package ru.devxem.reminder.api;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import ru.devxem.reminder.R;
import ru.devxem.reminder.ui.home.HomeFragment;

public class Error {
    public static void setErr(Context context, String error, String email) {
        sendError sendError = new sendError(error, email);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(sendError);
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
    }
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
                    .setPositiveButton("Продолжить использование", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        }
    }

    static void noInfo(Context context, boolean cancel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Нет данных, Вам требуется подключение к интернету.\n\nП.с. подключаться к интернету нужно раз в день для обновления.\nДля принудительного обновления потяните на нужном экране вниз")
                .setTitle(context.getString(R.string.error))
                .setNegativeButton(context.getString(R.string.Exit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        System.exit(0);
                    }
                })
                .setPositiveButton("Продолжить использование", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HomeFragment.setEnabled(true);
                        dialog.cancel();
                    }
                })
                .setCancelable(cancel)
                .create()
                .show();
    }
}

class sendError extends StringRequest {
    private static final String LOGIN_REQUEST_URL = URLs.getErr();
    private Map<String, String> params;

    sendError(String error, String email) {
        super(Method.POST, LOGIN_REQUEST_URL, null, null);
        params = new HashMap<>();
        params.put("error", error);
        params.put("email", email);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
