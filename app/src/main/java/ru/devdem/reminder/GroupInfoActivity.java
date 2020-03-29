package ru.devdem.reminder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupInfoActivity extends AppCompatActivity {
    private static final String TAG = "GroupInfoActivity";
    private int group_id = 0;
    private boolean go_button = false;
    private Group mGroup;
    private View v;
    private Thread mThread;
    private ThreadGroup mThreadGroup;

    public static Intent getAIntent(Context context, int group_id, boolean go_button) {
        Intent intent = new Intent(context, GroupInfoActivity.class);
        intent.putExtra("group_id", group_id);
        intent.putExtra("go_button", go_button);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            group_id = getIntent().getIntExtra("group_id", 0);
            go_button = getIntent().getBooleanExtra("go_button", false);
        }
        v = View.inflate(this, R.layout.activity_group_info, null);
        setContentView(v);
        FloatingActionButton actionButton = v.findViewById(R.id.floatingActionButton);
        actionButton.hide();
        RelativeLayout loadingLayout = v.findViewById(R.id.loadingLayout);
        loadingLayout.setVisibility(View.VISIBLE);
        NetworkController networkController = NetworkController.get();
        Response.Listener<String> listener = response -> {
            Log.d(TAG, "listener returned: " + response);
            try {
                JSONObject object = new JSONObject(response);
                int all = object.getInt("all");
                for (int i = 0; i < all; i++) {
                    JSONObject groupJson = object.getJSONObject(String.valueOf(i));
                    Group group = new Group();
                    group.setId(groupJson.getInt("id"));
                    group.setName(groupJson.getString("name"));
                    group.setCity(groupJson.getString("city"));
                    group.setBuilding(groupJson.getString("building"));
                    group.setDescription(groupJson.getString("description"));
                    group.setUrl(groupJson.getString("urlImage"));
                    group.setConfirmed(groupJson.getString("confirmed").equals("Yes"));
                    String date_created = groupJson.getString("date_created");
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    group.setDateCreated(!date_created.equals("null") ? format.parse(date_created) : new Date());
                    JSONObject usersJson = groupJson.getJSONObject("users");
                    int users_all = usersJson.getInt("users_all");
                    ArrayList<User> users = new ArrayList<>();
                    for (int j = 0; j < users_all; j++) {
                        JSONObject userJson = usersJson.getJSONObject(String.valueOf(j));
                        User user = new User();
                        user.setmId(userJson.getInt("id"));
                        user.setmName(userJson.getString("name"));
                        user.setmLogin(userJson.getString("login"));
                        user.setUrlImage(userJson.getString("urlImage"));
                        user.setmPro(userJson.getString("pro").equals("Yes"));
                        if (groupJson.getInt("author_id") == user.getmId()) {
                            group.setAuthor(user);
                        }
                        else group.setAuthor(null);
                        users.add(user);
                    }
                    group.setmMembers(users);
                    mGroup = group;
                }
                start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        Response.ErrorListener errorListener = error -> {
            Log.e(TAG, "errorListener: ", error);
            setResult(RESULT_CANCELED, null);
            finish();
        };
        networkController.getGroups(this, listener, errorListener, new String[]{"", "", "", "", String.valueOf(group_id), "true"});
    }

    void start() {
        CollapsingToolbarLayout toolbarLayout = v.findViewById(R.id.collapseToolbar);
        Toolbar toolbar = v.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v12 -> {
            setResult(RESULT_CANCELED, null);
            finish();
        });
        FloatingActionButton actionButton = v.findViewById(R.id.floatingActionButton);
        if (!go_button) actionButton.hide();
        else {
            actionButton.show();
            actionButton.setOnClickListener(v1 -> {
                Intent intent = new Intent();
                intent.putExtra("configured", true);
                intent.putExtra("group_id", group_id);
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.transition_in_back, R.anim.transition_out_back);
            });
        }
        TextView mTextName = v.findViewById(R.id.group_name);
        TextView mTextLocation = v.findViewById(R.id.group_location);
        TextView mTextDescription = v.findViewById(R.id.group_description);
        LinearLayout mLayoutConfirmed = v.findViewById(R.id.group_confirmed);
        RecyclerView mListUsers = v.findViewById(R.id.userList);
        ImageView imageView = v.findViewById(R.id.app_bar_image);
        Target mTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                if (bitmap == null) {
                    Log.w(TAG, "Null");
                } else {
                    mThread = new Thread(mThreadGroup, () -> {
                        try {
                            Log.d(TAG, "run: start");
                            int width = toolbar.getWidth();
                            int height = Math.round((float) width / bitmap.getWidth() * bitmap.getHeight());
                            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, width, height, true);
                            Bitmap pixel = Bitmap.createScaledBitmap(scaled, 1, 1, true);
                            int color = pixel.getPixel(0, 0);
                            int rez = 0xFFF - color + 0xFF000000;
                            toolbarLayout.setCollapsedTitleTextColor(ColorStateList.valueOf(rez));
                            toolbarLayout.setExpandedTitleColor(rez);
                            runOnUiThread(() -> {
                                imageView.setImageBitmap(scaled);
                                Window window = getWindow();
                                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                window.setStatusBarColor(color);
                                imageView.setForeground(getDrawable(R.drawable.bg_gradient));
                                int[][] states = new int[][]{
                                        new int[]{android.R.attr.state_enabled},
                                };
                                int[] colors = new int[]{
                                        color,
                                };
                                imageView.setForegroundTintList(new ColorStateList(states, colors));
                                toolbarLayout.setContentScrimColor(color);
                            });
                            Log.d(TAG, "run: ready");
                        } catch (Exception e) {
                            Log.e(TAG, "run: ", e);
                        }
                    }, "Background");
                    mThread.start();
                }
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                Log.e(TAG, "onBitmapFailed: ", e);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Log.i(TAG, "Prepare");
            }
        };
        if (!mGroup.getUrl().equals("null")) Picasso.get().load(mGroup.getUrl()).into(mTarget);
        else Picasso.get().load(R.drawable.cat_error).into(mTarget);
        toolbar.setTitle(mGroup.getName());
        mTextName.setText(mGroup.getName());
        String location = mGroup.getBuilding() + ", " + mGroup.getCity();
        mTextLocation.setText(location);
        mTextDescription.setText(mGroup.getDescription().equals("null") ? getString(R.string.no_description) : mGroup.getDescription());
        mLayoutConfirmed.setVisibility(mGroup.getConfirmed() ? View.VISIBLE : View.GONE);
        FrameLayout frameAuthor = v.findViewById(R.id.authorFrame);
        View authorView = View.inflate(this, R.layout.group_info_user_view, null);
        TextView authorLogin = authorView.findViewById(R.id.profileLogin);
        TextView authorName = authorView.findViewById(R.id.profileName);
        CardView authorCard = authorView.findViewById(R.id.card_view);
        CircleImageView authorImage = authorView.findViewById(R.id.profileImage);
        User author = mGroup.getAuthor();
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Log.d(TAG, "onBitmapLoaded: loaded");
                new Thread(mThreadGroup, () -> {
                    int width = 250;
                    int height = Math.round((float) width / bitmap.getWidth() * bitmap.getHeight());
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap, width, height, true);
                    Bitmap preparePixel = Bitmap.createScaledBitmap(scaled, 1, 1, true);
                    int color = preparePixel.getPixel(0, 0);
                    int rez = 0xFFF - color + 0xFF000000;
                    float[] hsv = new float[3];
                    Color.colorToHSV(rez, hsv);
                    hsv[0] = hsv[0] + 180;
                    int cardColor = Color.HSVToColor(hsv);
                    int textColor = -1 * cardColor + 0xFF000000;
                    runOnUiThread(() -> {
                        authorImage.setImageBitmap(scaled);
                        authorImage.setBorderColor(textColor);
                        if (author == null || author.ismPro()) {
                            int[][] states = new int[][]{
                                    new int[]{android.R.attr.state_enabled}
                            };
                            int[] colors = new int[]{
                                    cardColor
                            };
                            authorName.setTextColor(textColor);
                            authorLogin.setTextColor(textColor);
                            authorCard.setBackgroundTintList(new ColorStateList(states, colors));
                        }
                        new CountDownTimer(2000, 16) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                float alpha = (10000f - millisUntilFinished) / 10000f;
                                authorImage.setAlpha(alpha);
                            }

                            @Override
                            public void onFinish() {
                                authorImage.setAlpha(1.0f);
                            }
                        }.start();
                    });
                }, "Author background").start();
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                Log.e(TAG, "onBitmapFailed: ", e);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                authorImage.setAlpha(0.0f);
                Log.i(TAG, "onPrepareLoad: loading");
            }
        };
        if (author != null) {
            String login = "@" + author.getmLogin();
            authorLogin.setText(login);
            authorName.setText(author.getmName());
            if (!author.getUrlImage().equals("null")) {
                Picasso.get().load(author.getUrlImage()).into(target);
            }
        } else {
            authorName.setText(R.string.system);
            Picasso.get().load("https://files.devdem.ru/apps/schedule/user_images/server.jpg").into(target);
        }
        frameAuthor.addView(authorView);
        mListUsers.setLayoutManager(new LinearLayoutManager(this));
        mListUsers.setHasFixedSize(true);
        MembersAdapter adapter = new MembersAdapter();
        if (mThreadGroup == null) mThreadGroup = new ThreadGroup("Card Backgrounds");
        adapter.setMembers(mGroup.getmMembers(), this, mThreadGroup);
        mListUsers.setAdapter(adapter);
        RelativeLayout loadingLayout = v.findViewById(R.id.loadingLayout);
        YoYo.with(Techniques.TakingOff).duration(700).interpolate(new AccelerateDecelerateInterpolator()).onEnd(animator -> loadingLayout.setVisibility(View.GONE)).playOn(loadingLayout);
    }

    @Override
    protected void onDestroy() {
        if (mThread != null) mThread.interrupt();
        if (mThreadGroup != null) mThreadGroup.interrupt();
        super.onDestroy();
    }

    public static class User {
        private int mId;
        private String mName;
        private String mLogin;
        private String urlImage;
        private boolean mPro;

        boolean ismPro() {
            return mPro;
        }

        void setmPro(boolean pro) {
            mPro = pro;
        }

        User() {

        }

        public int getmId() {
            return mId;
        }

        public void setmId(int mId) {
            this.mId = mId;
        }

        public String getmName() {
            return mName;
        }

        public void setmName(String mName) {
            this.mName = mName;
        }

        public String getmLogin() {
            return mLogin;
        }

        public void setmLogin(String mLogin) {
            this.mLogin = mLogin;
        }

        public String getUrlImage() {
            return urlImage;
        }

        public void setUrlImage(String urlImage) {
            this.urlImage = urlImage;
        }
    }

    public static class Group {
        private int mId;
        private String mName;
        private String mCity;
        private String mBuilding;
        private String mDescription;
        private String mUrl;
        private Boolean mConfirmed;
        private User mAuthor;
        private Date mDateCreated;
        private ArrayList<User> mMembers = new ArrayList<>();

        Group() {

        }

        public ArrayList<User> getmMembers() {
            return mMembers;
        }

        public void setmMembers(ArrayList<User> mMembers) {
            this.mMembers = mMembers;
        }

        public int getId() {
            return mId;
        }

        public void setId(int id) {
            mId = id;
        }

        public String getName() {
            return mName;
        }

        public void setName(String mName) {
            this.mName = mName;
        }

        String getCity() {
            return mCity;
        }

        void setCity(String mCity) {
            this.mCity = mCity;
        }

        String getBuilding() {
            return mBuilding;
        }

        void setBuilding(String mBuilding) {
            this.mBuilding = mBuilding;
        }

        String getDescription() {
            return mDescription;
        }

        void setDescription(String mDescription) {
            this.mDescription = mDescription;
        }

        String getUrl() {
            return mUrl;
        }

        void setUrl(String mUrl) {
            this.mUrl = mUrl;
        }

        public Boolean getConfirmed() {
            return mConfirmed;
        }

        void setConfirmed(Boolean mConfirmed) {
            this.mConfirmed = mConfirmed;
        }

        public User getAuthor() {
            return mAuthor;
        }

        void setAuthor(User author) {
            this.mAuthor = author;
        }

        Date getDateCreated() {
            return mDateCreated;
        }

        void setDateCreated(Date mDateCreated) {
            this.mDateCreated = mDateCreated;
        }

    }

    public static class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MemberViewer> {

        ArrayList<User> members;
        private ThreadGroup mThreadGroup;
        private Activity mActivity;

        MembersAdapter() {

        }

        void setMembers(ArrayList<User> memberList, Activity activity, ThreadGroup threadGroup) {
            members = memberList;
            if (members.size() < 1) members.add(new User());
            mThreadGroup = threadGroup;
            mActivity = activity;
        }

        @NonNull
        @Override
        public MemberViewer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_info_user_view, parent, false);
            return new MemberViewer(v);
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
        public void onBindViewHolder(@NonNull MemberViewer holder, int position) {
            User user = members.get(position);
            if (position == 0 && user.getmLogin() == null) {
                holder.mTextNoMembers.setVisibility(View.VISIBLE);
                holder.mCardView.setVisibility(View.GONE);
            } else {
                String login = "@" + user.getmLogin();
                holder.mTextLogin.setText(login);
                holder.mTextName.setText(user.getmName());
                if (!user.getUrlImage().equals("null")) {
                    Target target = new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            Log.d(TAG, "onBitmapLoaded: loaded");
                            new Thread(mThreadGroup, () -> {
                                int width = 250;
                                int height = Math.round((float) width / bitmap.getWidth() * bitmap.getHeight());
                                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, width, height, true);
                                Bitmap preparePixel = Bitmap.createScaledBitmap(scaled, 1, 1, true);
                                int color = preparePixel.getPixel(0, 0);
                                int rez = 0xFFF - color + 0xFF000000;
                                float[] hsv = new float[3];
                                Color.colorToHSV(rez, hsv);
                                hsv[0] = hsv[0] + 180;
                                int cardColor = Color.HSVToColor(hsv);
                                int textColor = -1 * cardColor + 0xFF000000;
                                mActivity.runOnUiThread(() -> {
                                    holder.mImageProfile.setImageBitmap(scaled);
                                    holder.mImageProfile.setBorderColor(textColor);
                                    if (user.ismPro()) {
                                        int[][] states = new int[][]{
                                                new int[]{android.R.attr.state_enabled}
                                        };
                                        int[] colors = new int[]{
                                                cardColor
                                        };
                                        holder.mTextName.setTextColor(textColor);
                                        holder.mTextLogin.setTextColor(textColor);
                                        holder.mCardView.setBackgroundTintList(new ColorStateList(states, colors));
                                    }
                                    new CountDownTimer(2000, 16) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            float alpha = (10000f - millisUntilFinished) / 10000f;
                                            holder.mImageProfile.setAlpha(alpha);
                                        }

                                        @Override
                                        public void onFinish() {
                                            holder.mImageProfile.setAlpha(1.0f);
                                        }
                                    }.start();
                                });
                            }, "Card background " + position).start();
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            Log.e(TAG, "onBitmapFailed: ", e);
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                            holder.mImageProfile.setAlpha(0.0f);
                            Log.i(TAG, "onPrepareLoad: loading");
                        }
                    };
                    Picasso.get().load(user.getUrlImage()).into(target);
                }
            }
            if (position == members.size()) holder.mSpace.setVisibility(View.VISIBLE);
        }

        @Override
        public int getItemCount() {
            return members.size();
        }

        static class MemberViewer extends RecyclerView.ViewHolder {
            TextView mTextName;
            TextView mTextLogin;
            TextView mTextNoMembers;
            RelativeLayout mRLCard;
            Space mSpace;
            CircleImageView mImageProfile;
            CardView mCardView;


            MemberViewer(View itemView) {
                super(itemView);
                mRLCard = itemView.findViewById(R.id.relativeLayoutCard);
                mTextName = itemView.findViewById(R.id.profileName);
                mTextLogin = itemView.findViewById(R.id.profileLogin);
                mSpace = itemView.findViewById(R.id.space);
                mImageProfile = itemView.findViewById(R.id.profileImage);
                mTextNoMembers = itemView.findViewById(R.id.no_members);
                mCardView = itemView.findViewById(R.id.card_view);
            }
        }
    }
}
