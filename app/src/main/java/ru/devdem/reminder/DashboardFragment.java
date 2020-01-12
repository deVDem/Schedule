package ru.devdem.reminder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Response;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class DashboardFragment extends Fragment {

    private String[] days;
    private TimeController mTimeController;
    private LessonsController mLessonsController;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences mSettings;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.fragment_dashboard, null);
        mContext = Objects.requireNonNull(getContext());
        mSettings = mContext.getSharedPreferences("settings", Context.MODE_PRIVATE);
        days = getResources().getStringArray(R.array.days);
        mTimeController = TimeController.get(getContext());
        mLessonsController = LessonsController.get(mContext);
        mRecyclerView = v.findViewById(R.id.recyclerViewDash);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(llm);
        swipeRefreshLayout = v.findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setOnRefreshListener(() -> update(1));
        update(0);
        return v;
    }

    private void update(int why) {
        if (why == 1) {
            Response.Listener<String> listener = response -> {
                mLessonsController.parseLessons(response);
                updateUI();
            };
            NetworkController.getLessons(mContext, listener, mSettings.getString("group", "0"));
        } else updateUI();
    }

    private void updateUI() {
        RVAdapter RVAdapter = new RVAdapter(prepareArrayFromArray(mLessonsController.getLessons()));
        ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(RVAdapter);
        scaleInAnimationAdapter.setDuration(500);
        scaleInAnimationAdapter.setFirstOnly(false);
        scaleInAnimationAdapter.setInterpolator(new AccelerateDecelerateInterpolator());
        AlphaInAnimationAdapter animationAdapter = new AlphaInAnimationAdapter(scaleInAnimationAdapter);
        animationAdapter.setDuration(1000);
        animationAdapter.setFirstOnly(false);
        mRecyclerView.setAdapter(animationAdapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    private ArrayList<ArrayList<LessonsController.Lesson>> prepareArrayFromArray(ArrayList<LessonsController.Lesson> lessons) {
        ArrayList<ArrayList<LessonsController.Lesson>> mArrayFromArray = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            ArrayList<LessonsController.Lesson> lessonForOneDay = new ArrayList<>();
            for (int j = 0; j < lessons.size(); j++) {
                if (lessons.get(j).getDay() == i) {
                    lessonForOneDay.add(lessons.get(j));
                }
            }
            mArrayFromArray.add(lessonForOneDay);
        }


        return mArrayFromArray;
    }


    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.LessonsViewer> {
        ArrayList<ArrayList<LessonsController.Lesson>> mLessons;

        RVAdapter(ArrayList<ArrayList<LessonsController.Lesson>> lessons) {
            this.mLessons = lessons;
        }

        @NonNull
        @Override
        public LessonsViewer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
            return new LessonsViewer(v);
        }

        @Override
        public void onBindViewHolder(@NonNull LessonsViewer holder, int position) {
            ArrayList<LessonsController.Lesson> lessons = mLessons.get(position);
            if (lessons.size() != 0) {
                Calendar calendar = Calendar.getInstance();
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                switch (dayOfWeek) {
                    case Calendar.MONDAY:
                        dayOfWeek = 0;
                        break;
                    case Calendar.TUESDAY:
                        dayOfWeek = 1;
                        break;
                    case Calendar.WEDNESDAY:
                        dayOfWeek = 2;
                        break;
                    case Calendar.THURSDAY:
                        dayOfWeek = 3;
                        break;
                    case Calendar.FRIDAY:
                        dayOfWeek = 4;
                        break;
                    case Calendar.SATURDAY:
                        dayOfWeek = 5;
                        break;
                    case Calendar.SUNDAY:
                        dayOfWeek = 6;
                        break;
                }
                holder.mDayOfWeekText.setText(days[position]);
                String dayOfWeekText = days[position] + " (сегодня)";
                if (dayOfWeek == position) holder.mDayOfWeekText.setText(dayOfWeekText);
                for (int i = 0; i < lessons.size(); i++) {
                    // 0 - урок или перемена
                    // 1 - номер урока которого считать
                    // 2 - номер след.урока
                    // 3 - состояние: ( 0 - до уроков всех, 1 - урок, 2 - перемена, 3 - конец всех уроков)
                    int[] params = mTimeController.getNumberlesson();
                    LessonsController.Lesson lesson = lessons.get(i);
                    String startString = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(lesson.getStart());
                    String endString = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(lesson.getEnd());
                    String timeString = startString + "-" + endString;
                    @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext()).inflate(R.layout.lesson_item, null);
                    RelativeLayout relativeLayout = view.findViewById(R.id.rlLesson);
                    int[][] states = new int[][]{
                            new int[]{android.R.attr.state_enabled},
                            new int[]{-android.R.attr.state_enabled}
                    };
                    int[] colors = new int[]{
                            getResources().getColor(R.color.colorAccent),
                            getResources().getColor(R.color.card_color),
                    };
                    TextView numberLesson = view.findViewById(R.id.numberLesson);
                    TextView nameLesson = view.findViewById(R.id.textLesson);
                    TextView dateText = view.findViewById(R.id.textDate);
                    TextView cabText = view.findViewById(R.id.textCab);
                    if (params[3] != 3 && params[3] != 0)
                        switch (params[0]) {
                            case 0:
                                if (lesson.getNumber() == params[1]) {
                                    relativeLayout.setBackgroundTintList(new ColorStateList(states, colors));
                                    relativeLayout.setEnabled(true);
                                    numberLesson.setTextColor(getResources().getColor(R.color.white));
                                    nameLesson.setTextColor(getResources().getColor(R.color.white));
                                    dateText.setTextColor(getResources().getColor(R.color.white));
                                    cabText.setTextColor(getResources().getColor(R.color.white));
                                }
                                break;
                            case 1:
                                if (lesson.getNumber() == params[2]) {
                                    relativeLayout.setBackgroundTintList(new ColorStateList(states, colors));
                                    relativeLayout.setEnabled(true);
                                    numberLesson.setTextColor(getResources().getColor(R.color.white));
                                    nameLesson.setTextColor(getResources().getColor(R.color.white));
                                    dateText.setTextColor(getResources().getColor(R.color.white));
                                    cabText.setTextColor(getResources().getColor(R.color.white));
                                }
                                break;
                            default:
                                relativeLayout.setEnabled(true);
                                break;
                        }
                    numberLesson.setText(lesson.getNumberText());
                    nameLesson.setText(lesson.getName());
                    dateText.setText(timeString);
                    cabText.setText(lesson.getCab());
                    holder.mLessonsLL.addView(view);
                }
            } else {
                holder.mRelativeLayout.removeAllViews();
                holder.mRelativeLayout.setVisibility(View.GONE);
            }
            if (position + 1 == getItemCount()) {
                holder.mSpace.setVisibility(View.VISIBLE);
            }
        }

        private int countItems() {
            int i = 0;
            for (; i < mLessons.size(); i++) {
                ArrayList<LessonsController.Lesson> lessons = mLessons.get(i);
                if (lessons.size() == 0) break;
            }
            return i;
        }

        @Override
        public int getItemCount() {
            return countItems();
        }

        class LessonsViewer extends RecyclerView.ViewHolder {
            RelativeLayout mRelativeLayout;
            TextView mDayOfWeekText;
            LinearLayout mLessonsLL;
            Space mSpace;

            LessonsViewer(View itemView) {
                super(itemView);
                mRelativeLayout = itemView.findViewById(R.id.relativeLayoutCard);
                mDayOfWeekText = itemView.findViewById(R.id.textDay);
                mLessonsLL = itemView.findViewById(R.id.llLessons);
                mSpace = itemView.findViewById(R.id.space);
            }
        }
    }
}
