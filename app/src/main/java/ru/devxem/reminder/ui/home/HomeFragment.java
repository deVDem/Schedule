package ru.devxem.reminder.ui.home;

import android.annotation.SuppressLint;
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
import java.util.TimeZone;

import ru.devxem.reminder.MainActivity;
import ru.devxem.reminder.R;
import ru.devxem.reminder.api.Days;
import ru.devxem.reminder.api.Time;

public class HomeFragment extends Fragment {

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, final Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.remaintext);
        final TextView timetext = root.findViewById(R.id.timetext);
        final TextView grouptext = root.findViewById(R.id.grouptext);
        String group = MainActivity.getSss().get(1);
        grouptext.setText("Ваша группа: " + group);
        new CountDownTimer(1000000000, 250) {

            @Override
            public void onTick(long millisUntilFinished) {
                Date currentDate = new Date();
                DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                DateFormat timeFormatH = new SimpleDateFormat("HH", Locale.getDefault());
                DateFormat timeFormatM = new SimpleDateFormat("mm", Locale.getDefault());
                DateFormat timeFormatS = new SimpleDateFormat("ss", Locale.getDefault());
                String timeText = timeFormat.format(currentDate);

                Calendar c = Calendar.getInstance();
                c.setFirstDayOfWeek(Calendar.SATURDAY);
                c.setTimeZone(TimeZone.getDefault());
                c.setTime(currentDate);
                int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);


                timetext.setText("Текущее время: " + timeText + "\n" + "День недели: " + Days.getDay(dayOfWeek - 1));


                int hour = Integer.valueOf(timeFormatH.format(currentDate));
                int min = Integer.valueOf(timeFormatM.format(currentDate));
                int sec = Integer.valueOf(timeFormatS.format(currentDate));


                textView.setText(Time.getRemain(24, hour, 60, min, 60, sec));
            }

            @Override
            public void onFinish() {

            }
        }.start();
        return root;
    }
}