package ru.devdem.reminder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Response;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.util.ArrayList;

public class GroupListActivity extends AppCompatActivity {
    private static final String TAG = "GroupListActivity";
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private GroupListFragment mFragment1;
    private GroupSearchFragment mFragment2;
    private ViewPager mViewPager;
    private float[] scale = new float[2];
    private static NetworkController mNetworkController;
    private RelativeLayout mLoadingLayout;
    private SharedPreferences mSettings;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, HelloActivity.class));
        overridePendingTransition(R.anim.transition_in_back, R.anim.transition_out_back);
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettings = getSharedPreferences("settings", MODE_PRIVATE);
        View view = View.inflate(this, R.layout.activity_grouplist, null);
        mNetworkController = NetworkController.get();
        mFragment1 = new GroupListFragment();
        mFragment2 = new GroupSearchFragment();
        setContentView(view);
        mViewPager = view.findViewById(R.id.viewPager2);
        mLoadingLayout = view.findViewById(R.id.loadingLayout);
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
        floatingActionButton.setOnClickListener(v -> mViewPager.setCurrentItem(0));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    floatingActionButton.setScaleX(positionOffset * scale[0]);
                    floatingActionButton.setScaleY(positionOffset * scale[1]);
                    floatingActionButtonSearch.setScaleX((1 - positionOffset) * scale[0]);
                    floatingActionButtonSearch.setScaleY((1 - positionOffset) * scale[1]);
                }
                if (position == 1) {
                    floatingActionButton.setScaleX((1 - positionOffset) * scale[0]);
                    floatingActionButton.setScaleY((1 - positionOffset) * scale[1]);
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
        mLoadingLayout.setVisibility(View.VISIBLE);
        Response.Listener<String> listener = response -> {
            Log.d(TAG, "joinToGroup: " + response);
            try {
                JSONObject jsonResponse = new JSONObject(response);
                String status = jsonResponse.getString("status");
                String error = jsonResponse.getString("error");
                if (status.equals("error")) {
                    if (error.equals("WRONG_TOKEN")) {
                        mSettings.edit().clear().apply();
                        mSettings.edit().putBoolean("notification", false).apply();
                        this.getSharedPreferences("jsondata", Context.MODE_PRIVATE).edit().clear().apply();
                        restart();
                    }
                    mLoadingLayout.setVisibility(View.GONE);
                    Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                } else if (status.equals("JOINED")) {
                    JSONObject group_info = jsonResponse.getJSONObject("group_info");
                    mSettings.edit()
                            .putString("group", group_info.getString("id"))
                            .putString("group_name", group_info.getString("name"))
                            .apply();
                    startActivity(new Intent(this, MainActivity.class));
                    overridePendingTransition(R.anim.transition_out, R.anim.transition_in);
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        Response.ErrorListener errorListener = error -> {
            mLoadingLayout.setVisibility(View.GONE);
            Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        };
        mNetworkController.joinToGroup(this, listener, errorListener, String.valueOf(id), mSettings.getString("token", null));
    }

    private void restart() {
        finish();
        startActivity(new Intent(this, SplashActivity.class));
        overridePendingTransition(R.anim.transition_out, R.anim.transition_in);
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
