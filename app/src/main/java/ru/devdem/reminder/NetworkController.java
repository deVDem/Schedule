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
    private static NetworkController sNetworkController;
    private String URL_LOGIN = "/accounts/login.php";
    private String URL_REGISTER = "/accounts/register.php";
    private String URL_GET_GROUPS = "/groups/get.php";
    private String URL_NOTIFICATIONS_GET = "/notifications/get.php";
    private String URL_LESSONS_GET = "/lessons/get.php";
    private String URL_GET_VER_INT = "/getver.php";
    private String URL_UPDATE_PROFILE = "/accounts/update.php";
    private String URL_ADD_NOTIFICATION = "/notifications/add.php";
    private static RequestQueue queue;

    private NetworkController() {
        String URL_ROOT = "https://api.devdem.ru/apps/schedule/v/" + BuildConfig.VERSION_CODE;
            URL_LOGIN = URL_ROOT + URL_LOGIN;
            URL_REGISTER = URL_ROOT + URL_REGISTER;
            URL_GET_GROUPS = URL_ROOT + URL_GET_GROUPS;
            URL_NOTIFICATIONS_GET = URL_ROOT + URL_NOTIFICATIONS_GET;
        URL_LESSONS_GET = URL_ROOT + URL_LESSONS_GET;
            URL_GET_VER_INT = URL_ROOT + URL_GET_VER_INT;
            URL_UPDATE_PROFILE = URL_ROOT + URL_UPDATE_PROFILE;
            URL_ADD_NOTIFICATION = URL_ROOT + URL_ADD_NOTIFICATION;
    }

    public static NetworkController get() {
        if (sNetworkController == null) sNetworkController = new NetworkController();
        return sNetworkController;
    }

    Response.ErrorListener getErrorListener(Context context) {
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

    private void goSend(Context context, Response.Listener<String> listener, Response.ErrorListener errorListener, String URL, Map<String, String> map) {
        SendRequest sendRequest = new SendRequest(listener, errorListener, URL, map);
        if (queue == null) queue = Volley.newRequestQueue(context);
        queue.add(sendRequest);
    }

    void addNotification(Context context, Response.Listener<String> listener, Response.ErrorListener errorListener, String token, String group, String title, String message, String urlImage) {
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("group", group);
        map.put("title", title);
        map.put("message", message);
        map.put("urlImage", urlImage);
        goSend(context, listener, errorListener, URL_ADD_NOTIFICATION, map);

    }

    void editProfile(Context context, Response.Listener<String> listener, Response.ErrorListener errorListener, String name, String email, String login, String token) {
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("email", email);
        map.put("login", login);
        map.put("token", token);
        goSend(context, listener, errorListener, URL_UPDATE_PROFILE, map);
    }

    void Login(Context context, String login, String password, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        Map<String, String> map = new HashMap<>();
        map.put("login", login);
        map.put("password", password);
        goSend(context, listener, errorListener, URL_LOGIN, map);
    }

    void Register(Context context, String login, String name, String email, String password, String spam, Response.Listener<String> listener) {
        Map<String, String> map = new HashMap<>();
        map.put("login", login);
        map.put("name", name);
        map.put("email", email);
        map.put("password", password);
        map.put("spam", spam);
        goSend(context, listener, getErrorListener(context), URL_REGISTER, map);
    }

    void getLessons(Context context, Response.Listener<String> listener, Response.ErrorListener errorListener, String group, String token) {
        Map<String, String> map = new HashMap<>();
        map.put("group", group);
        map.put("token", token);
        goSend(context, listener, errorListener, URL_LESSONS_GET, map);
    }

    void getGroups(Context context, String group) {
        Response.Listener<String> listener = response -> {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                for (int i = 1; i < 255; i++) {
                    try {
                        if (i == Integer.parseInt(group)) {
                            context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit()
                                    .putString("group_name", jsonResponse.getJSONObject(String.valueOf(i)).getString("name"))
                                    .apply();
                            break;
                        }
                    } catch (Exception e) {
                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };
        goSend(context, listener, null, URL_GET_GROUPS, new HashMap<>());
    }

    void GetGroupsToSpinner(Context context, Spinner spinner) {
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
        goSend(context, listener, getErrorListener(context), URL_GET_GROUPS, new HashMap<>());
    }

    void getNotifications(Context context, String group, String token, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        Map<String, String> map = new HashMap<>();
        map.put("group", group);
        map.put("token", token);
        goSend(context, listener, errorListener, URL_NOTIFICATIONS_GET, map);
    }

    void getLastVerInt(Context context, Response.Listener<String> listener) {
        goSend(context, listener, null, URL_GET_VER_INT, new HashMap<>());
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

