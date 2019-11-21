package ru.devxem.reminder.api;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import ru.devxem.reminder.R;
import ru.devxem.reminder.ui.notifications.NotificationsFragment;

public class GetNotes {
    private static RequestQueue queue;

    public static void updateNotes(Context context, String group, int reason) {
        reloadNotes(group, context);

    }

    private static void reloadNotes(final String group, final Context context) {
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                SaveLoad.SaveNotes(response, context.getSharedPreferences("settings", Context.MODE_PRIVATE));
                NotificationsFragment.Update(SaveLoad.LoadNotes(context.getSharedPreferences("settings", Context.MODE_PRIVATE)));
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, context.getString(R.string.error) + ": " + error.toString(), Toast.LENGTH_LONG).show();
            }
        };

        GetNotesConnect connect = new GetNotesConnect(listener, errorListener, group);
        if (queue == null) queue = Volley.newRequestQueue(context);
        queue.add(connect);
    }
}


class GetNotesConnect extends StringRequest {
    private static final String LOGIN_REQUEST_URL = URLs.getNotes();
    private Map<String, String> params;

    GetNotesConnect(Response.Listener<String> listener, Response.ErrorListener errorListener, String group) {
        super(Request.Method.POST, LOGIN_REQUEST_URL, listener, errorListener);
        params = new HashMap<>();
        params.put("group", group);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}