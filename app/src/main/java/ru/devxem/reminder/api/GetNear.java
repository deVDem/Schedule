package ru.devxem.reminder.api;

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

import ru.devxem.reminder.ui.home.HomeFragment;

public class GetNear {

    public static int[] reloadlessons(final Context context, String group, final String id, final int hour, final int min) {
        final String[][] lessons = new String[6][5];
        final int[] answer = new int[6];
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean noles = jsonResponse.getBoolean("no_les");
                    if (noles) {
                        return;
                    }
                    JSONObject onetwo = jsonResponse.getJSONObject("1-2");
                    lessons[0][0] = onetwo.getString("text");
                    lessons[0][1] = onetwo.getString("hour");
                    lessons[0][2] = onetwo.getString("time");
                    lessons[0][3] = onetwo.getString("end_h");
                    lessons[0][4] = onetwo.getString("end_m");
                    JSONObject three = jsonResponse.getJSONObject("3");
                    lessons[1][0] = three.getString("text");
                    lessons[1][1] = three.getString("hour");
                    lessons[1][2] = three.getString("time");
                    lessons[1][3] = three.getString("end_h");
                    lessons[1][4] = three.getString("end_m");
                    JSONObject four = jsonResponse.getJSONObject("4");
                    lessons[2][0] = four.getString("text");
                    lessons[2][1] = four.getString("hour");
                    lessons[2][2] = four.getString("time");
                    lessons[2][3] = four.getString("end_h");
                    lessons[2][4] = four.getString("end_m");
                    JSONObject fivesix = jsonResponse.getJSONObject("5-6");
                    lessons[3][0] = fivesix.getString("text");
                    lessons[3][1] = fivesix.getString("hour");
                    lessons[3][2] = fivesix.getString("time");
                    lessons[3][3] = fivesix.getString("end_h");
                    lessons[3][4] = fivesix.getString("end_m");
                    JSONObject seven = jsonResponse.getJSONObject("7");
                    lessons[4][0] = seven.getString("text");
                    lessons[4][1] = seven.getString("hour");
                    lessons[4][2] = seven.getString("time");
                    lessons[4][3] = seven.getString("end_h");
                    lessons[4][4] = seven.getString("end_m");
                    JSONObject eight = jsonResponse.getJSONObject("8");
                    lessons[5][0] = eight.getString("text");
                    lessons[5][1] = eight.getString("hour");
                    lessons[5][2] = eight.getString("time");
                    lessons[5][3] = eight.getString("end_h");
                    lessons[5][4] = eight.getString("end_m");
                    if (lessons[1][1] == null) {
                        Error.setError(context, id);
                    }
                    int a = 24;
                    int i;
                    for (i = 0; i <= 5; i++) {
                        int r = Integer.parseInt(lessons[i][1]) - hour;
                        if ((Integer.parseInt(lessons[i][1]) < hour || hour > Integer.parseInt(lessons[i][3])) || (Integer.parseInt(lessons[i][2]) < min  && Integer.parseInt(lessons[i][4]) > min)) {
                            answer[5] = 1;
                        }
                        else {
                            answer[5] = 0;
                        }
                        if(r<a) {
                            a=r;
                        }
                        else break;
                    }
                    if (answer[5]==0) {
                        answer[0] = Integer.parseInt(lessons[i][1]);
                        answer[1] = Integer.parseInt(lessons[i][2]);
                        answer[2] = i;
                        answer[3] = a;

                    } else {
                        answer[0] = Integer.parseInt(lessons[i][3]);
                        answer[1] = Integer.parseInt(lessons[i][4]);
                        answer[2] = i;
                        answer[3] = a;
                    }
                    HomeFragment.Update(answer);
                } catch (JSONException e) {
                    Error.setError(context, id);
                    e.printStackTrace();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                Error.setError(context, id);
            }
        };
        GetLessons groupss = new GetLessons(listener, errorListener, id, group);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(groupss);
        return answer;
    }
}

class GetLessons extends StringRequest {
    private static final String LOGIN_REQUEST_URL = URLs.getLess();
    private Map<String, String> params;

    GetLessons(Response.Listener<String> listener, Response.ErrorListener errorListener, String id, String group) {
        super(Method.POST, LOGIN_REQUEST_URL, listener, errorListener);
        params = new HashMap<>();
        params.put("id", id);
        params.put("group", group);
        params.put("day", "2");
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}