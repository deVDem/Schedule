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
import java.util.Date;
import java.util.Locale;

import ru.devxem.reminder.MainActivity;
import ru.devxem.reminder.R;
import ru.devxem.reminder.api.Time;

public class HomeFragment extends Fragment {

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.remaintext);
        final TextView timetext = root.findViewById(R.id.timetext);
        String group = MainActivity.getSss().get(1);
        final int[] i = {0};
        new CountDownTimer(1000000000, 250) {

            @Override
            public void onTick(long millisUntilFinished) {
                Date currentDate = new Date();
                DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                DateFormat timeFormatH = new SimpleDateFormat("HH", Locale.getDefault());
                DateFormat timeFormatM = new SimpleDateFormat("mm", Locale.getDefault());
                DateFormat timeFormatS = new SimpleDateFormat("ss", Locale.getDefault());
                String timeText = timeFormat.format(currentDate);
                timetext.setText("Текущее время: " + timeText);


                int hour = Integer.valueOf(timeFormatH.format(currentDate));
                int min = Integer.valueOf(timeFormatM.format(currentDate));
                int sec = Integer.valueOf(timeFormatS.format(currentDate));


                textView.setText(Time.getRemain(24,hour,60,min,60,sec));
            }

            @Override
            public void onFinish() {

            }
        }.start();
        return root;
    }
}