package ru.devdem.reminder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

class NetworkController {
    private static RequestQueue queue;

    private static String URL_LOGIN = "https://api.devdem.ru/apps/schedule/accounts/login.php";
    private static String URL_REGISTER = "https://api.devdem.ru/apps/schedule/accounts/register.php";
    private static String URL_GETGROUPS = "https://api.devdem.ru/apps/schedule/getgroups.php";

    private static Response.ErrorListener getErrorListener(Context context) {
        return error -> {
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle(R.string.errornetwork)
                    .setMessage(context.getString(R.string.detail) + " " + error.toString())
                    .setCancelable(false)
                    .setPositiveButton(R.string.retry, (dialog1, which) -> {
                        Activity activity = (Activity) context;
                        activity.recreate();
                        dialog1.cancel();
                    })
                    .create();
            dialog.show();
        };
    }

    static void Login(Context context, String login, String password, Response.Listener<String> listener) {
        SendRequest sendLoginRequest = new SendRequest(login, password, listener, getErrorListener(context));
        if (queue == null) queue = Volley.newRequestQueue(context);
        queue.add(sendLoginRequest);
    }

    static void Register(Context context, String login, String name, String email, String password, String group, String spam, Response.Listener<String> listener) {
        SendRequest sendLoginRequest = new SendRequest(login, name, email, password, group, spam, listener, getErrorListener(context));
        if (queue == null) queue = Volley.newRequestQueue(context);
        queue.add(sendLoginRequest);
    }

    static void GetGroups(Context context, Spinner spinner) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        adapter.add(context.getString(R.string.choose));
        Response.Listener<String> listener = response -> {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                for (int i = 1; i <= 100; i++) {
                    try {
                        adapter.add(jsonResponse.getJSONObject(String.valueOf(i)).getString("name"));
                    } catch (Exception e) {
                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            spinner.setAdapter(adapter);
        };
        SendRequest sendGetGroupsRequest = new SendRequest(listener, getErrorListener(context));
        if (queue == null) queue = Volley.newRequestQueue(context);
        queue.add(sendGetGroupsRequest);
    }

    private static class SendRequest extends StringRequest {
        private Map<String, String> params;

        SendRequest(Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(Method.POST, URL_GETGROUPS, listener, errorListener);
            params = new HashMap<>();
        }

        SendRequest(String login, String password, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(Method.POST, URL_LOGIN, listener, errorListener);
            params = new HashMap<>();
            params.put("login", login);
            params.put("password", password);
        }

        SendRequest(String login, String name, String email, String password, String group, String spam, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(Method.POST, URL_REGISTER, listener, errorListener);
            params = new HashMap<>();
            params.put("login", login);
            params.put("name", name);
            params.put("email", email);
            params.put("password", password);
            params.put("group", group);
            params.put("spam", spam);
        }

        @Override
        public Map<String, String> getParams() {
            return params;
        }
    }
}

