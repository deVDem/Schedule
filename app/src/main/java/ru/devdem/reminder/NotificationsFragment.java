package ru.devdem.reminder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class NotificationsFragment extends Fragment {
    private String DIALOG_PHOTO = "fullphoto";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.fragment_notifications, null);
        Context context = getContext();
        RecyclerView mRecyclerView = v.findViewById(R.id.recyclerViewNotifications);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(llm);
        RVAdapter adapter = new RVAdapter(createNotifications());
        ScaleInAnimationAdapter animationAdapter = new ScaleInAnimationAdapter(adapter);
        animationAdapter.setDuration(500);
        animationAdapter.setFirstOnly(false);
        animationAdapter.setInterpolator(new AccelerateDecelerateInterpolator());
        mRecyclerView.setAdapter(animationAdapter);
        return v;
    }

    private ArrayList<Notification> createNotifications() {
        ArrayList<Notification> mNotifications = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Notification notification = new Notification();
            notification.setId(i);
            notification.setTitle("Заголовок " + i);
            notification.setSubTitle("Мы просто намбер " + i + "и..\nмного-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много--много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много--много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много--много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много--много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много--много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много-много текса");
            notification.setDate(new Date());
            if (new Random().nextBoolean())
                notification.setUrlImage("https://pbs.twimg.com/media/DGVoayKU0AEr-k7.jpg:large");
            else
                notification.setUrlImage("https://cs6.pikabu.ru/post_img/big/2014/05/12/10/1399912497_442275426.JPG");
            mNotifications.add(notification);
        }


        return mNotifications;
    }

    public class Notification {
        private int mId;
        private String mTitle;
        private String mSubTitle;
        private String mUrlImage;
        private Date mDate;

        Notification() {

        }

        public int getId() {
            return mId;
        }

        public void setId(int id) {
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

        RVAdapter(ArrayList<Notification> notifications) {
            this.mNotifications = notifications;
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
            String dateString = new SimpleDateFormat("d MMMM HH:mm:ss", Locale.getDefault()).format(date);
            holder.mDateView.setText(dateString);
            String urlImage = notification.getUrlImage();
            if (urlImage != null) {
                Picasso.get().load(urlImage).into(holder.mImageView);
                holder.mImageView.setOnClickListener(v -> {
                    FragmentManager manager = getFragmentManager();
                    DialogFullImageFragment dialog = DialogFullImageFragment.newInstance(notification.getUrlImage());
                    dialog.show(Objects.requireNonNull(manager), DIALOG_PHOTO);
                });
            } else holder.mImageView.setVisibility(View.GONE);
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

            NotificationViewer(View v) {
                super(v);
                mImageView = v.findViewById(R.id.imageViewNotificationImage);
                mTitleView = v.findViewById(R.id.textViewTitle);
                mSubTitleView = v.findViewById(R.id.textViewSubTitle);
                mDateView = v.findViewById(R.id.textViewDate);
            }
        }
    }
}
