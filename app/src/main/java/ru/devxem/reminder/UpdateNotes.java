package ru.devxem.reminder;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import ru.devxem.reminder.api.GetNear;
import ru.devxem.reminder.api.Time;

public class UpdateNotes extends Service {
    Date currentDate;
    int hour;
    int min;
    int sec;
    Context context;
    SharedPreferences preferences;
    CountDownTimer timer;

    public UpdateNotes() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        timer = null;
        TimeNotification.cancel(context);
        TimeNotification.cancel(context);
        TimeNotification.cancel(context);
        TimeNotification.cancel(context);
        TimeNotification.cancel(context);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    void UpdateTime(final int[] answer) {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getDefault());
        c.setTime(currentDate);
        if (answer == null) return;
        if (answer[5] != 2) {
            String remain;
            remain = Time.getRemain(answer[0], hour, answer[1], min, 0, sec, -1);
            String string = context.getString(R.string.remain) + context.getString(R.string.pause);
            if (answer[5] == 1) {
                string = context.getString(R.string.remain) + context.getString(R.string.lesson);
            }
            if (preferences.getBoolean("notification", true))
                TimeNotification.notify(context, string + ": " + remain, context.getString(R.string.app_name), 1);
        } else {
            if (preferences.getBoolean("notification", true))
                TimeNotification.notify(context, context.getString(R.string.noles) + ": " + context.getString(R.string.rest), context.getString(R.string.app_name), 1);
        }
        if (!preferences.getBoolean("notification", true)) TimeNotification.cancel(context);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        preferences = getSharedPreferences("settings", MODE_PRIVATE);
        if (timer == null) {
            timer = new CountDownTimer(1000000000, 250) {

                @Override
                public void onTick(long millisUntilFinished) {
                    context = getApplicationContext();
                    currentDate = new Date();
                    DateFormat timeFormatH = new SimpleDateFormat("HH", Locale.getDefault());
                    DateFormat timeFormatM = new SimpleDateFormat("mm", Locale.getDefault());
                    DateFormat timeFormatS = new SimpleDateFormat("ss", Locale.getDefault());


                    hour = Integer.valueOf(timeFormatH.format(currentDate));
                    min = Integer.valueOf(timeFormatM.format(currentDate));
                    sec = Integer.valueOf(timeFormatS.format(currentDate));

                    Calendar c = Calendar.getInstance();
                    c.setTimeZone(TimeZone.getDefault());
                    c.setTime(currentDate);
                    int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                    UpdateTime(GetNear.updatelessons(context, hour, min, String.valueOf(dayOfWeek), sec));
                }

                @Override
                public void onFinish() {

                }
            }.start();
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

