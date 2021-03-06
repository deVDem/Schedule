package ru.devdem.reminder.ui.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import ru.devdem.reminder.BuildConfig;
import ru.devdem.reminder.controllers.LessonsController;
import ru.devdem.reminder.object.Lesson;
import ru.devdem.reminder.controllers.ObjectsController;
import ru.devdem.reminder.R;
import ru.devdem.reminder.controllers.TimeController;

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
    private MainActivity mMainActivity;
    private int[] lastParams;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_timer, null);
        mContext = requireContext();
        mTimeController = TimeController.get(mContext);
        mLessonsController = LessonsController.get(mContext);
        mActivity = getActivity();
        mMainActivity = (MainActivity) mActivity;
        countText = view.findViewById(R.id.countText);
        counterText = view.findViewById(R.id.counterText);
        lessonNextText = view.findViewById(R.id.lessonNextText);
        lessonNext = view.findViewById(R.id.lessonNext);
        if (!ObjectsController.getLocalUserInfo(mActivity.getSharedPreferences("settings", Context.MODE_PRIVATE)).isPro()) {
            AdView adView = new AdView(mContext);
            if (!BuildConfig.DEBUG)
                adView.setAdUnitId("ca-app-pub-7389415060915567/7081052515");
            else adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
            RelativeLayout relativeLayout = view.findViewById(R.id.relativeTimer);
            adView.setAdSize(getAdSize());
            relativeLayout.addView(adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }
        return view;
    }

    private AdSize getAdSize() {
        Display display = requireActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;
        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(mContext, adWidth);
    }

    private Thread createThread() {
        return new Thread(null, () -> {
            while (canGo) {
                try {
                    if (mLessonsController.getLessons().size() > 0) {
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
                                counterString = mTimeController.getRemainTextFull(mLessons.get(params[1]).getEnd(), Objects.requireNonNull(date));
                                break;
                            case 1:
                                countString = mContext.getString(R.string.left_before_lesson);
                                counterString = mTimeController.getRemainTextFull(mLessons.get(params[2]).getStart(), Objects.requireNonNull(date));
                                break;
                        }
                        int finalDay = day;
                        mActivity.runOnUiThread(() -> {
                            try {
                                lessonNextText.setVisibility(View.VISIBLE);
                                Lesson mLesson = mLessons.get(params[1]);
                                Lesson mLessonNext = mLessons.get(params[2]);
                                if (params[3] == 3) {
                                    countText.setVisibility(View.INVISIBLE);
                                    counterText.setText(R.string.end_week);
                                    lessonNextText.setText(R.string.good_rest);
                                    lessonNext.setVisibility(View.INVISIBLE);
                                } else if (mLessonNext.getDay() != finalDay && mLesson.getDay() != finalDay) {
                                    countText.setVisibility(View.INVISIBLE);
                                    counterText.setText(R.string.lessons_over_today);
                                    lessonNextText.setText(R.string.good_rest);
                                    lessonNext.setVisibility(View.INVISIBLE);
                                } else {
                                    countText.setVisibility(View.VISIBLE);
                                    lessonNext.setVisibility(View.VISIBLE);
                                    counterText.setText(counterString);
                                    lessonNextText.setText(R.string.next_lesson);
                                    countText.setText(countString);
                                    String nextLessonText;
                                    if (mLessons.get(params[2]).getDay() == finalDay)
                                        nextLessonText = mLessons.get(params[2]).getName();
                                    else
                                        nextLessonText = mContext.getString(R.string.lessons_over_today);
                                    lessonNext.setText(nextLessonText);
                                }
                                if (lastParams == null) lastParams = params;
                                if (lastParams[0] != params[0]) {
                                    mMainActivity.updateDashboard();
                                    lastParams = params;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        Thread.sleep(250);
                    } else {
                        mActivity.runOnUiThread(() -> {
                            countText.setVisibility(View.INVISIBLE);
                            counterText.setText(R.string.no_lessons);
                            lessonNextText.setVisibility(View.INVISIBLE);
                            lessonNext.setVisibility(View.INVISIBLE);
                        });
                        Thread.sleep(250);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
        canGo = true;
        if (mThread == null)
            mThread = createThread();
        mThread.start();
    }
}
