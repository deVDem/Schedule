package ru.devdem.reminder;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
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

public class DashboardFragment extends Fragment {

    private LessonsController mLessonsController;
    private String[] days = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dashboard, null);
        Context context = getContext();
        mLessonsController = LessonsController.get(context);
        //TODO: удалить код
        for (int i = 0; i < 7; i++) {
            for (int j = 1; j <= 8; j++) {
                mLessonsController.addLesson("Урок", String.valueOf(j), i, new Date(), new Date());
            }
        }
        RecyclerView recyclerView = v.findViewById(R.id.recyclerViewDash);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(new RVAdapter(prepareArrayFromArray(mLessonsController.getLessons())));
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

                StringBuilder lessonsString = new StringBuilder();
                for (int i = 0; i < lessons.size(); i++) {
                    LessonsController.Lesson lesson = lessons.get(i);
                    String startString = new SimpleDateFormat("H:mm", Locale.getDefault()).format(lesson.getStart());
                    String endString = new SimpleDateFormat("H:mm", Locale.getDefault()).format(lesson.getEnd());
                    lessonsString.append(lesson.getNumberText()).append(" | ")
                            .append(lesson.getName()).append(" | ").append(startString).append("->").append(endString);
                    if (i + 1 != lessons.size()) lessonsString.append("\n");
                }
                holder.mLessonsText.setText(lessonsString.toString());
            } else {
                holder.mRelativeLayout.removeAllViews();
                holder.mRelativeLayout.setVisibility(View.GONE);
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
            TextView mLessonsText;

            LessonsViewer(View itemView) {
                super(itemView);
                mRelativeLayout = itemView.findViewById(R.id.relativeLayoutCard);
                mDayOfWeekText = itemView.findViewById(R.id.textDay);
                mLessonsText = itemView.findViewById(R.id.textLessons);
            }
        }
    }
}
