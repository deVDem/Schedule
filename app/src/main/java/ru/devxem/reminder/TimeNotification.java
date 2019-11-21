package ru.devxem.reminder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.util.Objects;

import ru.devxem.reminder.api.Error;

public class TimeNotification {
    private static final String NOTIFICATION_TAG = "Time";
    private static final String CHANNEL_ID = "CHANNEL_ID";

    public static void notify(final Context context,
                              final String text, final String title, final int number) {
        try {
            Intent intent = new Intent(context, SplashScreen.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_stat_time)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setTicker(text)
                    .setContentIntent(pendingIntent)
                    .setNumber(number)
                    .setAutoCancel(false);
            notifys(context, builder.build());
        } catch (Exception e) {
            Error.setErr(context, e.toString(), context.getSharedPreferences("settings", Context.MODE_PRIVATE).getString("email", null));
        }
    }

    private static void notifys(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        createChannelIfNeeded(nm);
        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
        Objects.requireNonNull(nm).notify(NOTIFICATION_TAG, 0, notification);
    }

    private static void createChannelIfNeeded(NotificationManager manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(notificationChannel);
        }
    }

    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Objects.requireNonNull(nm).cancel(NOTIFICATION_TAG, 0);
    }
}