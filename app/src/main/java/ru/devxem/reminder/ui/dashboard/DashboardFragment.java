package ru.devxem.reminder.ui.dashboard;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import ru.devxem.reminder.MainActivity;
import ru.devxem.reminder.R;
import ru.devxem.reminder.api.GetNear;

public class DashboardFragment extends Fragment {

    @SuppressLint("StaticFieldLeak")
    private static ListView listView;

    public static void reloadLess(ArrayAdapter<String> adp2) {
        listView.setAdapter(adp2);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        String id = MainActivity.getSss().get(0);
        String group = MainActivity.getSss().get(1);
        // findViewById() делать через root!
        listView = root.findViewById(R.id.listofitems);
        GetNear.parseLessons(group, id, getContext());
        return root;
    }
}