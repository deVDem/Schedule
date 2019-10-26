package ru.devxem.reminder.api;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class SendInfo extends StringRequest {
    private static final String LOGIN_REQUEST_URL = URLs.getInfos();
    private Map<String, String> params;

    public SendInfo(Response.Listener<String> listener, Response.ErrorListener errorListener, String name, String email, int group, boolean spam) {
        super(Request.Method.POST, LOGIN_REQUEST_URL, listener, errorListener);
        params = new HashMap<>();
        params.put("name", name);
        params.put("email", email);
        params.put("group", String.valueOf(group));
        params.put("spam", String.valueOf(spam));
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}