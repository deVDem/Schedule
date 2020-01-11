package ru.devdem.reminder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import ru.devdem.reminder.LessonsController.Lesson;

public class TimerFragment extends Fragment {

    private static boolean canGo;
    private TimeController mTimeController;
    private LessonsController mLessonsController;
    private String countString;
    private String counterString;
    private Context mContext;
    private Activity mActivity;
    private TextView countText;
    private TextView counterText;
    private TextView lessonNextText;
    private TextView lessonNext;
    private Thread mThread;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_timer, null);
        mContext = getContext();
        mTimeController = TimeController.get(mContext);
        mLessonsController = LessonsController.get(mContext);
        mActivity = getActivity();
        countText = view.findViewById(R.id.countText);
        counterText = view.findViewById(R.id.counterText);
        lessonNextText = view.findViewById(R.id.lessonNextText);
        lessonNext = view.findViewById(R.id.lessonNext);
        return view;
    }

    private Thread createThread() {
        return new Thread(null, () -> {
            while (canGo) {
                if (mLessonsController.getLessons().size() > 0)
                    try {
                        Date date = null;
                        Calendar now = Calendar.getInstance();
                        int hour = now.get(Calendar.HOUR_OF_DAY);
                        int minute = now.get(Calendar.MINUTE);
                        int second = now.get(Calendar.SECOND);
                        int day = now.get(Calendar.DAY_OF_WEEK);
                        switch (day) {
                            case Calendar.MONDAY:
                                day = 0;
                                break;
                            case Calendar.TUESDAY:
                                day = 1;
                                break;
                            case Calendar.WEDNESDAY:
                                day = 2;
                                break;
                            case Calendar.THURSDAY:
                                day = 3;
                                break;
                            case Calendar.FRIDAY:
                                day = 4;
                                break;
                            case Calendar.SATURDAY:
                                day = 5;
                                break;
                            case Calendar.SUNDAY:
                                day = 6;
                                break;
                        }
                        try {
                            date = new SimpleDateFormat("d HH:mm:ss", Locale.getDefault()).parse(day + 1 + " " + hour + ":" + minute + ":" + second);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        // 0 - урок или перемена
                        // 1 - номер урока которого считать
                        // 2 - номер след.урока
                        // 3 - состояние: ( 0 - до уроков всех, 1 - урок, 2 - перемена, 3 - конец всех уроков)
                        int[] params = mTimeController.getNumberlesson();
                        ArrayList<Lesson> mLessons = mLessonsController.getLessons();
                        switch (params[0]) {
                            case 0:
                                countString = mContext.getString(R.string.left_before_the_break);
                                counterString = mTimeController.getRemainText(mLessons.get(params[1]).getEnd(), Objects.requireNonNull(date));
                                break;
                            case 1:
                                countString = mContext.getString(R.string.left_before_lesson);
                                counterString = mTimeController.getRemainText(mLessons.get(params[2]).getStart(), Objects.requireNonNull(date));
                                break;
                        }
                        mActivity.runOnUiThread(() -> {
                            try {
                                if (params[3] == 3) {
                                    countText.setVisibility(View.INVISIBLE);
                                    counterText.setText(R.string.end_week);
                                    lessonNextText.setText(R.string.good_rest);
                                    lessonNext.setVisibility(View.INVISIBLE);
                                } else {
                                    countText.setVisibility(View.VISIBLE);
                                    lessonNext.setVisibility(View.VISIBLE);
                                    counterText.setText(counterString);
                                    lessonNextText.setText(R.string.next_lesson);
                                    countText.setText(countString);
                                    lessonNext.setText(mLessons.get(params[2]).getName());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        Thread.sleep(250);
                    } catch (InterruptedException ignored) {
                    }
            }
        }, "Background");
    }

    @Override
    public void onPause() {
        super.onPause();
        canGo = false;
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLessonsController.getLessons().size() > 0) {
            canGo = true;
            if (mThread == null)
                mThread = createThread();
            mThread.start();
        }
    }
}
