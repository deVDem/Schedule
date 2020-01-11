package ru.devdem.reminder;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Response;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private BottomNavigationView mBottomNavigationView;
    private LessonsController mLessonsController;
    private TimeController mTimeController;
    private RelativeLayout mRelativeLayout;
    private Snackbar snackbar;
    private String mUrlNewVersion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        String NAME_PREFS = "settings";
        SharedPreferences settings = getSharedPreferences(NAME_PREFS, MODE_PRIVATE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        mLessonsController = LessonsController.get(this);
        mLessonsController.loadLessons();
        if (mLessonsController.getLessons().size() == 0) {
            Response.Listener<String> listener = response -> {
                mLessonsController.parseLessons(response);
                start();
            };
            NetworkController.getLessons(this, listener, settings.getString("group", "0"));
        } else start();
    }

    private void start() {
        View view = View.inflate(this, R.layout.activity_main, null);
        mRelativeLayout = view.findViewById(R.id.main_relative_layout);
        setContentView(view);
        mTimeController = TimeController.get(this);
        mViewPager = findViewById(R.id.viewPager);
        mViewPager.setAdapter(new MainViewPagerAdapter(getSupportFragmentManager(), 0));
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
                getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.text_color)
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
                    actionBar.setSubtitle(getResources().getString(R.string.dashboard));
                    mViewPager.setCurrentItem(1);
                    break;
                case R.id.main_timer:
                    actionBar.setSubtitle(getResources().getString(R.string.timer));
                    mViewPager.setCurrentItem(2);
                    break;
                case R.id.main_notifications:
                    actionBar.setSubtitle(getResources().getString(R.string.notifications));
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
    }

    private void downloadNewVer(String url) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            String filename = "test.apk";
            PRDownloader.initialize(getApplicationContext());
            PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                    .setDatabaseEnabled(false)
                    .build();
            PRDownloader.initialize(getApplicationContext(), config);
            Log.d("downloader", "start");
            PRDownloader.download(url, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath(), filename)
                    .build()
                    .start(new OnDownloadListener() {
                        @Override
                        public void onDownloadComplete() {
                            Toast.makeText(MainActivity.this, "Установочный файл скачан. Пожалуйста, откройте его вручную в папке загрузки: " + filename, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError(Error error) {
                            Toast.makeText(MainActivity.this, R.string.errorNetwork, Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            mUrlNewVersion = url;
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    123);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadNewVer(mUrlNewVersion);
            }
        }
    }

    private void getVerInt() {
        Response.Listener<String> listener = response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                int lastVer = jsonObject.getInt("ver");
                String url = jsonObject.getString("url");
                if (lastVer > BuildConfig.VERSION_CODE) {
                    snackbar = Snackbar.make(mRelativeLayout, R.string.a_new_version_of_the_app_is_available, Snackbar.LENGTH_LONG);
                    snackbar.setAction(R.string.download, v -> downloadNewVer(url))
                            .show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        NetworkController.getLastVerInt(this, listener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLessonsController.destroy();
        mTimeController.destroy();
    }

    static class MainViewPagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<Fragment> mFragments = new ArrayList<>();

        MainViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
            mFragments.add(new ProfileFragment());
            mFragments.add(new DashboardFragment());
            mFragments.add(new TimerFragment());
            mFragments.add(new NotificationsFragment());
            mFragments.add(new SettingsFragment());
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
