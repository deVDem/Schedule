package ru.devxem.reminder.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import ru.devxem.reminder.MainActivity;
import ru.devxem.reminder.R;
import ru.devxem.reminder.api.Days;
import ru.devxem.reminder.api.Error;
import ru.devxem.reminder.api.GetNear;
import ru.devxem.reminder.api.Time;

public class HomeFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    private static TextView textView;
    @SuppressLint("StaticFieldLeak")
    private static TextView timetext;
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

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, final Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        textView = root.findViewById(R.id.remaintext);
        timetext = root.findViewById(R.id.timetext);
        lefttext = root.findViewById(R.id.textView);
        TextView grouptext = root.findViewById(R.id.grouptext);
        context = getContext();
        id = MainActivity.getSss().get(0);
        group = MainActivity.getSss().get(1);
        grouptext.setText("Ваша группа: " + group);
        Thread threads = new Thread(null, doBackgroundThreadProcessing,
                "Background");
        threads.start();

        return root;
    }
    public static void setEnabled(boolean iab) {
        isEnabled = iab;
    }
    private Runnable doBackgroundThreadProcessing = new Runnable() {
        public void run() {
            backgroundThreadProcessing();
        }
    };
    private void backgroundThreadProcessing() {
        while (isEnabled) {
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

                GetNear.reloadlessons(context, group, String.valueOf(id),hour,min, String.valueOf(dayOfWeek), sec);
                //GetNear.reloadlessons(context, group, String.valueOf(id),hour,min, "2");
            } catch (InterruptedException e) {
                Error.setError(context, id);
                e.printStackTrace();
            }
        }
    }
    @SuppressLint("SetTextI18n")
    public static void Update(int[] answer) {

        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getDefault());
        c.setTime(currentDate);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String timeText = timeFormat.format(currentDate);

        if(answer[5]!=2) {
            textView.setText(Time.getRemain(answer[0], hour, answer[1], min, 0, sec));

            String string = context.getString(R.string.remain)+context.getString(R.string.pause);

            if (answer[5]==1) {
                string = context.getString(R.string.remain)+context.getString(R.string.lesson);
            }
            lefttext.setText(string);

        }
        else {
            lefttext.setText(context.getString(R.string.noles));
            textView.setText(context.getString(R.string.rest));
        }
        timetext.setText("Текущее время: " + timeText + "\n" + "День недели: " + Days.getDay(dayOfWeek - 1)+"\n"+"i: "+answer[2]+" a: "+answer[3]+" b: "+answer[4]+"\n"+answer[0]+":"+answer[1]);


    }
}