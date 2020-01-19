package ru.devdem.reminder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.downloader.Progress;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class NotificationUtils extends ContextWrapper {

    public static final String ANDROID_CHANNEL_ID = "ru.devdem.reminder.NOTIFICATIONS";
    public static final String DOWNLOAD_CHANNEL_ID = "ru.devdem.reminder.NOTIFICATIONS_DOWNLOAD";
    public static final String TIMER_CHANNEL_ID = "ru.devdem.reminder.NOTIFICATIONS_TIMER";
    public static final String ANDROID_CHANNEL_NAME = "Main";
    public static final String DOWNLOAD_CHANNEL_NAME = "Downloads";
    public static final String TIMER_CHANNEL_NAME = "Timer";
    private NotificationManager mManager;

    public NotificationUtils(Context base) {
        super(base);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            createChannels();
    }

    public NotificationCompat.Builder getTimerNotification(String remain, String remainText) {
        return new NotificationCompat.Builder(getApplicationContext(), TIMER_CHANNEL_ID)
                .setContentTitle(remainText)
                .setContentText(remain)
                .setSmallIcon(R.drawable.ic_notification_timer)
                .setAutoCancel(true);
    }

    public NotificationCompat.Builder getDownloadChannelNotification(Progress progress) {
        float mbCurrent = (float) progress.currentBytes / 1048576;
        float mbTotal = (float) progress.totalBytes / 1048576;
        float proc = (float) progress.currentBytes / (float) progress.totalBytes * 100;
        DecimalFormat f = new DecimalFormat("##.00", DecimalFormatSymbols.getInstance(Locale.getDefault()));
        String procString = f.format(proc);
        if (proc < 1.0) procString = "0" + procString;
        String mbCurrentString = f.format(mbCurrent);
        if (mbCurrent < 1.0) mbCurrentString = "0" + mbCurrentString;
        String mbTotalString = f.format(mbTotal);
        if (mbTotal < 1.0) mbTotalString = "0" + mbTotalString;
        return new NotificationCompat.Builder(getApplicationContext(), DOWNLOAD_CHANNEL_ID)
                .setContentTitle(getResources().getString(R.string.downloadTitle))
                .setContentText(procString + "% | " + mbCurrentString + "/" + mbTotalString + " MB")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setProgress((int) progress.totalBytes, (int) progress.currentBytes, false)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setVibrate(null)
                .setAutoCancel(false);
    }

    public NotificationCompat.Builder getNewUpdateChannelNotification() {
        return new NotificationCompat.Builder(getApplicationContext(), ANDROID_CHANNEL_ID)
                .setContentTitle(getResources().getString(R.string.a_new_version_of_the_app_is_available))
                .setContentText(getResources().getString(R.string.click_to_download))
                .setSmallIcon(R.drawable.ic_notification_timer)
                .setAutoCancel(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannels() {
        NotificationChannel downloadChannel = new NotificationChannel(DOWNLOAD_CHANNEL_ID, DOWNLOAD_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
        downloadChannel.enableLights(false);
        downloadChannel.enableVibration(false);
        downloadChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        NotificationChannel timerChannel = new NotificationChannel(TIMER_CHANNEL_ID, TIMER_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
        timerChannel.enableLights(false);
        timerChannel.enableVibration(false);
        timerChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        NotificationChannel androidChannel = new NotificationChannel(ANDROID_CHANNEL_ID,
                ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        androidChannel.enableLights(false);
        androidChannel.enableVibration(true);
        androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        getManager().createNotificationChannel(androidChannel);
        getManager().createNotificationChannel(downloadChannel);
        getManager().createNotificationChannel(timerChannel);
    }

    NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }
}
