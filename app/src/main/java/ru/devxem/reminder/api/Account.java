package ru.devxem.reminder.api;

import android.app.Dialog;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ru.devxem.reminder.FirstActivity;

public class Account {

    public static void Register(final Context context, String login, String name, String email, String password, String group, String spam, String ver, final Dialog dialog) {
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse.getBoolean("success")) {
                        FirstActivity.Registered();
                    } else {
                        dialog.cancel();
                        Toast.makeText(context, "Ошибка регистрации. Такой аккаунт существует либо произошла внутренняя ошибка сервера.", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Error.setErr(context, e.toString(), context.getSharedPreferences("settings", Context.MODE_PRIVATE).getString("email", null));
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Произошла ошибка: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        SendRegisterRequest sendRegisterRequest = new SendRegisterRequest(login, name, email, password, group, spam, ver, listener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(sendRegisterRequest);
    }

    public static void Login(final Context context, String login, String password, String ver) {
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    FirstActivity.reloadDialog(false);
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse.getBoolean("success")) {
                        FirstActivity.Logined(response);
                    } else {
                        FirstActivity.reloadDialog(false);
                        Toast.makeText(context, "Ошибка входа", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Error.setErr(context, e.toString(), context.getSharedPreferences("settings", Context.MODE_PRIVATE).getString("email", null));
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                FirstActivity.reloadDialog(false);
                Toast.makeText(context, "Произошла ошибка: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        SendLoginRequest sendLoginRequest = new SendLoginRequest(login, password, listener, errorListener, ver);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(sendLoginRequest);
    }

    private static class SendRegisterRequest extends StringRequest {
        private Map<String, String> params;

        SendRegisterRequest(String login, String name, String email, String password, String group, String spam, String ver, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(Method.POST, URLs.getRegister(), listener, errorListener);
            params = new HashMap<>();
            params.put("name", name);
            params.put("email", email);
            params.put("login", login);
            params.put("password", password);
            params.put("groups", group);
            params.put("spam", spam);
            params.put("v", ver);
        }

        @Override
        public Map<String, String> getParams() {
            return params;
        }
    }

    private static class SendLoginRequest extends StringRequest {
        private Map<String, String> params;

        SendLoginRequest(String login, String password, Response.Listener<String> listener, Response.ErrorListener errorListener, String ver) {
            super(Method.POST, URLs.getLogin(), listener, errorListener);
            params = new HashMap<>();
            params.put("login", login);
            params.put("password", password);
            params.put("v", ver);
        }

        @Override
        public Map<String, String> getParams() {
            return params;
        }
    }
}
