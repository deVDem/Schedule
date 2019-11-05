package ru.devxem.reminder.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
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
import java.util.Objects;

import ru.devxem.reminder.MainActivity;
import ru.devxem.reminder.ui.dashboard.DashboardFragment;
import ru.devxem.reminder.ui.home.HomeFragment;

public class GetNear {
    private static RequestQueue queue;

    private static String checknull(String integer) {
        String answer;
        if (integer.equals("0")) answer = "00";
        else answer = integer;

        return answer;
    }

    public static void reloadlessons(final Context context, String group, final String id, final String day, final int reason) {
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                SaveLoad.Save(response, context.getSharedPreferences("settings", Context.MODE_PRIVATE));
                context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit().putString("day", day).apply();
                HomeFragment.updateRefresh(false);
                HomeFragment.setEnabled(true);
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Ошибка: " + error.toString(), Toast.LENGTH_LONG).show();

                HomeFragment.noInfo();
                HomeFragment.setEnabled(false);
                switch (reason) {
                    case 0:
                        Error.setError(context, id);
                        break;
                    case 1:
                        Error.noInfo(context, true);
                        break;
                    case 2:
                        HomeFragment.setEnabled(true);
                        break;
                }
                HomeFragment.updateRefresh(false);
            }
        };
        GetLessons groupss = new GetLessons(listener, errorListener, id, group, day);
        if (queue == null) queue = Volley.newRequestQueue(context);
        queue.add(groupss);
    }

    public static ArrayAdapter<String> updateLessons(String group, Context context, int reason) {
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        String data = SaveLoad.LoadAll(context.getSharedPreferences("settings", Context.MODE_PRIVATE));
        if (data == null || reason == 1) {
            getAllLessons(group, context);
        } else
            stringArrayAdapter = parseLessons(context);
        return stringArrayAdapter;
    }

    private static void getAllLessons(final String group, final Context context) {
        final String[] save = new String[1];
        save[0] = "";
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.contains("null") && !response.contains("\"no_les\":true")) {
                    save[0] = response;
                    SaveLoad.SaveAll(save[0], context.getSharedPreferences("settings", Context.MODE_PRIVATE));
                    DashboardFragment.reloadLess(updateLessons(group, context, 0));
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
        GetLessons groupss = new GetLessons(listener, errorListener, "2", group, "8");
        if (queue == null) queue = Volley.newRequestQueue(context);
        queue.add(groupss);

    }

    private static ArrayAdapter<String> parseLessons(final Context context) {
        final ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);

        for (int i = 1; i <= 7; i++) {
            try {
                String response = SaveLoad.LoadAll(context.getSharedPreferences("settings", Context.MODE_PRIVATE));
                JSONObject jsonResponse = new JSONObject(response);
                boolean noles = jsonResponse.getBoolean("no_les");
                if (noles) {
                    return null;
                }
                JSONObject jsonObjectday = jsonResponse.getJSONObject(String.valueOf(i));
                stringArrayAdapter.add(Days.getDay(i - 1));
                JSONObject onetwo = jsonObjectday.getJSONObject("1-2");
                stringArrayAdapter.add(onetwo.getString("n") + ": " + onetwo.getString("text") + " "
                        + checknull(onetwo.getString("hour")) + ":" + checknull(onetwo.getString("time")) +
                        "->" + checknull(onetwo.getString("end_h")) + ":" + checknull(onetwo.getString("end_m")));
                JSONObject three = jsonObjectday.getJSONObject("3");
                stringArrayAdapter.add(three.getString("n") + ": " + three.getString("text") + " "
                        + checknull(three.getString("hour")) + ":" + checknull(three.getString("time")) +
                        "->" + checknull(three.getString("end_h")) + ":" + checknull(three.getString("end_m")));
                JSONObject four = jsonObjectday.getJSONObject("4");
                stringArrayAdapter.add(four.getString("n") + ": " + checknull(four.getString("text")) + " "
                        + checknull(four.getString("hour")) + ":" + checknull(four.getString("time")) +
                        "->" + checknull(four.getString("end_h")) + ":" + checknull(four.getString("end_m")));
                JSONObject fivesix = jsonObjectday.getJSONObject("5-6");
                stringArrayAdapter.add(fivesix.getString("n") + ": " + fivesix.getString("text") + " "
                        + checknull(fivesix.getString("hour")) + ":" + checknull(fivesix.getString("time")) +
                        "->" + checknull(fivesix.getString("end_h")) + ":" + checknull(fivesix.getString("end_m")));
                if (!jsonObjectday.isNull("7")) {
                    JSONObject seven = jsonObjectday.getJSONObject("7");
                    stringArrayAdapter.add(seven.getString("n") + ": " + seven.getString("text") + " "
                            + checknull(seven.getString("hour")) + ":" + checknull(seven.getString("time")) +
                            "->" + checknull(seven.getString("end_h")) + ":" + checknull(seven.getString("end_m")));
                    JSONObject eight = jsonObjectday.getJSONObject("8");
                    stringArrayAdapter.add(eight.getString("n") + ": " + eight.getString("text") + " "
                            + checknull(eight.getString("hour")) + ":" + checknull(eight.getString("time")) +
                            "->" + checknull(eight.getString("end_h")) + ":" + checknull(eight.getString("end_m")));
                } else if (!jsonObjectday.isNull("7-8")) {
                    JSONObject seveneight = jsonObjectday.getJSONObject("7-8");
                    stringArrayAdapter.add(seveneight.getString("n") + ": " + seveneight.getString("text") + " "
                            + checknull(seveneight.getString("hour")) + ":" + checknull(seveneight.getString("time")) +
                            "->" + checknull(seveneight.getString("end_h")) + ":" + checknull(seveneight.getString("end_m")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return stringArrayAdapter;
    }

    public static int[] updatelessons(Context context, int hour, int min, String day, int sec) {
        String[][] lessons = SaveLoad.Load(context, day);
        String id = MainActivity.getSss().get(0);
        String group = MainActivity.getSss().get(1);
        final int[] answer = new int[6];
        HomeFragment.setEnabled(false);
        SharedPreferences preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        if ((lessons == null || preferences.getString("day", null) == null || !Objects.requireNonNull(preferences.getString("day", null)).equals(day))) {
            HomeFragment.updateRefresh(true);
            GetNear.reloadlessons(context, group, String.valueOf(id), String.valueOf(day), 1);
            return null;
        } else {
            if (Objects.requireNonNull(lessons)[0][0].equals("true")) {
                answer[5] = 2;
                return answer;
            }
            if (lessons[1][1] == null) {
                Error.setError(context, id);
                return null;
            }
            int i = 0;
            int a = 3;
            int h = Integer.parseInt(lessons[i][1]);
            int m = Integer.parseInt(lessons[i][2]);
            int he = Integer.parseInt(lessons[i][3]);
            int me = Integer.parseInt(lessons[i][4]);
            int hp = 0;
            int mp = 0;
            if (lessons[4][0] != null) a = 4;
            if (lessons[5][0] != null) a = 5;
            for (i = 0; i <= a; i++) {
                h = Integer.parseInt(lessons[i][1]);
                m = Integer.parseInt(lessons[i][2]);
                if (i != a) {
                    hp = Integer.parseInt(lessons[i + 1][1]);
                    mp = Integer.parseInt(lessons[i + 1][2]);
                } else {

                    hp = Integer.parseInt(lessons[i][1]);
                    mp = Integer.parseInt(lessons[i][2]);
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
                Date nachalo;
                Date nachalol;
                Date konec;
                Date now;
                try {
                    nachalo = curFormater.parse(date1Str);
                    konec = curFormater.parse(date2Str);
                    now = curFormater.parse(nowStr);
                    nachalol = curFormater.parse(date3Str);
                    if (i == 0 && Objects.requireNonNull(nachalo).after(now)) {
                        answer[5] = 1;
                        break;
                    }
                    if (i == a && Objects.requireNonNull(konec).before(now)) {
                        answer[5] = 2;
                        break;
                    }
                    if (Objects.requireNonNull(nachalol).after(now) && Objects.requireNonNull(konec).before(now)) {
                        answer[5] = 1;
                        break;
                    }
                    if (Objects.requireNonNull(nachalo).before(now) && Objects.requireNonNull(konec).after(now)) {
                        answer[5] = 0;
                        break;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (answer[5] == 1) {
                answer[0] = hp;
                answer[1] = mp;
                answer[2] = i;

            } else {
                answer[0] = he;
                answer[1] = me;
                answer[2] = i;
            }

            HomeFragment.setEnabled(true);
        }
        return answer;
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