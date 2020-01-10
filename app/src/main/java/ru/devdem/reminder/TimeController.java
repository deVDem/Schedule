package ru.devdem.reminder;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ru.devdem.reminder.LessonsController.Lesson;

class TimeController {

    private static TimeController sTimeController;
    private Context mContext;
    private LessonsController mLessonsController;
    private SharedPreferences mPreferences;

    private TimeController(Context context) {
        String NAME_PREFS = "settings";
        mContext = context;
        mPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        mLessonsController = LessonsController.get(context);
    }

    static TimeController get(Context context) {
        if (sTimeController == null) sTimeController = new TimeController(context);
        return sTimeController;
    }

    String getRemainText(Date date1, Date date2) {
        long milliseconds = date1.getTime() - date2.getTime();
        int hours = (int) (milliseconds / (60 * 60 * 1000));
        milliseconds -= (60 * 60 * 1000) * hours;
        int minutes = (int) (milliseconds / (60 * 1000));
        milliseconds -= (60 * 1000) * minutes;
        int seconds = (int) (milliseconds / (1000));
        return hours + ":" + minutes + ":" + seconds;
    }

    int[] getNumberlesson() {
        ArrayList<Lesson> mLessons = mLessonsController.getLessons();
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);
        int day = now.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.MONDAY:
                day = 0;
                break;
            case Calendar.TUESDAY:
                day = 1;
                break;
            case Calendar.WEDNESDAY:
                day = 2;
                break;
            case Calendar.THURSDAY:
                day = 3;
                break;
            case Calendar.FRIDAY:
                day = 4;
                break;
            case Calendar.SATURDAY:
                day = 5;
                break;
            case Calendar.SUNDAY:
                day = 6;
                break;
        }
        Date date = null;
        try {
            date = new SimpleDateFormat("d HH:mm:ss", Locale.getDefault()).parse(day + 1 + " " + hour + ":" + minute + ":" + second);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 0 - урок или перемена
        // 1 - номер урока которого считать
        // 2 - номер след.урока
        // 3 - состояние: ( 0 - до уроков всех, 1 - урок, 2 - перемена, 3 - конец всех уроков)
        int[] answer = {-1, -1, -1, -1};
        for (int i = 0; i < mLessons.size(); i++) {
            Lesson lesson = mLessons.get(i);
            int lessonDay = lesson.getDay();
            Date lessonStart = lesson.getStart();
            Date lessonEnd = lesson.getEnd();
            if (lessonDay == day && date != null) {
                if (date.after(lessonStart) && date.before(lessonEnd)) {
                    answer[0] = 0;
                    answer[1] = i;
                    if (i + 1 < mLessons.size()) answer[2] = i + 1;
                    answer[3] = 1;
                    break;
                }
                if (date.after(lessonEnd) && i + 1 < mLessons.size() && date.before(mLessons.get(i + 1).getStart())) {
                    answer[0] = 1;
                    answer[1] = i + 1;
                    answer[2] = i + 1;
                    answer[3] = 2;
                    break;
                }
                if (date.before(lessonStart) && i == 0) {
                    answer[0] = 1;
                    answer[1] = i;
                    answer[2] = i;
                    answer[3] = 0;
                    break;
                }
                if (date.after(lessonEnd) && i + 1 == mLessons.size()) {
                    answer[0] = 0;
                    answer[1] = 0;
                    answer[2] = 0;
                    answer[3] = 3;
                    break;
                }
            }
        }
        return answer;
    }

    void destroy() {
        sTimeController = null;
    }
}
