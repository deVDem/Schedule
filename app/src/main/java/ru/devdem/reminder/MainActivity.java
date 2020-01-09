package ru.devdem.reminder;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences mSettings;
    private String NAME_PREFS = "settings";

    private ViewPager mViewPager;
    private BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mSettings = getSharedPreferences(NAME_PREFS, MODE_PRIVATE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        View view = View.inflate(this, R.layout.activity_main, null);
        setContentView(view);
        mViewPager = findViewById(R.id.viewPager);
        mViewPager.setAdapter(new MainViewPagerAdapter(getSupportFragmentManager()));
        mViewPager.setCurrentItem(0);
        mViewPager.setPageTransformer(true, (v, pos) -> {
            final float opacity = Math.abs(Math.abs(pos) - 1);
            v.setScaleX(opacity);
            v.setScaleY(opacity);
            v.setRotation(pos * 10000 / 360);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.setBackgroundColor(Color.argb(1 - opacity, 0, 0, 0));
            }
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
                        mBottomNavigationView.setSelectedItemId(R.id.main_dashboard);
                        break;
                    case 1:
                        mBottomNavigationView.setSelectedItemId(R.id.main_timer);
                        break;
                    case 2:
                        mBottomNavigationView.setSelectedItemId(R.id.main_notifications);
                        break;
                    case 3:
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
        mViewPager.setCurrentItem(1);
        AppCompatActivity appCompatActivity = this;
        ActionBar actionBar = Objects.requireNonNull(appCompatActivity.getSupportActionBar());
        actionBar.setSubtitle("Timer");
        mBottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.main_dashboard:
                    actionBar.setSubtitle("Dashboard");
                    mViewPager.setCurrentItem(0);
                    break;
                case R.id.main_timer:
                    actionBar.setSubtitle("Timer");
                    mViewPager.setCurrentItem(1);
                    break;
                case R.id.main_notifications:
                    actionBar.setSubtitle("Notifications");
                    mViewPager.setCurrentItem(2);
                    break;
                case R.id.main_settings:
                    actionBar.setSubtitle("Settings");
                    mViewPager.setCurrentItem(3);
                    break;
            }
            return true;
        });
    }


    public static class MainViewPagerAdapter extends FragmentStatePagerAdapter {
        MainViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new DashboardFragment();
                case 1:
                    return new TimerFragment();
                case 2:
                    return new NotificationsFragment();
                case 3:
                    return new SettingsFragment();
                default:
                    return new Fragment();
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}
