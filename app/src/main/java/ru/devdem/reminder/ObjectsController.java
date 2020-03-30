package ru.devdem.reminder;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Date;

class ObjectsController {


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

        public User getAuthor() {
            return mAuthor;
        }

        public void setAuthor(User author) {
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
        return user;
    }
    public static class User {
        private int mId;
        private String mName;
        private String mLogin;
        private String mUrlImage;
        private String mEmail;
        private boolean mPro;
        private int mPermission;

        User() {

        }

        int getPermission() {
            return mPermission;
        }

        void setPermission(int permission) {
            mPermission = permission;
        }

        public String getEmail() {
            return mEmail;
        }

        public void setEmail(String email) {
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
