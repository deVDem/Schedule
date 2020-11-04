package ru.devdem.reminder.ui.main;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
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

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import ru.devdem.reminder.R;
import ru.devdem.reminder.controllers.NetworkController;
import ru.devdem.reminder.controllers.ObjectsController;
import ru.devdem.reminder.controllers.ObjectsController.Notification;
import ru.devdem.reminder.controllers.ObjectsController.User;
import ru.devdem.reminder.ui.FullImageActivity;

import static android.app.Activity.RESULT_OK;


public class NotificationsFragment extends Fragment {
    private SharedPreferences mSettings;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RVAdapter mRVAdapter;
    private NetworkController mNetworkController;
    private boolean needReload = false;

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
        /*TODO : А еще было бы клево если б можно было в объявлении тег предмета поставить и
           переходить к объявлениям только этого предмета по клику на название в расписании */
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_notifications, menu);
        MenuItem item = menu.findItem(R.id.menu_edit);
        item.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_reload:
                if (!mSwipeRefreshLayout.isRefreshing()) createNotifications();
                return true;
            case R.id.menu_edit:
                // TODO: починить отправлялку сообщений
                //startActivityForResult(new Intent(getActivity(), NewNotificationActivity.class), 154);
                Toast.makeText(requireContext(), "Временно не работает", Toast.LENGTH_LONG).show();
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
                    User author;
                    if (!jsonObject.isNull("author"))
                        author = ObjectsController.parseUser(jsonObject.getJSONObject("author"));
                    else {
                        author = new User();
                        author.setNames(getString(R.string.system));
                        /*author.setImageId("https://files.devdem.ru/apps/schedule/user_images/server.jpg");*/
                        author.setLogin("system");
                    }
                    notification.setAuthor(author);
                    mNotifications.add(notification);
                }
                if (needReload)
                    mSettings.edit().putBoolean("first_notifications", false).putInt("notifications_all_service", all).apply();
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
            User author = new User();
            author.setNames(getString(R.string.system));
            /*author.setImageId("https://files.devdem.ru/apps/schedule/user_images/server.jpg");*/
            author.setLogin("system");
            notification.setAuthor(author);
            mNotifications.add(notification);
            updateUI(mNotifications);
            mSwipeRefreshLayout.setRefreshing(false);
        };
        mNetworkController.getNotifications(getContext(), ObjectsController.getLocalUserInfo(mSettings).getGroupId(), ObjectsController.getLocalUserInfo(mSettings).getToken(), listener, errorListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 154 && resultCode == RESULT_OK) {
            createNotifications();
        }
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
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(@NonNull NotificationViewer holder, int position) {
            if (!prepared[position]) {
                Notification notification = mNotifications.get(position);
                User author = notification.getAuthor();
                holder.mSubTitleView.setText(notification.getSubTitle());
                CardView cardView = (CardView) holder.itemView;
                if (notification.getGroup() == -1) {
                    cardView.setCardBackgroundColor(getResources().getColor(R.color.notification_color_server, Objects.requireNonNull(getContext()).getTheme()));
                }
                if (author != null) {
                    holder.mAuthorName.setText(author.getNames());
                    holder.mAuthorPro.setVisibility(author.isPro() ? View.VISIBLE : View.GONE);
                    String login = "@" + author.getLogin();
                    holder.mAuthorLogin.setText(login);
                    /*if (author.getImageId().length() > 5) {
                        Target target = new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                CircleImageView targetView = holder.mAuthorImage;
                                int width = 250;
                                int height = Math.round((float) width / bitmap.getWidth() * bitmap.getHeight());
                                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, width, height, true);
                                Bitmap preparePixel = Bitmap.createScaledBitmap(scaled, 1, 1, true);
                                targetView.setImageBitmap(scaled);
                                int color = preparePixel.getPixel(0, 0);
                                targetView.setBorderColor(color);
                                int rez = getResources().getColor(R.color.card_color) - color + 0xFF000000;
                                holder.mAuthorPro.setColorFilter(rez);
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        };
                        Picasso.get().load(author.getImageId()).into(target);
                    }*/
                }
                holder.mTitleView.setText(notification.getTitle());
                Date date = notification.getDate();
                String dateString = new SimpleDateFormat("d MMMM H:mm", Locale.getDefault()).format(date);
                holder.mDateView.setText(dateString);
                String urlImage = notification.getUrlImage();
                if (urlImage.length() > 0) {
                    holder.mImageView.setVisibility(View.VISIBLE);
                    Picasso.get().load(urlImage).placeholder(R.drawable.cat).error(R.drawable.cat_error).into(holder.mImageView);
                    holder.mImageView.setOnClickListener(v -> {
                        Activity activity = requireActivity();
                        startActivity(FullImageActivity.newInstance(activity, urlImage));
                        activity.overridePendingTransition(R.anim.transition_out, R.anim.transition_in);
                    });
                } else holder.mImageView.setVisibility(View.GONE);
                if (position + 1 == getItemCount()) {
                    holder.mSpace.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public int getItemCount() {
            return mNotifications.size();
        }


        class NotificationViewer extends RecyclerView.ViewHolder {
            CircleImageView mAuthorImage;
            TextView mAuthorName;
            ImageView mAuthorPro;
            TextView mAuthorLogin;
            ImageView mImageView;
            TextView mTitleView;
            TextView mSubTitleView;
            TextView mDateView;
            Space mSpace;

            NotificationViewer(View v) {
                super(v);
                mAuthorImage = v.findViewById(R.id.authorImage);
                mAuthorName = v.findViewById(R.id.authorName);
                mAuthorPro = v.findViewById(R.id.proImage);
                mAuthorLogin = v.findViewById(R.id.authorLogin);
                mImageView = v.findViewById(R.id.imageViewNotificationImage);
                mTitleView = v.findViewById(R.id.textViewTitle);
                mSubTitleView = v.findViewById(R.id.textViewSubTitle);
                mDateView = v.findViewById(R.id.textViewDate);
                mSpace = v.findViewById(R.id.space);
            }
        }
    }
}
