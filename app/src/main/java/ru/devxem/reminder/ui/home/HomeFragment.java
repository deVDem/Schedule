package ru.devxem.reminder.ui.home;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import ru.devxem.reminder.MainActivity;
import ru.devxem.reminder.R;

public class HomeFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.remaintext);
        String group = MainActivity.getSss().get(1);
        final int[] i = {0};
        new CountDownTimer(1000000000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                textView.setText(String.valueOf(i[0]));
                i[0]++;
            }

            @Override
            public void onFinish() {

            }
        }.start();
        return root;
    }
}