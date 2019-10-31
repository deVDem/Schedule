package ru.devxem.reminder.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ru.devxem.reminder.ui.dashboard.DashboardFragment;
import ru.devxem.reminder.ui.home.HomeFragment;

public class GetNear {
    private static RequestQueue queue;

    public static String checknull(String integer) {
        String answer = null;
        if (integer.equals("0")) answer = "00";
        else answer = integer;

        return answer;
    }

    public static void reloadlessons(final Context context, String group, final String id, final int hour, final int min, String day, final int sec) {
        final String[][] lessons = new String[6][5];
        final int[] answer = new int[6];
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean noles = jsonResponse.getBoolean("no_les");
                    if (noles) {
                        answer[5] = 2;
                        HomeFragment.Update(answer);
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
                    int a = 3;
                    if (!jsonResponse.isNull("7")) {
                        a = 5;
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
                    }
                    if (lessons[1][1] == null) {
                        Error.setError(context, id);
                    }
                    int i = 0;
                    int h = Integer.parseInt(lessons[i][1]);
                    int m = Integer.parseInt(lessons[i][2]);
                    int he = Integer.parseInt(lessons[i][3]);
                    int me = Integer.parseInt(lessons[i][4]);
                    int hp = Integer.parseInt(lessons[i + 1][3]);
                    int mp = Integer.parseInt(lessons[i + 1][3]);
                    for (i = 0; i <= a; i++) {
                        h = Integer.parseInt(lessons[i][1]);
                        m = Integer.parseInt(lessons[i][2]);
                        if (a == 5) {
                            if (i != 5) {
                                hp = Integer.parseInt(lessons[i + 1][1]);
                                mp = Integer.parseInt(lessons[i + 1][2]);
                            }
                        }
                        if (a == 3) {
                            if (i != 3) {
                                hp = Integer.parseInt(lessons[i + 1][1]);
                                mp = Integer.parseInt(lessons[i + 1][2]);
                            }
                        }
                        he = Integer.parseInt(lessons[i][3]);
                        me = Integer.parseInt(lessons[i][4]);
                        // 0 - урок
                        // 1 - перемена
                        String date1Str = h + ":" + m + ":00";
                        String date2Str = he + ":" + me + ":00";
                        String date3Str = hp + ":" + mp + ":00";
                        String nowStr = hour + ":" + min + ":" + sec;
                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat curFormater = new SimpleDateFormat("HH:mm:ss");
                        Date nachalo = null;
                        Date nachalol = null;
                        Date konec = null;
                        Date now = null;
                        if (i == 0 && hour < h) {
                            answer[5] = 1;
                            break;
                        }
                        try {
                            nachalo = curFormater.parse(date1Str);
                            konec = curFormater.parse(date2Str);
                            now = curFormater.parse(nowStr);
                            nachalol = curFormater.parse(date3Str);
                            if (i == a && konec.before(now)) {
                                answer[5] = 2;
                                break;
                            }
                            if (nachalo.before(now) && konec.after(now)) {
                                answer[5] = 0;
                                break;
                            } else if (nachalol.after(now) && konec.after(now)) {
                                answer[5] = 1;
                                break;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    if (answer[5] == 1) {
                        answer[0] = h;
                        answer[1] = m;
                        answer[2] = i;

                    } else {
                        answer[0] = he;
                        answer[1] = me;
                        answer[2] = i;
                    }
                    HomeFragment.Update(answer);
                } catch (JSONException e) {
                    if (!e.toString().contains("No value for 7")) {
                        Error.setError(context, id);
                        HomeFragment.setEnabled(false);
                        e.printStackTrace();
                    }
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                Error.setError(context, id);
                HomeFragment.setEnabled(false);
            }
        };
        GetLessons groupss = new GetLessons(listener, errorListener, id, group, day);
        if (queue == null) queue = Volley.newRequestQueue(context);
        queue.add(groupss);
    }

    public static void parseLessons(String group, final String id, Context context) {
        final ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        for (int i = 2; i <= 7; i++) {
            final int finalI = i - 1;
            Response.Listener<String> listener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    JSONObject jsonResponse = null;
                    try {
                        jsonResponse = new JSONObject(response);
                        boolean noles = jsonResponse.getBoolean("no_les");
                        if (noles) {
                            return;
                        }
                        stringArrayAdapter.add(Days.getDay(finalI));
                        JSONObject onetwo = jsonResponse.getJSONObject("1-2");
                        stringArrayAdapter.add(onetwo.getString("n") + ": " + onetwo.getString("text") + " "
                                + checknull(onetwo.getString("hour")) + ":" + checknull(onetwo.getString("time")) +
                                "->" + checknull(onetwo.getString("end_h")) + ":" + checknull(onetwo.getString("end_m")));
                        JSONObject three = jsonResponse.getJSONObject("3");
                        stringArrayAdapter.add(three.getString("n") + ": " + three.getString("text") + " "
                                + checknull(three.getString("hour")) + ":" + checknull(three.getString("time")) +
                                "->" + checknull(three.getString("end_h")) + ":" + checknull(three.getString("end_m")));
                        JSONObject four = jsonResponse.getJSONObject("4");
                        stringArrayAdapter.add(four.getString("n") + ": " + checknull(four.getString("text")) + " "
                                + checknull(four.getString("hour")) + ":" + checknull(four.getString("time")) +
                                "->" + checknull(four.getString("end_h")) + ":" + checknull(four.getString("end_m")));
                        JSONObject fivesix = jsonResponse.getJSONObject("5-6");
                        stringArrayAdapter.add(fivesix.getString("n") + ": " + fivesix.getString("text") + " "
                                + checknull(fivesix.getString("hour")) + ":" + checknull(fivesix.getString("time")) +
                                "->" + checknull(fivesix.getString("end_h")) + ":" + checknull(fivesix.getString("end_m")));
                        if (!jsonResponse.isNull("7")) {
                            JSONObject seven = jsonResponse.getJSONObject("7");
                            stringArrayAdapter.add(seven.getString("n") + ": " + seven.getString("text") + " "
                                    + checknull(seven.getString("hour")) + ":" + checknull(seven.getString("time")) +
                                    "->" + checknull(seven.getString("end_h")) + ":" + checknull(seven.getString("end_m")));
                            JSONObject eight = jsonResponse.getJSONObject("8");
                            stringArrayAdapter.add(eight.getString("n") + ": " + eight.getString("text") + " "
                                    + checknull(eight.getString("hour")) + ":" + checknull(eight.getString("time")) +
                                    "->" + checknull(eight.getString("end_h")) + ":" + checknull(eight.getString("end_m")));
                        } else if (!jsonResponse.isNull("7-8")) {
                            JSONObject seveneight = jsonResponse.getJSONObject("7-8");
                            stringArrayAdapter.add(seveneight.getString("n") + ": " + seveneight.getString("text") + " "
                                    + checknull(seveneight.getString("hour")) + ":" + checknull(seveneight.getString("time")) +
                                    "->" + checknull(seveneight.getString("end_h")) + ":" + checknull(seveneight.getString("end_m")));
                        }
                        DashboardFragment.reloadLess(stringArrayAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            };
            GetLessons groupss = new GetLessons(listener, errorListener, id, group, String.valueOf(i));
            if (queue == null) queue = Volley.newRequestQueue(context);
            queue.add(groupss);
        }
    }
}


class GetLessons extends StringRequest {
    private static final String LOGIN_REQUEST_URL = URLs.getLess();
    private Map<String, String> params;

    GetLessons(Response.Listener<String> listener, Response.ErrorListener errorListener, String id, String group, String day) {
        super(Method.POST, LOGIN_REQUEST_URL, listener, errorListener);
        params = new HashMap<>();
        params.put("id", id);
        params.put("group", group);
        params.put("day", day);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}