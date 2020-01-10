package ru.devdem.reminder;

import android.content.Context;
import android.view.View;

class TimeController {

    private static TimeController sTimeController;
    private Context mContext;
    private LessonsController mLessonsController;

    private TimeController(Context context) {
        mContext = context;
        mLessonsController = LessonsController.get(context);
    }

    public static TimeController TimeController(Context context) {
        if (sTimeController == null) sTimeController = new TimeController(context);
        return sTimeController;
    }

    private void startCount(View remainText, View remain, View nextLessonText, View nextLesson) {

    }
}
