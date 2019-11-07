package ru.devxem.reminder.api;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

class SaveLoad {
    static void SaveAll(String arg, SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Alllessons", arg);
        editor.apply();
    }

    static String LoadAll(SharedPreferences preferences) {
        return preferences.getString("Alllessons", null);
    }

    static void Save(String arg, SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("lessons", arg);
        editor.apply();
    }

    static String[][] Load(Context context, String day) {
        String[][] answer = new String[6][5];
        String data = context.getSharedPreferences("settings", Context.MODE_PRIVATE).getString("lessons", null);
        if (data != null) {
            try {
                JSONObject jsonResponses = new JSONObject(data);
                boolean noles = jsonResponses.getBoolean("no_les");
                if (noles) {
                    answer[0][0] = "true";
                    return answer;
                }
                JSONObject jsonResponse = jsonResponses.getJSONObject(day);
                JSONObject onetwo = jsonResponse.getJSONObject("1-2");
                answer[0][0] = onetwo.getString("text");
                answer[0][1] = onetwo.getString("hour");
                answer[0][2] = onetwo.getString("time");
                answer[0][3] = onetwo.getString("end_h");
                answer[0][4] = onetwo.getString("end_m");
                JSONObject three = jsonResponse.getJSONObject("3");
                answer[1][0] = three.getString("text");
                answer[1][1] = three.getString("hour");
                answer[1][2] = three.getString("time");
                answer[1][3] = three.getString("end_h");
                answer[1][4] = three.getString("end_m");
                JSONObject four = jsonResponse.getJSONObject("4");
                answer[2][0] = four.getString("text");
                answer[2][1] = four.getString("hour");
                answer[2][2] = four.getString("time");
                answer[2][3] = four.getString("end_h");
                answer[2][4] = four.getString("end_m");
                JSONObject fivesix = jsonResponse.getJSONObject("5-6");
                answer[3][0] = fivesix.getString("text");
                answer[3][1] = fivesix.getString("hour");
                answer[3][2] = fivesix.getString("time");
                answer[3][3] = fivesix.getString("end_h");
                answer[3][4] = fivesix.getString("end_m");
                if (!jsonResponse.isNull("7")) {
                    JSONObject seven = jsonResponse.getJSONObject("7");
                    answer[4][0] = seven.getString("text");
                    answer[4][1] = seven.getString("hour");
                    answer[4][2] = seven.getString("time");
                    answer[4][3] = seven.getString("end_h");
                    answer[4][4] = seven.getString("end_m");
                    JSONObject eight = jsonResponse.getJSONObject("8");
                    answer[5][0] = eight.getString("text");
                    answer[5][1] = eight.getString("hour");
                    answer[5][2] = eight.getString("time");
                    answer[5][3] = eight.getString("end_h");
                    answer[5][4] = eight.getString("end_m");
                }
                if (!jsonResponse.isNull("7-8")) {
                    JSONObject seven = jsonResponse.getJSONObject("7-8");
                    answer[4][0] = seven.getString("text");
                    answer[4][1] = seven.getString("hour");
                    answer[4][2] = seven.getString("time");
                    answer[4][3] = seven.getString("end_h");
                    answer[4][4] = seven.getString("end_m");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Error.setError(context, null);
            }
        }
        return answer;
    }

    static void SaveNotes(String json, SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("notifications", json);
        editor.apply();
    }

    static ArrayList<ArrayList<String>> LoadNotes(SharedPreferences preferences) {
        ArrayList<String> ids = new ArrayList<>();
        ArrayList<String> date = new ArrayList<>();
        ArrayList<String> text = new ArrayList<>();
        ArrayList<String> head = new ArrayList<>();
        ArrayList<ArrayList<String>> answer = new ArrayList<>();

        String json = preferences.getString("notifications", null);
        if (json == null) return null;
        try {
            JSONObject notes = new JSONObject(json);
            for (int i = 1; i <= 50; i++) {
                try {
                    JSONObject temp = notes.getJSONObject(String.valueOf(i));
                    ids.add(temp.getString("id"));
                    date.add(temp.getString("date"));
                    head.add(temp.getString("head"));
                    text.add(temp.getString("text"));
                } catch (Exception e) {
                    break;
                }
            }
            answer.add(0, ids);
            answer.add(1, date);
            answer.add(2, head);
            answer.add(3, text);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return answer;
    }
}
