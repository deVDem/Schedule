package ru.devxem.reminder.ui.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import ru.devxem.reminder.BuildConfig;
import ru.devxem.reminder.MainActivity;
import ru.devxem.reminder.R;
import ru.devxem.reminder.api.Error;
import ru.devxem.reminder.api.GetNear;
import ru.devxem.reminder.api.Time;
import ru.devxem.reminder.api.URLs;

public class HomeFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    private static TextView textView;
    @SuppressLint("StaticFieldLeak")
    private static TextView lefttext;
    private static Date currentDate;
    private static int hour;
    private static int min;
    private static int sec;
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private String id;
    private String group;
    private static boolean isEnabled = true;
    @SuppressLint("StaticFieldLeak")
    private static SwipeRefreshLayout swipeRefreshLayout;
    @SuppressLint("StaticFieldLeak")
    private static Activity activity;
    private RelativeLayout panel_update;
    private Button updateBT;
    private AdView mAdView;


    private Runnable doBackgroundThreadProcessing = new Runnable() {
        public void run() {
            backgroundThreadProcessing();
        }
    };

    public static void updateRefresh(final boolean set) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(set);
            }
        });
    }

    public static void noInfo() {
        lefttext.setText("");
        textView.setText("Нет данных");
    }

    public static void setEnabled(boolean iab) {
        isEnabled = iab;
    }

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, final Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        context = getContext();
        try {
            MobileAds.initialize(context, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });
            Response.Listener<String> listener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    int curVer = BuildConfig.VERSION_CODE;
                    int nowVer = Integer.parseInt(response);
                    setUpdate(nowVer > curVer);
                }
            };
            getVer getVer = new getVer(listener);
            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(getVer);
            panel_update = root.findViewById(R.id.panel_update);
            updateBT = root.findViewById(R.id.button3);

            textView = root.findViewById(R.id.remaintext);
            lefttext = root.findViewById(R.id.textView);
            activity = getActivity();
            id = MainActivity.getSss().get(0);
            group = MainActivity.getSss().get(1);
            Thread threads = new Thread(null, doBackgroundThreadProcessing,
                    "Main");
            threads.start();
            swipeRefreshLayout = root.findViewById(R.id.swipe_container);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Calendar c = Calendar.getInstance();
                    c.setTimeZone(TimeZone.getDefault());
                    c.setTime(currentDate);
                    int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                    GetNear.reloadlessons(context, group, String.valueOf(id), String.valueOf(dayOfWeek), 2);
                }
            });
            final FrameLayout adContainerView = root.findViewById(R.id.adViewHome);
            mAdView = new AdView(context);
            if (!BuildConfig.DEBUG) mAdView.setAdUnitId("ca-app-pub-7389415060915567/7081052515");
            else mAdView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
            adContainerView.addView(mAdView);
            loadBanner();
        } catch (Exception e) {
            Error.setErr(context, e.toString(), context.getSharedPreferences("settings", Context.MODE_PRIVATE).getString("email", null));
        }
        return root;
    }

    private void loadBanner() {
        AdRequest adRequest =
                new AdRequest.Builder().build();
        AdSize adSize = getAdSize();
        mAdView.setAdSize(adSize);
        mAdView.loadAd(adRequest);
    }

    private AdSize getAdSize() {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
    }

    private void backgroundThreadProcessing() {
        while (true) {
            if (isEnabled) {
                try {
                    Thread.sleep(250);
                    currentDate = new Date();
                    DateFormat timeFormatH = new SimpleDateFormat("HH", Locale.getDefault());
                    DateFormat timeFormatM = new SimpleDateFormat("mm", Locale.getDefault());
                    DateFormat timeFormatS = new SimpleDateFormat("ss", Locale.getDefault());


                    hour = Integer.valueOf(timeFormatH.format(currentDate));
                    min = Integer.valueOf(timeFormatM.format(currentDate));
                    sec = Integer.valueOf(timeFormatS.format(currentDate));

                    Calendar c = Calendar.getInstance();
                    c.setTimeZone(TimeZone.getDefault());
                    c.setTime(currentDate);
                    int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                    Update(GetNear.updatelessons(context, hour, min, String.valueOf(dayOfWeek), sec));
                } catch (InterruptedException e) {
                    Error.setError(context, id);
                    e.printStackTrace();
                }
            } else {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isEnabled = true;
        if (currentDate != null) {
            Calendar c = Calendar.getInstance();
            c.setTimeZone(TimeZone.getDefault());
            c.setTime(currentDate);
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            Update(GetNear.updatelessons(context, hour, min, String.valueOf(dayOfWeek), sec));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isEnabled = false;
    }

    private void setUpdate(boolean update) {
        if (update)
            panel_update.setVisibility(View.VISIBLE);
        else panel_update.setVisibility(View.GONE);
        updateBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri address = Uri.parse("https://github.com/deVDem/Schedule/raw/master/app/release/app-release.apk");
                Intent openlink = new Intent(Intent.ACTION_VIEW, address);
                startActivity(openlink);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void Update(final int[] answer) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Calendar c = Calendar.getInstance();
                c.setTimeZone(TimeZone.getDefault());
                c.setTime(currentDate);
                if (answer == null) return;
                if (answer[5] != 2) {
                    String remain;
                    remain = Time.getRemain(answer[0], hour, answer[1], min, 0, sec, -1);
                    textView.setText(remain);
                    String string = context.getString(R.string.remain) + context.getString(R.string.pause);
                    if (answer[5] == 1) {
                        string = context.getString(R.string.remain) + context.getString(R.string.lesson);
                    }
                    lefttext.setText(string);
                } else {
                    lefttext.setText(context.getString(R.string.noles));
                    textView.setText(context.getString(R.string.rest));
                }
            }
        });
    }
}

class getVer extends StringRequest {
    private static final String LOGIN_REQUEST_URL = URLs.getVer();
    private Map<String, String> params;

    getVer(Response.Listener<String> listener) {
        super(Method.POST, LOGIN_REQUEST_URL, listener, null);
        params = new HashMap<>();
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}