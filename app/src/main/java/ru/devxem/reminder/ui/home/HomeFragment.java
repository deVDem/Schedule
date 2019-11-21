package ru.devxem.reminder.ui.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import ru.devxem.reminder.MainActivity;
import ru.devxem.reminder.R;
import ru.devxem.reminder.TimeNotification;
import ru.devxem.reminder.api.Error;
import ru.devxem.reminder.api.GetNear;
import ru.devxem.reminder.api.Time;

public class HomeFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    private static TextView textView;
    @SuppressLint("StaticFieldLeak")
    private static TextView lefttext;
    private static Date currentDate;
    private static int hour;
    private static int min;
    private static int sec;
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private String id;
    private String group;
    private static boolean isEnabled = true;
    @SuppressLint("StaticFieldLeak")
    private static SwipeRefreshLayout swipeRefreshLayout;
    @SuppressLint("StaticFieldLeak")
    private static Activity activity;
    private SharedPreferences preferences;


    private Runnable doBackgroundThreadProcessing = new Runnable() {
        public void run() {
            backgroundThreadProcessing();
        }
    };

    public static void updateRefresh(final boolean set) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(set);
            }
        });
    }

    public static void noInfo() {
        lefttext.setText("");
        textView.setText("Нет данных");
    }

    public static void setEnabled(boolean iab) {
        isEnabled = iab;
    }

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, final Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        textView = root.findViewById(R.id.remaintext);
        lefttext = root.findViewById(R.id.textView);
        activity = getActivity();
        context = getContext();
        preferences = Objects.requireNonNull(context).getSharedPreferences("settings", Context.MODE_PRIVATE);
        id = MainActivity.getSss().get(0);
        group = MainActivity.getSss().get(1);
        Thread threads = new Thread(null, doBackgroundThreadProcessing,
                "Main");
        threads.start();
        swipeRefreshLayout = root.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Calendar c = Calendar.getInstance();
                c.setTimeZone(TimeZone.getDefault());
                c.setTime(currentDate);
                int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                GetNear.reloadlessons(context, group, String.valueOf(id), String.valueOf(dayOfWeek), 2);
            }
        });
        /*MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                AdView mAdView = root.findViewById(R.id.adViewHome);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            }
        });*/

        return root;
    }

    private void backgroundThreadProcessing() {
        while (true) {
            if (isEnabled) {
                try {
                    Thread.sleep(250);
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
                    Update(GetNear.updatelessons(context, hour, min, String.valueOf(dayOfWeek), sec));
                } catch (InterruptedException e) {
                    Error.setError(context, id);
                    e.printStackTrace();
                }
            } else {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TimeNotification.cancel(context);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @SuppressLint("SetTextI18n")
    private void Update(final int[] answer) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Calendar c = Calendar.getInstance();
                c.setTimeZone(TimeZone.getDefault());
                c.setTime(currentDate);
                if (answer == null) return;
                if (answer[5] != 2) {
                    String remain;
                    if (!preferences.getBoolean("millis", false)) {
                        remain = Time.getRemain(answer[0], hour, answer[1], min, 0, sec, -1);
                    } else {
                        Date dateNow = new Date();
                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat formatForDateNow = new SimpleDateFormat("SSS");
                        remain = Time.getRemain(answer[0], hour, answer[1], min, 0, sec, Integer.parseInt(formatForDateNow.format(dateNow)));
                    }
                    textView.setText(remain);

                    String string = context.getString(R.string.remain) + context.getString(R.string.pause);

                    if (answer[5] == 1) {
                        string = context.getString(R.string.remain) + context.getString(R.string.lesson);
                    }
                    lefttext.setText(string);
                    if (preferences.getBoolean("notification", true))
                        TimeNotification.notify(context, string + ": " + remain, context.getString(R.string.app_name), 1);
                } else {
                    if (preferences.getBoolean("notification", true))
                        TimeNotification.notify(context, context.getString(R.string.noles) + ": " + context.getString(R.string.rest), context.getString(R.string.app_name), 1);
                    lefttext.setText(context.getString(R.string.noles));
                    textView.setText(context.getString(R.string.rest));
                }
                if (!preferences.getBoolean("notification", true)) TimeNotification.cancel(context);
            }
        });
    }
}