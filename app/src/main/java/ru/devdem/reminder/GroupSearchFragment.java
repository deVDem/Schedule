package ru.devdem.reminder;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class GroupSearchFragment extends Fragment {

    private EditText mETGroupName;
    private EditText mETCity;
    private EditText mETBuilding;
    private Switch mSwConfirmed;

    public GroupSearchFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_grouplist_search, null);
        mETGroupName = view.findViewById(R.id.etGroupName);
        mETCity = view.findViewById(R.id.etGroupCity);
        mETBuilding = view.findViewById(R.id.etGroupBuilding);
        mSwConfirmed = view.findViewById(R.id.swOnlyConfirmed);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.search_group);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        return view;
    }

    String[] getParams() {
        String[] params = new String[4];
        params[0] = mETGroupName.getText().toString();
        params[1] = mETCity.getText().toString();
        params[2] = mETBuilding.getText().toString();
        params[3] = mSwConfirmed.isChecked() ? "Yes" : "";
        return params;
    }
}
