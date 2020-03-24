package ru.devdem.reminder;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;

public class GroupListActivity extends AppCompatActivity {
    private static final String TAG = "GroupListActivity";
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private GroupListFragment mFragment1;
    private GroupSearchFragment mFragment2;
    private ViewPager mViewPager;
    private float[] scale = new float[2];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(this, R.layout.activity_grouplist, null);
        mFragment1 = new GroupListFragment();
        mFragment2 = new GroupSearchFragment();
        setContentView(view);
        mViewPager = view.findViewById(R.id.viewPager2);
        GroupListViewPagerAdapter groupListViewPagerAdapter = new GroupListViewPagerAdapter(getSupportFragmentManager(), 0);
        mFragments.clear();
        mFragments.add(mFragment1);
        mFragments.add(mFragment2);
        groupListViewPagerAdapter.setFragments(mFragments);
        mViewPager.setAdapter(groupListViewPagerAdapter);
        mViewPager.setCurrentItem(1);
        FloatingActionButton floatingActionButton = view.findViewById(R.id.floatingActionButtonGroupList);
        FloatingActionButton floatingActionButtonSearch = view.findViewById(R.id.floatingActionButtonGroupListSearch);
        floatingActionButtonSearch.setOnClickListener(v -> mViewPager.setCurrentItem(1));
        floatingActionButtonSearch.hide();
        scale[0] = floatingActionButtonSearch.getScaleX();
        scale[1] = floatingActionButtonSearch.getScaleY();
        floatingActionButton.setOnClickListener(v -> {
            mViewPager.setCurrentItem(0);
            GroupListFragment listFragment = mFragment1;
            String[] parameters = mFragment2.getParams();
            listFragment.updateGroups(parameters);
            Log.d(TAG, "onCreate: " + Arrays.toString(parameters));
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    floatingActionButton.setRotation((1 - positionOffset) * 180);
                    floatingActionButtonSearch.setScaleX((1 - positionOffset) * scale[0]);
                    floatingActionButtonSearch.setScaleY((1 - positionOffset) * scale[1]);
                }
                if (position == 1) {
                    floatingActionButton.setRotation(positionOffset * 180);
                    floatingActionButtonSearch.setScaleX(positionOffset * scale[0]);
                    floatingActionButtonSearch.setScaleY(positionOffset * scale[1]);
                }
            }

            @Override
            public void onPageSelected(int position) {
                floatingActionButton.setRotation(180 - position * 180);
                floatingActionButton.setScaleX(scale[0]);
                floatingActionButton.setScaleY(scale[1]);
                switch (position) {
                    //TODO: сделать страницу создания группы
                    case 1:
                        floatingActionButton.show();
                        floatingActionButtonSearch.hide();
                        break;
                    default:
                        floatingActionButton.hide();
                        floatingActionButtonSearch.show();
                        GroupListFragment listFragment = mFragment1;
                        String[] parameters = mFragment2.getParams();
                        listFragment.updateGroups(parameters);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    void joinToGroup(int id) {
        Log.d(TAG, "joinToGroup: " + id);
    }

    void changePager(int pos) {
        mViewPager.setCurrentItem(pos);
    }

    static class GroupListViewPagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<Fragment> mFragments = new ArrayList<>();

        GroupListViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        void setFragments(ArrayList<Fragment> fragments) {
            mFragments = fragments;
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
