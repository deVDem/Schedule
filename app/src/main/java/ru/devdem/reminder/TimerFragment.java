package ru.devdem.reminder;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TimerFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_timer, null);
        TextView countText = view.findViewById(R.id.countText);
        TextView counterText = view.findViewById(R.id.counterText);
        TextView lessonNextText = view.findViewById(R.id.lessonNextText);
        TextView lessonNext = view.findViewById(R.id.lessonNext);
        return view;
    }
}
