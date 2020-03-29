package ru.devdem.reminder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Response;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import org.json.JSONObject;

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
    private NetworkController mNetworkController;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences mSettings;
    private Context mContext;
    private boolean sort_by_week = false;
    private static final String TAG = "DashboardFragment";
    private String[] adIds = {"ca-app-pub-7389415060915567/7850481695",
            "ca-app-pub-7389415060915567/1285073344",
            "ca-app-pub-7389415060915567/3301148008"};
    private MainActivity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.fragment_dashboard, null);
        mContext = Objects.requireNonNull(getContext());
        activity = (MainActivity) getActivity();
        mSettings = mContext.getSharedPreferences("settings", Context.MODE_PRIVATE);
        days = getResources().getStringArray(R.array.days);
        mTimeController = TimeController.get(getContext());
        mLessonsController = LessonsController.get(mContext);
        mNetworkController = NetworkController.get();
        mRecyclerView = v.findViewById(R.id.recyclerViewDash);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(llm);
        swipeRefreshLayout = v.findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setOnRefreshListener(() -> update(true));
        update(mLessonsController.getLessons().size() == 0);
        setHasOptionsMenu(true);
        sort_by_week = mSettings.getBoolean("sort_by_week", false);
        return v;
    }

    void update(boolean why) {
        if (why) {
            Response.Listener<String> listener = response -> {
                try {
                    if (new JSONObject(response).getString("error").equals("NO_TOKEN")) {
                        Objects.requireNonNull(activity).checkAccount();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mLessonsController.parseLessons(response);
                updateUI();
            };
            Response.ErrorListener errorListener = error -> {
                Toast.makeText(mContext, R.string.errorNetwork, Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            };
            mNetworkController.getLessons(mContext, listener, errorListener, mSettings.getString("group", "0"), mSettings.getString("token", "null"));
        } else updateUI();
    }

    private void updateUI() {
        if (mLessonsController != null) {
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_dashboard, menu);
        MenuItem item = menu.findItem(R.id.menu_sort);
        if (sort_by_week) {
            item.setIcon(R.drawable.ic_list_numbered);
            item.setTitle(getResources().getString(R.string.sort_by) + " " + getResources().getString(R.string.current_day));
        } else {
            item.setIcon(R.drawable.ic_list_bulleted);
            item.setTitle(getResources().getString(R.string.sort_by) + " " + getResources().getString(R.string.week));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_sort) {
            AnimatedVectorDrawable drawable;
            if (!sort_by_week) {
                drawable = (AnimatedVectorDrawable) mContext.getDrawable(R.drawable.ic_menu_dashboard_in);
                item.setIcon(drawable);
                item.setTitle(getResources().getString(R.string.sort_by) + " " + getResources().getString(R.string.current_day));
            } else {
                drawable = (AnimatedVectorDrawable) mContext.getDrawable(R.drawable.ic_menu_dashboard);
                item.setIcon(drawable);
                item.setTitle(getResources().getString(R.string.sort_by) + " " + getResources().getString(R.string.week));
            }
            if (drawable != null) {
                Log.d(TAG, "onOptionsItemSelected: started");
                drawable.start();
            }
            sort_by_week = !sort_by_week;
            mSettings.edit().putBoolean("sort_by_week", sort_by_week).apply();
            update(false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.LessonsViewer> {
        ArrayList<ArrayList<LessonsController.Lesson>> mLessons;
        boolean[] prepared = new boolean[6];

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
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(@NonNull LessonsViewer holder, int position) {
            boolean tomorrow = false;
            if (!prepared[position]) {
                prepared[position] = true;
                ArrayList<LessonsController.Lesson> lessons = mLessons.get(position);
                if (position % 2 == 0) {
                    AdView adView = new AdView(mContext);
                    if (!BuildConfig.DEBUG)
                        adView.setAdUnitId(adIds[position / 2]);
                    else adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
                    adView.setAdSize(getAdSize());
                    holder.mAdContainer.addView(adView);
                    AdRequest adRequest = new AdRequest.Builder().build();
                    adView.loadAd(adRequest);
                }
                if (position + 1 == getItemCount()) {
                    TextView space = new TextView(mContext);
                    space.setWidth(holder.mAdContainer.getWidth());
                    space.setHeight(activity.getHeightMenu() + 20);
                    holder.mAdContainer.addView(space);
                }
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

                    // 0 - урок или перемена
                    // 1 - номер урока которого считать
                    // 2 - номер след.урока
                    // 3 - состояние: ( 0 - до уроков всех, 1 - урок, 2 - перемена, 3 - конец всех уроков)
                    int[] params = mTimeController.getNumberlesson();
                    if (dayOfWeek + 1 == mLessonsController.getLessons().get(params[2]).getDay() && dayOfWeek + 1 == mLessonsController.getLessons().get(params[1]).getDay()) {
                        tomorrow = true;
                    }
                    if (!sort_by_week) {
                        if (tomorrow) position++;
                        if (position + dayOfWeek != 0) {
                            int size = 0;
                            for (int n = 0; n < mLessons.size(); n++) {
                                if (mLessons.get(n).size() == 0) {
                                    break;
                                }
                                size++;
                            }
                            if (dayOfWeek > size) dayOfWeek--;
                            position = (dayOfWeek + position) % (size);
                        }
                        lessons = mLessons.get(position);
                    }
                    holder.mDayOfWeekText.setText(days[position]);
                    if (dayOfWeek == position && !tomorrow) {
                        String dayOfWeekText = days[position] + " " + getResources().getString(R.string.today);
                        holder.mDayOfWeekText.setText(dayOfWeekText);
                    }
                    if (dayOfWeek + 1 == position && tomorrow) {
                        String dayOfWeekText = days[position] + " (" + getResources().getString(R.string.tomorrow) + ")";
                        holder.mDayOfWeekText.setText(dayOfWeekText);
                    }
                    for (int i = 0; i < lessons.size(); i++) {
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
                        TextView descText = view.findViewById(R.id.descLesson);
                        String numberText = lesson.getNumberText();
                        if (params[3] != 3 && params[3] != 0)
                            if (params[0] == 0) {
                                if (lesson.getNumber() == params[1]) {
                                    relativeLayout.setBackgroundTintList(new ColorStateList(states, colors));
                                    relativeLayout.setEnabled(true);
                                    numberLesson.setTextColor(getResources().getColor(R.color.white));
                                    nameLesson.setTextColor(getResources().getColor(R.color.white));
                                    dateText.setTextColor(getResources().getColor(R.color.white));
                                    cabText.setTextColor(getResources().getColor(R.color.white));
                                    descText.setTextColor(getResources().getColor(R.color.white));
                                } else if (lesson.isZamena()) {
                                    colors = new int[]{
                                            getResources().getColor(R.color.card_color_replace),
                                            getResources().getColor(R.color.card_color),
                                    };
                                    numberText = numberText + " " + getResources().getString(R.string.replacement);
                                    relativeLayout.setBackgroundTintList(new ColorStateList(states, colors));
                                    relativeLayout.setEnabled(true);
                                }
                            } else {
                                relativeLayout.setEnabled(true);
                            }

                        numberLesson.setText(numberText);
                        nameLesson.setText(lesson.getName());
                        dateText.setText(timeString);
                        cabText.setText(lesson.getCab());
                        if (lesson.getDescription().length() < 1)
                            descText.setVisibility(View.GONE);
                        else descText.setText(lesson.getDescription());
                        holder.mLessonsLL.addView(view);
                    }
                } else {
                    holder.mRelativeLayout.removeAllViews();
                    holder.mRelativeLayout.setVisibility(View.GONE);
                }
            }
        }

        private AdSize getAdSize() {
            Display display = Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics();
            display.getMetrics(outMetrics);
            float widthPixels = outMetrics.widthPixels;
            float density = outMetrics.density;
            int adWidth = (int) (widthPixels / density);
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(mContext, adWidth);
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
            LinearLayout mAdContainer;
            Space mSpace;

            LessonsViewer(View itemView) {
                super(itemView);
                mRelativeLayout = itemView.findViewById(R.id.relativeLayoutCard);
                mDayOfWeekText = itemView.findViewById(R.id.textDay);
                mLessonsLL = itemView.findViewById(R.id.llLessons);
                mAdContainer = itemView.findViewById(R.id.adContainer);
                mSpace = itemView.findViewById(R.id.space);
            }
        }
    }
}
