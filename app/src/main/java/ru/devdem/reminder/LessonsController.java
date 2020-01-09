package ru.devdem.reminder;

import android.content.Context;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static java.util.UUID.randomUUID;

public class LessonsController {


    private static LessonsController sLessonsController;

    // сам контроллер
    private static ArrayList<Lesson> mLessons = new ArrayList<>();
    private Context mContext;

    private LessonsController(Context context) {
        mContext = context.getApplicationContext();
        //TODO: удалить код
        for (int i = 0; i < 7; i++) {
            for (int j = 1; j <= 8; j++) {
                this.addLesson("Урок", String.valueOf(j), i, new Date(), new Date());
            }
        }
    }

    static LessonsController get(Context context) {
        if (sLessonsController == null) sLessonsController = new LessonsController(context);
        return sLessonsController;
    }

    void addLesson(String name, String numberText, int day, Date start, Date end) {
        Lesson l = new Lesson();
        l.setName(name);
        l.setNumberText(numberText);
        l.setDay(day);
        l.setStart(start);
        l.setNumber(mLessons.size());
        l.setEnd(end);
        mLessons.add(l);
    }

    public void removeLessons() {
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
