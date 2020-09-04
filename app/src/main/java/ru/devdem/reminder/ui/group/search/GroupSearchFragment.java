package ru.devdem.reminder.ui.group.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.Objects;

import ru.devdem.reminder.BuildConfig;
import ru.devdem.reminder.controllers.ObjectsController;
import ru.devdem.reminder.controllers.ObjectsController.User;
import ru.devdem.reminder.R;

public class GroupSearchFragment extends Fragment {

    private EditText mETGroupName;
    private EditText mETCity;
    private EditText mETBuilding;
    private Switch mSwConfirmed;
    private User mUser;

    public GroupSearchFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_grouplist_search, null);
        mUser = ObjectsController.getLocalUserInfo(getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE));
        mETGroupName = view.findViewById(R.id.etGroupName);
        mETCity = view.findViewById(R.id.etGroupCity);
        mETBuilding = view.findViewById(R.id.etGroupBuilding);
        mSwConfirmed = view.findViewById(R.id.swOnlyConfirmed);
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_checked},
                new int[]{-android.R.attr.state_checked}
        };
        int[] colors = new int[]{
                getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.white)
        };
        mSwConfirmed.setThumbTintList(new ColorStateList(states, colors));
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.search_group);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        if (!mUser.isPro()) {
            AdView adView = new AdView(getContext());
            if (!BuildConfig.DEBUG)
                adView.setAdUnitId("ca-app-pub-7389415060915567/6177952150");
            else adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
            adView.setAdSize(getAdSize());
            LinearLayout mAdContainer = view.findViewById(R.id.adContainer);
            mAdContainer.addView(adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }
        return view;
        // TODO: сделать подсказку для свайпа
    }

    private AdSize getAdSize() {
        Display display = Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;
        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(getContext(), adWidth);
    }

    String[] getParams() {
        String[] params = new String[6];
        try {
            params[0] = mETGroupName.getText().toString();
            params[1] = mETCity.getText().toString();
            params[2] = mETBuilding.getText().toString();
            params[3] = mSwConfirmed.isChecked() ? "Yes" : "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }
}
