package ru.devxem.reminder.ui.dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Objects;

import ru.devxem.reminder.MainActivity;
import ru.devxem.reminder.R;
import ru.devxem.reminder.api.Error;

public class DashboardFragment extends Fragment {

    @SuppressLint("StaticFieldLeak")
    private static ListView listView;
    private static CountDownTimer timer;
    private static boolean loaded;
    @SuppressLint("StaticFieldLeak")
    private static SwipeRefreshLayout swipeRefreshLayout;

    public static void reloadLess(ArrayAdapter<String> adp2) {
        swipeRefreshLayout.setRefreshing(false);
        loaded = true;
        timer.cancel();
        listView.setAdapter(adp2);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        try {
            final String id = MainActivity.getSss().get(0);
            final String group = MainActivity.getSss().get(1);
            Objects.requireNonNull(getActivity()).setTitle("хуй");
            // findViewById() делать через root!
            listView = root.findViewById(R.id.listofitems);
            swipeRefreshLayout = root.findViewById(R.id.swipe_dash);
            swipeRefreshLayout.setNestedScrollingEnabled(true);
            //swipeRefreshLayout.canChildScrollUp() = true;
            final Context context = Objects.requireNonNull(getContext());
            loaded = false;
            /*timer = new CountDownTimer(15000, 250) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (loaded) {
                        this.cancel();
                    }
                }

                @Override
                public void onFinish() {
                    if (!loaded) {
                        Error.setError(context, id);
                    }
                }
            }.start(); */
            swipeRefreshLayout.setRefreshing(true);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // reloadLess(GetNear.updateLessons(group, getContext(), 1));
                }
            });
            //reloadLess(GetNear.updateLessons(group, getContext(), 0));
        } catch (Exception e) {
            Error.setErr(getContext(), e.toString(), Objects.requireNonNull(getContext()).getSharedPreferences("settings", Context.MODE_PRIVATE).getString("email", null));
        }
        return root;
    }
}