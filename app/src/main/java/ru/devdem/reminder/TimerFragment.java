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

    private TextView mCountText;
    private TextView mCounterText;
    private TextView mLessonNextText;
    private TextView mLessonNext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_timer, null);
        mCountText = view.findViewById(R.id.countText);
        mCounterText = view.findViewById(R.id.counterText);
        mLessonNextText = view.findViewById(R.id.lessonNextText);
        mLessonNext = view.findViewById(R.id.lessonNext);
        return view;
    }
}
