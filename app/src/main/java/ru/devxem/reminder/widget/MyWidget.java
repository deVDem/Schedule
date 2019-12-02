package ru.devxem.reminder.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import ru.devxem.reminder.R;
import ru.devxem.reminder.api.GetNear;
import ru.devxem.reminder.api.Time;

public class MyWidget extends AppWidgetProvider {

    private static Date currentDate;
    final String LOG_TAG = "myLogs";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.d(LOG_TAG, "onEnabled");
    }

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager,
                         final int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.d(LOG_TAG, "onUpdate " + Arrays.toString(appWidgetIds));
        new CountDownTimer(2147483647, 250) {

            @Override
            public void onTick(long millisUntilFinished) {
                for (int appWidgetID : appWidgetIds)

                    updateWidget(context, appWidgetManager, appWidgetID);
            }

            @Override
            public void onFinish() {

            }
        }.start();

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.d(LOG_TAG, "onDeleted " + Arrays.toString(appWidgetIds));
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.d(LOG_TAG, "onDisabled");
    }

    public void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetID) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        currentDate = new Date();
        DateFormat timeFormatH = new SimpleDateFormat("HH", Locale.getDefault());
        DateFormat timeFormatM = new SimpleDateFormat("mm", Locale.getDefault());
        DateFormat timeFormatS = new SimpleDateFormat("ss", Locale.getDefault());


        int hour = Integer.valueOf(timeFormatH.format(currentDate));
        int min = Integer.valueOf(timeFormatM.format(currentDate));
        int sec = Integer.valueOf(timeFormatS.format(currentDate));

        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getDefault());
        c.setTime(currentDate);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        int[] answer = GetNear.updatelessons(context, hour, min, String.valueOf(dayOfWeek), sec, true);
        if (answer == null) return;
        if (answer[5] != 2) {
            String string = context.getString(R.string.remain) + context.getString(R.string.pause);

            if (answer[5] == 1) {
                string = context.getString(R.string.remain) + context.getString(R.string.lesson);
            }
            views.setTextViewText(R.id.remain, string);
            views.setTextViewText(R.id.remaincount, Time.getRemain(answer[0], hour, answer[1], min, 0, sec, -1));
        } else {
            views.setTextViewText(R.id.remaincount, context.getString(R.string.rest));
            views.setTextViewText(R.id.remain, context.getString(R.string.noles));
        }
        appWidgetManager.updateAppWidget(appWidgetID, views);
    }

}
