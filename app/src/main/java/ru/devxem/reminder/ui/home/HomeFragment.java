package ru.devxem.reminder.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import java.util.Objects;
import java.util.TimeZone;

import ru.devxem.reminder.MainActivity;
import ru.devxem.reminder.R;
import ru.devxem.reminder.api.Days;
import ru.devxem.reminder.api.GetNear;
import ru.devxem.reminder.api.Time;

public class HomeFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    private static TextView textView;
    @SuppressLint("StaticFieldLeak")
    private static TextView timetext;
    private static Date currentDate;
    private static int hour;
    private static int min;
    private static int sec;

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, final Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final SharedPreferences settings = Objects.requireNonNull(getContext()).getSharedPreferences("settings", Context.MODE_PRIVATE);
        textView = root.findViewById(R.id.remaintext);
        timetext = root.findViewById(R.id.timetext);
        TextView grouptext = root.findViewById(R.id.grouptext);
        final String id = MainActivity.getSss().get(0);
        final String group = MainActivity.getSss().get(1);
        final Context context = getContext();
        grouptext.setText("Ваша группа: " + group);
        new CountDownTimer(1000000000, 500) {

            @Override
            public void onTick(long millisUntilFinished) {
                currentDate = new Date();
                DateFormat timeFormatH = new SimpleDateFormat("HH", Locale.getDefault());
                DateFormat timeFormatM = new SimpleDateFormat("mm", Locale.getDefault());
                DateFormat timeFormatS = new SimpleDateFormat("ss", Locale.getDefault());


                hour = Integer.valueOf(timeFormatH.format(currentDate));
                min = Integer.valueOf(timeFormatM.format(currentDate));
                sec = Integer.valueOf(timeFormatS.format(currentDate));

                GetNear.reloadlessons(context, group, String.valueOf(id),hour,min);
                }

            @Override
            public void onFinish() {

            }
        }.start();
        return root;
    }
    @SuppressLint("SetTextI18n")
    public static void Update(int[] answer) {

        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.SATURDAY);
        c.setTimeZone(TimeZone.getDefault());
        c.setTime(currentDate);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String timeText = timeFormat.format(currentDate);

        textView.setText(Time.getRemain(answer[0], hour, answer[1], min, 60, sec));


        timetext.setText("Текущее время: " + timeText + "\n" + "День недели: " + Days.getDay(dayOfWeek - 1)+"\n"+"i: "+answer[2]+" a: "+answer[3]+" b: "+answer[4]+"\n"+answer[0]+":"+answer[1]);
    }
}