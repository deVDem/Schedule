package ru.devxem.reminder.api;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Groups {
    private static List<String> groups = new ArrayList<>();

    public static List<String> getGroups(Context context) {
        reloadGroups(context);
        return groups;
    }

    private static void reloadGroups(final Context context) {
        groups.clear();
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray array = jsonResponse.getJSONArray("groups");
                    for (int i=0;i<array.length();i++){
                        groups.add(array.getString(i));
                    }
                } catch (JSONException e) {
                    Error.setError(context, null);
                    e.printStackTrace();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                Error.setError(context, null);
            }
        };
        Groupss groupss = new Groupss(listener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(groupss);
    }
}


class Groupss extends StringRequest {
    private static final String LOGIN_REQUEST_URL = URLs.getGroups();
    private Map<String, String> params;

    Groupss(Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, LOGIN_REQUEST_URL, listener, errorListener);
        params = new HashMap<>();
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
