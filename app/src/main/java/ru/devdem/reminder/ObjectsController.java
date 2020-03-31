package ru.devdem.reminder;

import android.content.SharedPreferences;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

class ObjectsController {


    static User parseUser(JSONObject jsonUser) {
        User user = new User();
        try {
            if (!jsonUser.isNull("id")) user.setId(jsonUser.getInt("id"));
            if (!jsonUser.isNull("name")) user.setName(jsonUser.getString("name"));
            if (!jsonUser.isNull("email")) user.setEmail(jsonUser.getString("email"));
            if (!jsonUser.isNull("login")) user.setLogin(jsonUser.getString("login"));
            if (!jsonUser.isNull("urlImage")) user.setUrlImage(jsonUser.getString("urlImage"));
            if (!jsonUser.isNull("pro")) user.setPro(jsonUser.getString("pro").equals("Yes"));
            if (!jsonUser.isNull("permission")) user.setPermission(jsonUser.getInt("permission"));
            if (!jsonUser.isNull("token")) user.setToken(jsonUser.getString("token"));
            if (!jsonUser.isNull("groups")) user.setGroupId(jsonUser.getString("groups"));
        } catch (Exception e) {
            e.printStackTrace();
            user = null;
        }
        return user;
    }

    static class Notification {
        private int mId;
        private String mTitle;
        private String mSubTitle;
        private String mUrlImage;
        private Date mDate;
        private User mAuthor;
        private int mGroup;

        Notification() {

        }

        User getAuthor() {
            return mAuthor;
        }

        void setAuthor(User author) {
            mAuthor = author;
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

    static User getLocalUserInfo(SharedPreferences settings) {
        User user = new User();
        user.setId(settings.getInt("user_id", 0));
        user.setName(settings.getString("name", "error"));
        user.setEmail(settings.getString("email", "error"));
        user.setLogin(settings.getString("login", "error"));
        user.setPro(settings.getBoolean("pro", false));
        user.setUrlImage(settings.getString("urlImage", null));
        user.setPermission(settings.getInt("permission", 0));
        user.setToken(settings.getString("token", "error"));
        user.setGroupId(settings.getString("group", "error"));
        return user;
    }

    public static class User {
        private int mId;
        private String mName;
        private String mLogin;
        private String mUrlImage;
        private String mEmail;
        private boolean mPro = false;
        private int mPermission;
        private String mToken;
        private String mGroupId;

        User() {

        }

        String getGroupId() {
            return mGroupId;
        }

        void setGroupId(String groupId) {
            mGroupId = groupId;
        }

        String getToken() {
            return mToken;
        }

        void setToken(String token) {
            mToken = token;
        }

        int getPermission() {
            return mPermission;
        }

        void setPermission(int permission) {
            mPermission = permission;
        }

        String getEmail() {
            return mEmail;
        }

        void setEmail(String email) {
            mEmail = email;
        }

        boolean isPro() {
            return mPro;
        }

        void setPro(boolean pro) {
            mPro = pro;
        }

        public int getId() {
            return mId;
        }

        public void setId(int mId) {
            this.mId = mId;
        }

        public String getName() {
            return mName;
        }

        public void setName(String mName) {
            this.mName = mName;
        }

        public String getLogin() {
            return mLogin;
        }

        public void setLogin(String mLogin) {
            this.mLogin = mLogin;
        }

        String getUrlImage() {
            return mUrlImage;
        }

        void setUrlImage(String mUrlImage) {
            this.mUrlImage = mUrlImage;
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

        ArrayList<User> getMembers() {
            return mMembers;
        }

        void setMembers(ArrayList<User> mMembers) {
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

        Boolean getConfirmed() {
            return mConfirmed;
        }

        void setConfirmed(Boolean mConfirmed) {
            this.mConfirmed = mConfirmed;
        }

        User getAuthor() {
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
}
