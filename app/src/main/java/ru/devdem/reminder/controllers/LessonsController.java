package ru.devdem.reminder.controllers;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import ru.devdem.reminder.object.Lesson;

import static java.util.UUID.randomUUID;

public class LessonsController {


    private static LessonsController sLessonsController;
    private static String NAME_PREFS_JSONS = "jsondata";

    // сам контроллер
    private static ArrayList<Lesson> mLessons = new ArrayList<>();
    private Context mContext;

    private LessonsController(Context context) {
        mContext = context.getApplicationContext();
    }

    public static LessonsController get(Context context) {
        if (sLessonsController == null) sLessonsController = new LessonsController(context);
        return sLessonsController;
    }

    private void addLesson(String name, String numberText, int day, String cab, Date start, Date end, boolean zamena, String desc) {
        Lesson l = new Lesson();
        l.setName(name);
        l.setNumberText(numberText);
        l.setDay(day);
        l.setStart(start);
        l.setNumber(mLessons.size());
        l.setEnd(end);
        l.setCab(cab);
        l.setZamena(zamena);
        l.setDescription(desc);
        mLessons.add(l);
    }

    private static final String TAG = "LessonsController";

    public Thread parseLessonsAsync(String response) {
        Thread thread = new Thread(null, () -> {
            Log.d(TAG, "parseLessons: " + response);
            removeLessons();
            try {
                JSONObject object = new JSONObject(response);
                if (object.isNull("error") && !object.isNull("response")) { // TODO: разделить условия
                    JSONObject jsonResponse = object.getJSONObject("response");
                    JSONArray jsonLessons = jsonResponse.getJSONArray("lessons");
                    for (int i = 0; i < jsonLessons.length(); i++) {
                        JSONObject jsonLesson = jsonLessons.getJSONObject(i);
                        String name = jsonLesson.getString("name");
                        String cab = jsonLesson.getString("cab");
                        String numberText = jsonLesson.getString("n");
                        int day = jsonLesson.getInt("day");
                        Date start = new SimpleDateFormat("d HH:mm:ss", Locale.getDefault()).parse(day + 1 + " " + jsonLesson.getString("start"));
                        Date end = new SimpleDateFormat("d HH:mm:ss", Locale.getDefault()).parse(day + 1 + " " + jsonLesson.getString("end"));
                        boolean isZamena = jsonLesson.getInt("zamena") == 1;
                        String desc = jsonLesson.getString("description");
                        if (desc.equals("null")) desc = "";
                        addLesson(name, numberText, day, cab, start, end, isZamena, desc);
                    }
                    saveLessons(response);
                } else {
                    // ошибка
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return thread;
    }

    public void parseLessons(String response) {
        Thread thread = new Thread(null, () -> {
            Log.d(TAG, "parseLessons: " + response);
            removeLessons();
            try {
                JSONObject object = new JSONObject(response);
                if (object.isNull("error") && !object.isNull("response")) { // TODO: разделить условия
                    JSONObject jsonResponse = object.getJSONObject("response");
                    JSONArray jsonLessons = jsonResponse.getJSONArray("lessons");
                    for (int i = 0; i < jsonLessons.length(); i++) {
                        JSONObject jsonLesson = jsonLessons.getJSONObject(i);
                        String name = jsonLesson.getString("name");
                        String cab = jsonLesson.getString("cab");
                        String numberText = jsonLesson.getString("n");
                        int day = jsonLesson.getInt("day");
                        Date start = new SimpleDateFormat("d HH:mm:ss", Locale.getDefault()).parse(day + 1 + " " + jsonLesson.getString("start"));
                        Date end = new SimpleDateFormat("d HH:mm:ss", Locale.getDefault()).parse(day + 1 + " " + jsonLesson.getString("end"));
                        boolean isZamena = jsonLesson.getInt("zamena") == 1;
                        String desc = jsonLesson.getString("description");
                        if (desc.equals("null")) desc = "";
                        addLesson(name, numberText, day, cab, start, end, isZamena, desc);
                    }
                    saveLessons(response);
                } else {
                    // ошибка
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public void loadLessons() {
        parseLessons(mContext.getSharedPreferences(NAME_PREFS_JSONS, Context.MODE_PRIVATE).getString("lessons", null));
    }

    private void saveLessons(String json) {
        mContext.getSharedPreferences(NAME_PREFS_JSONS, Context.MODE_PRIVATE).edit().putString("lessons", json).apply();
    }

    public void removeLessons() {
        mContext.getSharedPreferences(NAME_PREFS_JSONS, Context.MODE_PRIVATE).edit().clear().apply();
        mLessons.clear();
    }

    public ArrayList<Lesson> getLessons() {
        return mLessons;
    }

    public void destroy() {
        sLessonsController = null;
    }

    // объект урока

}
