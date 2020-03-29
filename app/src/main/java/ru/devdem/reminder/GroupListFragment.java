package ru.devdem.reminder;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class GroupListFragment extends Fragment {
    private static NetworkController networkController;
    private RecyclerView mRecyclerView;
    private RelativeLayout mLoadingLayout;
    private RelativeLayout mErrorLayout;
    private GroupListActivity activity;
    private GroupAdapter groupAdapter;
    private String[] lastParams = new String[4];

    public GroupListFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = Objects.requireNonNull((GroupListActivity) getActivity());
        networkController = NetworkController.get();
        groupAdapter = new GroupAdapter();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_grouplist, null);
        mRecyclerView = view.findViewById(R.id.groupListRecycler);
        mLoadingLayout = view.findViewById(R.id.loadingLayout);
        mErrorLayout = view.findViewById(R.id.errorLayout);
        Button errorButton = mErrorLayout.findViewById(R.id.retryFind);
        errorButton.setOnClickListener(v -> activity.changePager(1));
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(activity);
        mRecyclerView.setLayoutManager(llm);
        updateGroups(null);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.group_list);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        return view;
    }

    void updateGroups(String[] params) {
        if (!Arrays.equals(lastParams, params)) {
            lastParams = params;
            mLoadingLayout.setVisibility(View.VISIBLE);
            mErrorLayout.setVisibility(View.GONE);
            Response.Listener<String> listener = response -> {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int all = jsonObject.getInt("all");
                    ArrayList<Group> groups = new ArrayList<>();
                    for (int i = 0; i < all; i++) {
                        JSONObject groupjson = jsonObject.getJSONObject(String.valueOf(i));
                        Group group = new Group();
                        int id = groupjson.getInt("id");
                        String name = groupjson.getString("name");
                        String city = groupjson.getString("city");
                        String building = groupjson.getString("building");
                        String description = groupjson.getString("description");
                        String urlImage = groupjson.getString("urlImage");
                        String confirmed = groupjson.getString("confirmed");
                        String date_created = groupjson.getString("date_created");
                        group.setId(id);
                        group.setName(!name.equals("null") ? name : "");
                        group.setCity(!city.equals("null") ? city : "");
                        group.setBuilding(!building.equals("null") ? building : "");
                        group.setDescription(!description.equals("null") ? description : getString(R.string.no_description));
                        group.setUrl(!urlImage.equals("null") ? urlImage : "");
                        group.setConfirmed(confirmed.equals("Yes"));
                        // TODO: сделать автора
                        group.setAuthor(new User());
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        group.setDateCreated(!date_created.equals("null") ? format.parse(date_created) : new Date());
                        groups.add(group);
                    }
                    if (groups.size() < 1) {
                        mErrorLayout.setVisibility(View.VISIBLE);
                    } else {
                        groupAdapter.setGroups(groups);
                        mRecyclerView.setAdapter(groupAdapter);
                    }
                    mLoadingLayout.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            Response.ErrorListener errorListener = error -> {
                //test 2
                mErrorLayout.setVisibility(View.VISIBLE);
                // TODO: доделать индикацию ошибок
            };
            networkController.getGroups(activity, listener, errorListener, params);
        }
    }

    public static class User {
        private int mId;
        private String mName;
        private String mLogin;
        private String urlImage;

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

    public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupListViewer> {

        ArrayList<Group> mGroups;

        GroupAdapter() {

        }

        void setGroups(ArrayList<Group> groups) {
            mGroups = groups;
        }

        @NonNull
        @Override
        public GroupListViewer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_item, parent, false);
            return new GroupListViewer(v);
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
        public void onBindViewHolder(@NonNull GroupListViewer holder, int position) {
            Group group = mGroups.get(position);
            holder.mGroupName.setText(group.getName());
            holder.mBuilding.setText(group.getBuilding());
            holder.mCity.setText(group.getCity());
            if (group.getUrl().length() > 1) Picasso.get().load(group.getUrl()).into(holder.mImage);
            String description = group.getDescription();
            Date groupCreated = group.getDateCreated();
            if (groupCreated != null) {
                int year = Calendar.getInstance().YEAR;
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(groupCreated);
                int yearGroup = Calendar.YEAR;
                String dateString;
                if (year != yearGroup)
                    dateString = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(groupCreated);
                else
                    dateString = new SimpleDateFormat("d MMMM", Locale.getDefault()).format(groupCreated);
                description += "\nДата создания: " + dateString;
            }
            holder.mDescription.setText(description);
            holder.mGoButton.setOnClickListener(v -> activity.joinToGroup(group.getId()));
            holder.mDetailedButton.setOnClickListener(v -> activity.detailedGroup(group.getId()));
            if (position == mGroups.size() - 1) holder.mSpace.setVisibility(View.VISIBLE);
        }

        @Override
        public int getItemCount() {
            return mGroups.size();
        }

        class GroupListViewer extends RecyclerView.ViewHolder {
            RelativeLayout mRelativeLayout;
            ImageView mImage;
            TextView mGroupName;
            TextView mBuilding;
            TextView mDescription;
            TextView mCity;
            Button mGoButton;
            Button mDetailedButton;
            Space mSpace;

            GroupListViewer(View itemView) {
                super(itemView);
                mRelativeLayout = itemView.findViewById(R.id.relativeLayoutCard);
                mImage = itemView.findViewById(R.id.imageViewGroup);
                mGroupName = itemView.findViewById(R.id.textViewTitle);
                mBuilding = itemView.findViewById(R.id.textViewBuilding);
                mDescription = itemView.findViewById(R.id.textViewSubTitle);
                mGoButton = itemView.findViewById(R.id.btnJoinToGroup);
                mDetailedButton = itemView.findViewById(R.id.btnInfoGroup);
                mSpace = itemView.findViewById(R.id.space);
                mCity = itemView.findViewById(R.id.textViewCity);
            }
        }
    }
}
