package ru.devdem.reminder;

import android.content.Context;
import android.content.SharedPreferences;

class TimeController {

    private static TimeController sTimeController;
    private Context mContext;
    private LessonsController mLessonsController;
    private SharedPreferences mPreferences;
    private boolean go = false;

    private TimeController(Context context) {
        String NAME_PREFS = "settings";
        mContext = context;
        mPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        mLessonsController = LessonsController.get(context);
    }

    public static TimeController TimeController(Context context) {
        if (sTimeController == null) sTimeController = new TimeController(context);
        return sTimeController;
    }
}
