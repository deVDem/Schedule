package ru.devdem.reminder;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences mSettings;
    private String NAME_PREFS = "settings";

    private ViewPager mViewPager;
    private BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mSettings = getSharedPreferences(NAME_PREFS, MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = findViewById(R.id.viewPager);
        mViewPager.setAdapter(new MainViewPagerAdapter(getSupportFragmentManager()));
        mViewPager.setCurrentItem(0);
        mViewPager.setPageTransformer(false, (v, pos) -> {
            final float opacity = Math.abs(Math.abs(pos) - 1);
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
        mBottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.main_dashboard:
                    mViewPager.setCurrentItem(0);
                    break;
                case R.id.main_timer:
                    mViewPager.setCurrentItem(1);
                    break;
                case R.id.main_notifications:
                    mViewPager.setCurrentItem(2);
                    break;
                case R.id.main_settings:
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
                    return new TimerFragment();
                case 1:
                    return new TimerFragment();
                case 2:
                    return new TimerFragment();
                case 3:
                    return new TimerFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}
