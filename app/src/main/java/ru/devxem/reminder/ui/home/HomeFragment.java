package ru.devxem.reminder.ui.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import ru.devxem.reminder.MainActivity;
import ru.devxem.reminder.R;
import ru.devxem.reminder.TimeNotification;
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
            /*Response.Listener<String> listener = new Response.Listener<String>() {
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
            updateBT = root.findViewById(R.id.button3); */
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
        /*MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                AdView mAdView = root.findViewById(R.id.adViewHome);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            }
        });*/
        } catch (Exception e) {
            Error.setErr(context, e.toString(), context.getSharedPreferences("settings", Context.MODE_PRIVATE).getString("email", null));
        }
        return root;
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

    private void setUpdate(boolean update) {
        if (update)
            panel_update.setVisibility(View.VISIBLE);
        else panel_update.setVisibility(View.GONE);
        updateBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: сделать запрос разрешения
                String destFileName = "img.jpg";
                String src = "http://files.devdem.ru/image.png";
                File dest = new File(Environment.getExternalStorageDirectory() + "/Download/" + destFileName);
                new LoadFile(src, dest).start();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TimeNotification.cancel(context);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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

    private void onDownloadComplete(boolean success, File dest) {
        // файл скачался, можно как-то реагировать
        if (success) {
            // TODO: открытие файла
        }
        Log.i("***", "************** " + success);
    }

    private class LoadFile extends Thread {
        private final String src;
        private final File dest;

        LoadFile(String src, File dest) {
            this.src = src;
            this.dest = dest;
        }

        @Override
        public void run() {
            try {
                FileUtils.copyURLToFile(new URL(src), dest);
                onDownloadComplete(true, dest);
            } catch (IOException e) {
                e.printStackTrace();
                onDownloadComplete(false, null);
            }
        }
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