package ru.devdem.reminder;

import android.annotation.SuppressLint;
import android.content.Context;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class DashboardFragment extends Fragment {

    private String[] days;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.fragment_dashboard, null);
        Context context = Objects.requireNonNull(getContext());
        days = getResources().getStringArray(R.array.days);
        LessonsController lessonsController = LessonsController.get(context);
        RecyclerView recyclerView = v.findViewById(R.id.recyclerViewDash);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(llm);
        RVAdapter adapter = new RVAdapter(prepareArrayFromArray(lessonsController.getLessons()));
        ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(adapter);
        scaleInAnimationAdapter.setDuration(500);
        scaleInAnimationAdapter.setFirstOnly(false);
        scaleInAnimationAdapter.setInterpolator(new AccelerateDecelerateInterpolator());
        AlphaInAnimationAdapter animationAdapter = new AlphaInAnimationAdapter(scaleInAnimationAdapter);
        animationAdapter.setDuration(1000);
        animationAdapter.setFirstOnly(false);
        recyclerView.setAdapter(animationAdapter);
        return v;
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
                int dayOfWeek = new Date().getDay() - 1;
                holder.mDayOfWeekText.setText(days[position]);
                String dayOfWeekText = days[position] + " (сегодня)";
                if (dayOfWeek == position) holder.mDayOfWeekText.setText(dayOfWeekText);
                for (int i = 0; i < lessons.size(); i++) {
                    LessonsController.Lesson lesson = lessons.get(i);
                    String startString = new SimpleDateFormat("H:mm", Locale.getDefault()).format(lesson.getStart());
                    String endString = new SimpleDateFormat("H:mm", Locale.getDefault()).format(lesson.getEnd());
                    String timeString = startString + "-" + endString;
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.lesson_item, null);
                    TextView numberLesson = view.findViewById(R.id.numberLesson);
                    TextView nameLesson = view.findViewById(R.id.textLesson);
                    TextView dateText = view.findViewById(R.id.textDate);
                    TextView cabText = view.findViewById(R.id.textCab);
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
