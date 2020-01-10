package ru.devdem.reminder;

import android.content.Context;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

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

    static LessonsController get(Context context) {
        if (sLessonsController == null) sLessonsController = new LessonsController(context);
        return sLessonsController;
    }

    void addLesson(String name, String numberText, int day, String cab, Date start, Date end) {
        Lesson l = new Lesson();
        l.setName(name);
        l.setNumberText(numberText);
        l.setDay(day);
        l.setStart(start);
        l.setNumber(mLessons.size());
        l.setEnd(end);
        l.setCab(cab);
        mLessons.add(l);
    }

    void parseLessons(String response) {
        removeLessons();
        try {
            JSONObject object = new JSONObject(response);
            int all = object.getInt("all");
            for (int i = 0; i < all; i++) {
                JSONObject jsonObject = object.getJSONObject(String.valueOf(i));
                String name = jsonObject.getString("text");
                String cab = jsonObject.getString("cab");
                String numberText = jsonObject.getString("n");
                int day = jsonObject.getInt("day");
                Date start = new SimpleDateFormat("h:mm:ss", Locale.getDefault()).parse(jsonObject.getString("start"));
                Date end = new SimpleDateFormat("h:mm:ss", Locale.getDefault()).parse(jsonObject.getString("end"));
                addLesson(name, numberText, day, cab, start, end);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        saveLessons(response);
    }

    void loadLessons() {
        parseLessons(mContext.getSharedPreferences(NAME_PREFS_JSONS, Context.MODE_PRIVATE).getString("lessons", null));
    }

    void saveLessons(String json) {
        mContext.getSharedPreferences(NAME_PREFS_JSONS, Context.MODE_PRIVATE).edit().putString("lessons", json).apply();
    }

    void removeLessons() {
        mLessons.clear();
    }

    ArrayList<Lesson> getLessons() {
        return mLessons;
    }

    // объект урока
    public class Lesson {
        private UUID mId;
        private int mNumber;
        private String mNumberText;
        private int mDay;
        private String mName;
        private Date mStart;
        private Date mEnd;
        private String mCab;

        public String getCab() {
            return mCab;
        }

        public void setCab(String cab) {
            mCab = cab;
        }


        Lesson() {
            this(randomUUID());
        }

        Lesson(UUID uuid) {
            mId = uuid;
        }

        String getNumberText() {
            return mNumberText;
        }

        void setNumberText(String numberText) {
            mNumberText = numberText;
        }

        public UUID getId() {
            return mId;
        }

        public int getNumber() {
            return mNumber;
        }

        void setNumber(int number) {
            mNumber = number;
        }

        int getDay() {
            return mDay;
        }

        void setDay(int day) {
            mDay = day;
        }

        public String getName() {
            return mName;
        }

        public void setName(String name) {
            mName = name;
        }

        Date getStart() {
            return mStart;
        }

        void setStart(Date start) {
            mStart = start;
        }

        Date getEnd() {
            return mEnd;
        }

        void setEnd(Date end) {
            mEnd = end;
        }
    }
}
