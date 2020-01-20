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
    private static String URL_NOTIFICATIONS = "https://api.devdem.ru/apps/schedule/notifications.php";
    private static String URL_LESSONS = "https://api.devdem.ru/apps/schedule/lessons.php";
    private static String URL_GET_VER_INT = "https://api.devdem.ru/apps/schedule/getver.php";
    private static String URL_SERVICE_DEBUG = "https://api.devdem.ru/apps/schedule/service_debug.php";

    private static Response.ErrorListener getErrorListener(Context context) {
        return error -> {
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle(R.string.errorNetwork)
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

    static void serviceDebug(Context context, int count) {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", String.valueOf(context.getSharedPreferences("settings", Context.MODE_PRIVATE).getInt("user_id", 0)));
        map.put("token", context.getSharedPreferences("settings", Context.MODE_PRIVATE).getString("token", "null"));
        map.put("count", String.valueOf(count));
        SendRequest sendRequest = new SendRequest(null, null, URL_SERVICE_DEBUG, map);
        if (queue == null) queue = Volley.newRequestQueue(context);
        queue.add(sendRequest);
    }

    static void Login(Context context, String login, String password, Response.Listener<String> listener) {
        Map<String, String> map = new HashMap<>();
        map.put("login", login);
        map.put("password", password);
        SendRequest sendLoginRequest = new SendRequest(listener, getErrorListener(context), URL_LOGIN, map);
        if (queue == null) queue = Volley.newRequestQueue(context);
        queue.add(sendLoginRequest);
    }

    static void Register(Context context, String login, String name, String email, String password, String group, String spam, Response.Listener<String> listener) {
        Map<String, String> map = new HashMap<>();
        map.put("login", login);
        map.put("name", name);
        map.put("email", email);
        map.put("password", password);
        map.put("group", group);
        map.put("spam", spam);
        SendRequest sendRequest = new SendRequest(listener, getErrorListener(context), URL_REGISTER, map);
        if (queue == null) queue = Volley.newRequestQueue(context);
        queue.add(sendRequest);
    }

    static void getLessons(Context context, Response.Listener<String> listener, Response.ErrorListener errorListener, String group) {
        Map<String, String> map = new HashMap<>();
        map.put("group", group);
        SendRequest sendRequest = new SendRequest(listener, errorListener, URL_LESSONS, map);
        if (queue == null) queue = Volley.newRequestQueue(context);
        queue.add(sendRequest);
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
        SendRequest sendGetGroupsRequest = new SendRequest(listener, getErrorListener(context), URL_GETGROUPS, new HashMap<>());
        if (queue == null) queue = Volley.newRequestQueue(context);
        queue.add(sendGetGroupsRequest);
    }

    static void getNotifications(Context context, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        SendRequest sendLoginRequest = new SendRequest(listener, errorListener, URL_NOTIFICATIONS, new HashMap<>());
        if (queue == null) queue = Volley.newRequestQueue(context);
        queue.add(sendLoginRequest);
    }

    static void getLastVerInt(Context context, Response.Listener<String> listener) {
        SendRequest sendRequest = new SendRequest(listener, null, URL_GET_VER_INT, new HashMap<>());
        if (queue == null) queue = Volley.newRequestQueue(context);
        queue.add(sendRequest);
    }

    private static class SendRequest extends StringRequest {
        private Map<String, String> mParams;

        SendRequest(Response.Listener<String> listener, Response.ErrorListener errorListener, String url, Map<String, String> params) {
            super(Method.POST, url, listener, errorListener);
            mParams = params;
        }
        @Override
        public Map<String, String> getParams() {
            return mParams;
        }
    }
}

