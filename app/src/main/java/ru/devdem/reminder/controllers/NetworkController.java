package ru.devdem.reminder.controllers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ru.devdem.reminder.BuildConfig;
import ru.devdem.reminder.R;

public class NetworkController {
    private static final String TAG = "NetworkController";
    private static NetworkController sNetworkController;
    String URL_ROOT;
    private static RequestQueue queue;
    String URL_GET_VER_INT = "https://api.devdem.ru/apps/schedule/version.php";

    private NetworkController() {
        if (BuildConfig.DEBUG)
            URL_ROOT = "https://api.devdem.ru/apps/schedule/debug/";
        else
            URL_ROOT = "https://api.devdem.ru/apps/schedule/" + BuildConfig.VERSION_CODE + "/";
    }

    public static NetworkController get() {
        if (sNetworkController == null) sNetworkController = new NetworkController();
        return sNetworkController;
    }

    public Response.ErrorListener getErrorListener(Context context) {
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
        SendRequest sendRequest = new SendRequest(listener, errorListener, URL, map, Request.Method.POST);
        if (queue == null) queue = Volley.newRequestQueue(context);
        queue.add(sendRequest);
    }

    public void addNotification(Context context, Response.Listener<String> listener, Response.ErrorListener errorListener, String token, String group, String title, String message, String image) {
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("group", group);
        map.put("title", title);
        map.put("message", message);
        if (image != null) map.put("image", image);
        //goSend(context, listener, errorListener, URL_ADD_NOTIFICATION, map);
    }

    public void checkSubs(Context context, Response.Listener<String> listener, Response.ErrorListener errorListener, String packageName, String subId, String token, String user_id) {
        Map<String, String> map = new HashMap<>();
        map.put("packageName", packageName);
        map.put("subscriptionId", subId);
        map.put("token", token);
        map.put("user_id", user_id);
        //goSend(context, listener, errorListener, URL_CHECK_SUB, map);
    }

    public void addGroup(Context context, Response.Listener<String> listener, Response.ErrorListener errorListener, String token, String name, String city, String building, String description, String image) {
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("name", name);
        map.put("city", city);
        map.put("building", building);
        map.put("description", description);
        if (image != null) map.put("image", image);
        //goSend(context, listener, errorListener, URL_ADD_GROUP, map);
    }


    public void editProfile(Context context, Response.Listener<String> listener, Response.ErrorListener errorListener, String name, String email, String login, String token) {
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("email", email);
        map.put("login", login);
        map.put("token", token);
        //goSend(context, listener, errorListener, URL_UPDATE_PROFILE, map);
    }

    public void Login(Context context, String login, String password, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        Map<String, String> map = new HashMap<>();
        map.put("action", "login");
        map.put("login", login);
        map.put("password", password);
        goSend(context, listener, errorListener, URL_ROOT, map);
    }

    public void Register(Context context, String login, String name, String email, String password, String spam, Response.Listener<String> listener) {
        Map<String, String> map = new HashMap<>();
        /*login, email, password, names, spam*/
        map.put("action", "register");
        map.put("login", login);
        map.put("names", name);
        map.put("email", email);
        map.put("password", password);
        map.put("spam", spam);
        goSend(context, listener, getErrorListener(context), URL_ROOT, map);
    }

    public void getLessons(Context context, Response.Listener<String> listener, Response.ErrorListener errorListener, String group, String token) {
        Map<String, String> map = new HashMap<>();
        map.put("action", "getLessons");
        map.put("groupId", group);
        map.put("token", token);
        Log.d(TAG, "getLessons: groupId:" + group);
        Log.d(TAG, "getLessons: token:" + token);
        goSend(context, listener, errorListener, URL_ROOT, map);
    }

    public void joinToGroup(Context context, Response.Listener<String> listener, Response.ErrorListener errorListener, String group_id, String token) {
        Map<String, String> map = new HashMap<>();
        map.put("action", "joinGroup");
        map.put("groupId", group_id);
        map.put("token", token);
        goSend(context, listener, errorListener, URL_ROOT, map);
    }

    public void getGroups(Context context, Response.Listener<String> listener, Response.ErrorListener errorListener, String token, String[] params) {
        // name, city, building, confirmed
        Map<String, String> map = new HashMap<>();
        map.put("action", "getGroups");
        map.put("token", token);
        if (params != null) {
            if (params[0] != null)
                map.put("name", params[0]);
            if (params[1] != null)
                map.put("city", params[1]);
            if (params[2] != null)
                map.put("building", params[2]);
            if (params[3] != null)
                map.put("confirmed", params[3]);
            if (params[4] != null)
                map.put("groupId", params[4]);
            if (params[5] != null)
                map.put("full", params[5]);
        }
        goSend(context, listener, errorListener, URL_ROOT, map);
    }

    public void getGroup(Context context, Response.ErrorListener errorListener, String group, String token) {
        Map<String, String> map = new HashMap<>();
        map.put("action", "getGroups");
        map.put("groupId", group);
        map.put("token", token);
        Response.Listener<String> listener = response -> {
            Log.d(TAG, "getGroup: " + response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.isNull("error")) {
                    if (!jsonObject.isNull("response")) {
                        JSONObject jsonGroup = jsonObject.getJSONObject("response").getJSONArray("group_list").getJSONObject(0);
                        if (jsonGroup != null) {
                            context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit()
                                    .putString("group_name", jsonGroup.getString("name"))
                                    .putString("group_city", jsonGroup.getString("city"))
                                    .putString("group_building", jsonGroup.getString("building"))
                                    .putString("group_description", jsonGroup.getString("description"))
                                    .putString("group_urlImage", jsonGroup.getString("imageId"))
                                    .putString("group_confirmed", jsonGroup.getString("confirmed"))
                                    .putString("group_author_id", jsonGroup.getString("ownerId"))
                                    .putString("group_date_created", jsonGroup.getString("date_created"))
                                    .apply();
                        }
                    } else {
                        // ... code
                    }
                } else {
                    // ... code
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };
        goSend(context, listener, errorListener, URL_ROOT, map);
    }

    public void getNotifications(Context context, String group, String token, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        Map<String, String> map = new HashMap<>();
        map.put("group", group);
        map.put("token", token);
        //goSend(context, listener, errorListener, URL_NOTIFICATIONS_GET, map);
    }

    public void getLastVerInt(Context context, Response.Listener<String> listener) {
        Map<String, String> map = new HashMap<>();
        goSend(context, listener, null, URL_GET_VER_INT, map);
    }

    private static class SendRequest extends StringRequest {
        private static final String TAG = "SendRequest";
        private Map<String, String> mParams;

        SendRequest(Response.Listener<String> listener, Response.ErrorListener errorListener, String url, Map<String, String> params, int method) {
            super(method, url, listener, errorListener);
            Log.d(TAG, "SendRequest: Method: " + (BuildConfig.DEBUG ? Method.GET : Method.POST));
            mParams = params;
        }

        @Override
        public Map<String, String> getParams() {
            return mParams;
        }
    }
}

