package ru.devdem.reminder.ui.main;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Response;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import ru.devdem.reminder.BuildConfig;
import ru.devdem.reminder.NotificationService;
import ru.devdem.reminder.NotificationUtils;
import ru.devdem.reminder.R;
import ru.devdem.reminder.controllers.LessonsController;
import ru.devdem.reminder.controllers.NetworkController;
import ru.devdem.reminder.controllers.TimeController;
import ru.devdem.reminder.ui.DownloadActivity;
import ru.devdem.reminder.ui.HelloActivity;
import ru.devdem.reminder.ui.SplashActivity;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private BottomNavigationView mBottomNavigationView;
    private LessonsController mLessonsController;
    private TimeController mTimeController;
    private NetworkController mNetworkController;
    NotificationUtils notificationUtils;
    private Snackbar snackbar;
    private SharedPreferences mSettings;
    private View mView;
    ArrayList<Fragment> mFragments = new ArrayList<>();
    private static final String TAG = "MainActivity";

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String NAME_PREFS = "settings";
        mSettings = getSharedPreferences(NAME_PREFS, MODE_PRIVATE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mNetworkController = NetworkController.get();
        mLessonsController = LessonsController.get(this);
        mLessonsController.loadLessons();
        notificationUtils = new NotificationUtils(this);
        if (!mSettings.getBoolean("first", true)) {
            if (mSettings.getInt("alpha_warn", 0)%5==0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Предупреждение");
                builder.setMessage("Данная версия приложения находится в альфа-тестировании, " +
                        "поэтому не все функции могут быть доступны, а так же в каких-то моментах " +
                        "приложение может вылетать(не замечал, но может). В общем, дайте время плс :)");
                builder.setPositiveButton("Окей", (dialogInterface, i) -> {
                    mSettings.edit().putInt("alpha_warn", 1).apply();
                    dialogInterface.cancel();
                });
                builder.setCancelable(false);
                builder.show();
            } else {
                mSettings.edit().putInt("alpha_warn", mSettings.getInt("alpha_warn", 0)+1).apply();
            }
            if (Objects.equals(mSettings.getString("group", "0"), "0")) {
                startActivity(new Intent(MainActivity.this, HelloActivity.class));
                finish();
            } else {
                Response.Listener<String> listener = response -> mLessonsController.parseLessons(response);
                mNetworkController.getLessons(this, listener, null, mSettings.getString("group", "0"), mSettings.getString("token", "null"));
                mNetworkController.getGroup(this, null,  mSettings.getString("group", ""), mSettings.getString("token", ""));
                start();
            }
        } else finish();
    }

    private void start() {
        mView = View.inflate(this, R.layout.activity_main, null);
        setContentView(mView);
        MobileAds.initialize(this, initializationStatus -> {

        });
        mTimeController = TimeController.get(this);
        mViewPager = findViewById(R.id.viewPager);
        MainViewPagerAdapter mainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager(), 0);
        mFragments.clear();
        mFragments.add(new ProfileFragment());
        mFragments.add(new DashboardFragment());
        mFragments.add(new TimerFragment());
        mFragments.add(new NotificationsFragment());
        mFragments.add(new SettingsFragment());
        mainViewPagerAdapter.setFragments(mFragments);
        mViewPager.setAdapter(mainViewPagerAdapter);
        mViewPager.setCurrentItem(0);
        mViewPager.setPageTransformer(true, (v, pos) -> {
            final float opacity = Math.abs(Math.abs(pos) - 1);
            v.setRotation(pos * 10000 / 360);
            v.setAlpha(opacity);
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mBottomNavigationView.setSelectedItemId(R.id.main_profile);
                        break;
                    case 1:
                        mBottomNavigationView.setSelectedItemId(R.id.main_dashboard);
                        break;
                    case 2:
                        mBottomNavigationView.setSelectedItemId(R.id.main_timer);
                        break;
                    case 3:
                        mBottomNavigationView.setSelectedItemId(R.id.main_notifications);
                        break;
                    case 4:
                        mBottomNavigationView.setSelectedItemId(R.id.main_settings);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mBottomNavigationView = findViewById(R.id.bottom_nav_view);
        mBottomNavigationView.setSelectedItemId(R.id.main_timer);
        mViewPager.setCurrentItem(2);
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_checked},
                new int[]{android.R.attr.state_enabled}
        };
        int[] colors = new int[]{
                getResources().getColor(R.color.colorAccent, getTheme()),
                getResources().getColor(R.color.text_color, getTheme())
        };
        mBottomNavigationView.setItemIconTintList(new ColorStateList(states, colors));
        AppCompatActivity appCompatActivity = this;
        ActionBar actionBar = Objects.requireNonNull(appCompatActivity.getSupportActionBar());
        actionBar.setSubtitle(getResources().getString(R.string.timer));
        mBottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.main_profile:
                    actionBar.setSubtitle(getResources().getString(R.string.profile));
                    mViewPager.setCurrentItem(0);
                    break;
                case R.id.main_dashboard:
                    if(Objects.requireNonNull(mSettings.getString("group_name", "loading")).length() <= 5)
                    actionBar.setSubtitle(getResources().getString(R.string.schedule_for) + " " + mSettings.getString("group_name", "loading"));
                    else actionBar.setSubtitle(mSettings.getString("group_name", "loading"));
                    mViewPager.setCurrentItem(1);
                    break;
                case R.id.main_timer:
                    actionBar.setSubtitle(getResources().getString(R.string.timer));
                    mViewPager.setCurrentItem(2);
                    break;
                case R.id.main_notifications:
                    if(Objects.requireNonNull(mSettings.getString("group_name", "loading")).length() <= 5)
                        actionBar.setSubtitle(getResources().getString(R.string.messages_for) + " " + mSettings.getString("group_name", "loading"));
                    else actionBar.setSubtitle(mSettings.getString("group_name", "loading"));
                    mViewPager.setCurrentItem(3);
                    break;
                case R.id.main_settings:
                    actionBar.setSubtitle(getResources().getString(R.string.settings));
                    mViewPager.setCurrentItem(4);
                    break;
            }
            return true;
        });
        getVerInt();
        if (mSettings.getBoolean("notification", true)) {
            startService(new Intent(this, NotificationService.class));
        }
        checkAccount();
    }

    public int getHeightMenu() {
        return mBottomNavigationView.getHeight();
    }

    public void checkAccount() {
        Response.Listener<String> listener = response -> {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.isNull("error")) {
                    if (!jsonResponse.isNull("response")) {
                        try {
                            JSONObject jsonObjectResponse = jsonResponse.getJSONObject("response");
                            JSONObject jsonUserInfo = jsonObjectResponse.getJSONObject("user_data");
                            int user_id = jsonUserInfo.getInt("id");
                            String name = jsonUserInfo.getString("names");
                            String email = jsonUserInfo.getString("email");
                            String login1 = jsonUserInfo.getString("login");
                            String group = jsonUserInfo.getString("groupId");
                            String password_hash = jsonUserInfo.getString("password");
                            boolean pro = jsonUserInfo.getString("pro").equals("Yes");
                            //String urlImage = jsonUserInfo.getString("urlImage");
                            boolean spam = jsonUserInfo.getString("spam").equals("Yes");
                            int permission = jsonUserInfo.isNull("permission") ? 0 : jsonUserInfo.getInt("permission");
                            String token = jsonUserInfo.getString("token");
                            SharedPreferences.Editor editor = mSettings.edit();
                            editor.putInt("user_id", user_id);
                            editor.putString("name", name);
                            editor.putString("email", email);
                            editor.putString("login", login1);
                            editor.putString("group", group);
                            editor.putBoolean("spam", spam);
                            editor.putBoolean("pro", pro);
                            editor.putString("password", password_hash);
                            //editor.putString("urlImage", urlImage);
                            editor.putInt("permission", permission);
                            editor.putString("token", token);
                            editor.apply();
                            if (Objects.equals(mSettings.getString("group", "-1"), "-1")) {
                                startActivity(new Intent(MainActivity.this, HelloActivity.class));
                                overridePendingTransition(R.anim.transition_in_back, R.anim.transition_out_back);
                                finish();
                            }
                            Log.d(TAG, "checkAccount: " + response);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Неудалось получить информацию о пользователе.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Wrong password.", Toast.LENGTH_SHORT).show();
                        exit();
                    }
                } else {
                    JSONObject errorJson = jsonResponse.getJSONObject("error");
                    Toast.makeText(this, errorJson.getInt("code") + " " + errorJson.getString("text"), Toast.LENGTH_SHORT).show();
                    if (errorJson.getInt("code") != 1) exit();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Произошла неизвестная ошибка", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        };
        Response.ErrorListener errorListener = error -> Toast.makeText(this, R.string.errorNetwork, Toast.LENGTH_SHORT).show();
        mNetworkController.Login(this, mSettings.getString("login", ""), mSettings.getString("token", ""), listener, errorListener);
    }

    private void exit() {
        mSettings.edit().clear().apply();
        startActivity(new Intent(this, SplashActivity.class));
        overridePendingTransition(R.anim.transition_in, R.anim.transition_out);
        finish();
    }

    private void getVerInt() {
        Response.Listener<String> listener = response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (!jsonObject.isNull(BuildConfig.BUILD_TYPE)){
                    JSONObject verInfJson = jsonObject.getJSONObject(BuildConfig.BUILD_TYPE);
                    int lastVer = verInfJson.getInt("ver");
                    String url = verInfJson.getString("url");
                    boolean market = verInfJson.getBoolean("market");
                    if (lastVer > BuildConfig.VERSION_CODE && !market) {
                        Intent updateIntent = DownloadActivity.newIntent(this, url);
                        updateIntent.setAction("ru.devdem.reminder.downloadupdate");
                        PendingIntent updatePendingIntent = PendingIntent.getActivity(this, 0, updateIntent, 0);
                        NotificationCompat.Builder nb = notificationUtils.getNewUpdateChannelNotification();
                        Notification notification = nb.addAction(new NotificationCompat.Action(R.drawable.ic_notification_timer, getResources().getString(R.string.download), updatePendingIntent)).build();
                        notification.contentIntent = updatePendingIntent;
                        notificationUtils.getManager().notify(102, notification);
                        snackbar = Snackbar.make(mView, Html.fromHtml("<font color=\"#ffffff\">" + getResources().getString(R.string.a_new_version_of_the_app_is_available) + "</font>"), Snackbar.LENGTH_LONG);
                        snackbar.setAction(R.string.download, v -> {
                            Intent updateIntent1 = DownloadActivity.newIntent(MainActivity.this, url);
                            startActivity(updateIntent1);
                        });
                        snackbar.show();
                    } else if(lastVer > BuildConfig.VERSION_CODE && market) {
                        Uri address = Uri.parse("http://developer.alexanderklimov.ru");
                        Intent openLinkIntent = new Intent(Intent.ACTION_VIEW, address);
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, openLinkIntent, 0);
                        NotificationCompat.Builder nb = notificationUtils.getNewUpdateChannelNotification();
                        Notification notification = nb.addAction(new NotificationCompat.Action(R.drawable.ic_notification_timer, getResources().getString(R.string.download), pendingIntent)).build();
                        notification.contentIntent = pendingIntent;
                        notificationUtils.getManager().notify(102, notification);
                        snackbar = Snackbar.make(mView, Html.fromHtml("<font color=\"#ffffff\">" + getResources().getString(R.string.a_new_version_of_the_app_is_available) + "</font>"), Snackbar.LENGTH_LONG);
                        snackbar.setAction(R.string.download, v -> {
                            startActivity(openLinkIntent);
                            notificationUtils.getManager().cancel(102);
                        });
                        snackbar.show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        mNetworkController.getLastVerInt(this, listener);
    }

    public void updateDashboard() {
        try {
            DashboardFragment fragment = (DashboardFragment) mFragments.get(1);
            fragment.update(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLessonsController != null) mLessonsController.destroy();
        if (mTimeController != null) mTimeController.destroy();
    }

    static class MainViewPagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<Fragment> mFragments = new ArrayList<>();

        void setFragments(ArrayList<Fragment> fragments) {
            mFragments = fragments;
        }

        MainViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
}
