package ru.devdem.reminder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class NotificationsFragment extends Fragment {
    private SharedPreferences mSettings;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RVAdapter mRVAdapter;
    private NetworkController mNetworkController;

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, null);
        Context context = getContext();
        mNetworkController = NetworkController.get();
        String NAME_PREFS = "settings";
        mSettings = Objects.requireNonNull(context).getSharedPreferences(NAME_PREFS, Context.MODE_PRIVATE);
        mRecyclerView = view.findViewById(R.id.recyclerViewNotifications);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(llm);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeContainer);
        mSwipeRefreshLayout.setOnRefreshListener(this::createNotifications);
        createNotifications();
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_profile, menu);
        if (mSettings.getInt("permission", 0) < 1) {
            MenuItem item = menu.findItem(R.id.menu_edit);
            item.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_reload:
                createNotifications();
                return true;
            case R.id.menu_edit:
                startActivityForResult(new Intent(getActivity(), NewNotificationActivity.class), 154);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        mRVAdapter = null;
    }

    private void createNotifications() {
        mSwipeRefreshLayout.setRefreshing(true);
        Response.Listener<String> listener = response -> {
            ArrayList<Notification> mNotifications = new ArrayList<>();
            try {
                JSONObject object = new JSONObject(response);
                int all = object.getInt("all");
                for (int i = 0; i < all; i++) {
                    JSONObject jsonObject = object.getJSONObject(String.valueOf(i));
                    int group = jsonObject.getInt("group");
                    Notification notification = new Notification();
                    notification.setId(jsonObject.getInt("id"));
                    notification.setTitle(jsonObject.getString("Title"));
                    notification.setSubTitle(jsonObject.getString("Subtitle"));
                    notification.setUrlImage(jsonObject.getString("URLImage"));
                    notification.setGroup(group);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    notification.setDate(format.parse(jsonObject.getString("date")));
                    mNotifications.add(notification);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            updateUI(mNotifications);
            mSwipeRefreshLayout.setRefreshing(false);
        };
        Response.ErrorListener errorListener = error -> {

            ArrayList<Notification> mNotifications = new ArrayList<>();
            Notification notification = new Notification();
            notification.setDate(new Date());
            notification.setId(0);
            notification.setUrlImage("");
            notification.setTitle(getString(R.string.error));
            notification.setSubTitle(getString(R.string.swipedowntoretry));
            notification.setGroup(-1);
            mNotifications.add(notification);
            updateUI(mNotifications);
            mSwipeRefreshLayout.setRefreshing(false);
        };
        mNetworkController.getNotifications(getContext(), mSettings.getString("group", ""), mSettings.getString("token", ""), listener, errorListener);
    }

    private void updateUI(ArrayList<Notification> mNotifications) {
        mRVAdapter = new RVAdapter(mNotifications);
        ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(mRVAdapter);
        scaleInAnimationAdapter.setDuration(500);
        scaleInAnimationAdapter.setFirstOnly(true);
        scaleInAnimationAdapter.setInterpolator(new AccelerateDecelerateInterpolator());
        AlphaInAnimationAdapter animationAdapter = new AlphaInAnimationAdapter(scaleInAnimationAdapter);
        animationAdapter.setDuration(1000);
        animationAdapter.setFirstOnly(true);
        mRecyclerView.setAdapter(animationAdapter);
        mRVAdapter.notifyDataSetChanged();
    }

    static class Notification {
        private int mId;
        private String mTitle;
        private String mSubTitle;
        private String mUrlImage;
        private Date mDate;
        private int mGroup;

        Notification() {

        }

        int getGroup() {
            return mGroup;
        }

        void setGroup(int group) {
            mGroup = group;
        }

        int getId() {
            return mId;
        }

        void setId(int id) {
            mId = id;
        }

        String getTitle() {
            return mTitle;
        }

        void setTitle(String title) {
            mTitle = title;
        }

        String getSubTitle() {
            return mSubTitle;
        }

        void setSubTitle(String subTitle) {
            mSubTitle = subTitle;
        }

        String getUrlImage() {
            return mUrlImage;
        }

        void setUrlImage(String urlImage) {
            mUrlImage = urlImage;
        }

        Date getDate() {
            return mDate;
        }

        void setDate(Date date) {
            mDate = date;
        }
    }

    class RVAdapter extends RecyclerView.Adapter<RVAdapter.NotificationViewer> {
        ArrayList<Notification> mNotifications;
        boolean[] prepared;

        RVAdapter(ArrayList<Notification> notifications) {
            this.mNotifications = notifications;
            prepared = new boolean[mNotifications.size()];
        }

        @NonNull
        @Override
        public NotificationsFragment.RVAdapter.NotificationViewer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_view, parent, false);
            return new NotificationsFragment.RVAdapter.NotificationViewer(v);
        }

        @Override
        public void onBindViewHolder(@NonNull NotificationViewer holder, int position) {
            Notification notification = mNotifications.get(position);
            holder.mTitleView.setText(notification.getTitle());
            holder.mSubTitleView.setText(notification.getSubTitle());
            Date date = notification.getDate();
            String dateString = new SimpleDateFormat("d MMMM H:mm", Locale.getDefault()).format(date);
            holder.mDateView.setText(dateString);
            String urlImage = notification.getUrlImage();
            if (notification.getGroup() == -1) {
                holder.itemView.setBackgroundColor(getResources().getColor(R.color.notification_color_server));
            }
            if (urlImage.length() > 0 && holder.mImageView.getVisibility() == View.GONE) {
                holder.mImageView.setVisibility(View.VISIBLE);
                Picasso.get().load(urlImage).placeholder(R.drawable.cat).error(R.drawable.cat_error).into(holder.mImageView);
                holder.mImageView.setOnClickListener(v -> {
                    Activity activity = Objects.requireNonNull(getActivity());
                    startActivity(FullImageActivity.newInstance(activity, urlImage));
                    activity.overridePendingTransition(R.anim.transition_out, R.anim.transition_in);
                });
            } else holder.mImageView.setVisibility(View.GONE);
            if (position + 1 == getItemCount()) {
                holder.mSpace.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return mNotifications.size();
        }


        class NotificationViewer extends RecyclerView.ViewHolder {
            ImageView mImageView;
            TextView mTitleView;
            TextView mSubTitleView;
            TextView mDateView;
            Space mSpace;

            NotificationViewer(View v) {
                super(v);
                mImageView = v.findViewById(R.id.imageViewNotificationImage);
                mTitleView = v.findViewById(R.id.textViewTitle);
                mSubTitleView = v.findViewById(R.id.textViewSubTitle);
                mDateView = v.findViewById(R.id.textViewDate);
                mSpace = v.findViewById(R.id.space);
            }
        }
    }
}
