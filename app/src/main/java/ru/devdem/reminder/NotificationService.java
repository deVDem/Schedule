package ru.devdem.reminder;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class NotificationService extends Service {
    private static String countString;
    private static String counterString;
    private NotificationUtils mNotificationUtils;
    private TimeController mTimeController;
    private LessonsController mLessonsController;
    private Thread sThread;
    private boolean canGo = true;
    private static final String TAG = "NotificationService";

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationUtils = new NotificationUtils(this);
        mTimeController = TimeController.get(this);
        mLessonsController = LessonsController.get(this);
        createThread();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!sThread.isInterrupted() && !sThread.isAlive()) sThread.start();
        else createThread();
        return START_REDELIVER_INTENT;
    }

    private void createThread() {
        sThread = new Thread(null, () -> {
            while (canGo) {
                if (mLessonsController.getLessons().size() > 0) {
                    try {
                        Date date = null;
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
                        try {
                            date = new SimpleDateFormat("d HH:mm:ss", Locale.getDefault()).parse(day + 1 + " " + hour + ":" + minute + ":" + second);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        // 0 - урок или перемена
                        // 1 - номер урока которого считать
                        // 2 - номер след.урока
                        // 3 - состояние: ( 0 - до уроков всех, 1 - урок, 2 - перемена, 3 - конец всех уроков)
                        int[] params = mTimeController.getNumberlesson();
                        ArrayList<LessonsController.Lesson> mLessons = mLessonsController.getLessons();
                        switch (params[0]) {
                            case 0:
                                countString = getApplicationContext().getString(R.string.left_before_the_break) + ": " + mTimeController.getRemainText(mLessons.get(params[1]).getEnd(), Objects.requireNonNull(date));
                                counterString = getApplicationContext().getString(R.string.next) + ": " + mLessons.get(params[2]).getName();
                                break;
                            case 1:
                                if (mLessons.get(params[2]).getDay() == day && mLessons.get(params[1]).getDay() == day) {
                                    countString = getApplicationContext().getString(R.string.next) + ": " + mLessons.get(params[2]).getName();
                                    counterString = getApplicationContext().getString(R.string.through) + " " + mTimeController.getRemainText(mLessons.get(params[2]).getStart(), Objects.requireNonNull(date));
                                } else {
                                    countString = getApplicationContext().getString(R.string.lessons_over_today);
                                    counterString = getString(R.string.good_rest);
                                }
                                break;
                        }
                        if (params[3] == 3) {
                            countString = getString(R.string.end_week);
                            counterString = getString(R.string.good_rest);
                        }
                        NotificationCompat.Builder builder = mNotificationUtils.getTimerNotification(counterString, countString);
                        Notification notification = builder.build();
                        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
                        if (canGo) {
                            mNotificationUtils.getManager().notify(103, notification);
                            startForeground(103, notification);
                        }
                        Thread.sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            stopSelf();
        }, "Timer Notification Background");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sThread.interrupt();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE);
        } else {
            mNotificationUtils.getManager().cancel(103);
        }
        canGo = false;
        if (getSharedPreferences("settings", MODE_PRIVATE).getBoolean("notification", true)) {
            NotificationCompat.Builder builder = mNotificationUtils.getTimerNotification(getApplicationContext().getString(R.string.click_to_restart), getApplicationContext().getString(R.string.service_stopped));
            Notification notification = builder.build();
            Intent reloadIntent = new Intent(getApplicationContext(), SplashActivity.class);
            reloadIntent.setAction("ru.devdem.reminder.reloadservice");
            notification.contentIntent = PendingIntent.getActivity(this, 0, reloadIntent, 0);
            mNotificationUtils.getManager().notify(103, notification);
            sendBroadcast(new Intent("YouWillNeverKillMe"));
        }
        sThread = null;
        Log.d(TAG, "onDestroy: ddd");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
