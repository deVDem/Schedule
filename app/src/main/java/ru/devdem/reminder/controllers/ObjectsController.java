package ru.devdem.reminder.controllers;

import android.content.SharedPreferences;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class ObjectsController {


    public static User parseUser(JSONObject jsonUser) {
        User user = new User();
        try {
            if (!jsonUser.isNull("id")) user.setId(jsonUser.getInt("id"));
            if (!jsonUser.isNull("names")) user.setNames(jsonUser.getString("names"));
            if (!jsonUser.isNull("email")) user.setEmail(jsonUser.getString("email"));
            if (!jsonUser.isNull("login")) user.setLogin(jsonUser.getString("login"));
            if (!jsonUser.isNull("imageId")) user.setImageId(jsonUser.getInt("imageId"));
            if (!jsonUser.isNull("pro")) user.setPro(jsonUser.getString("pro").equals("Yes"));
            if (!jsonUser.isNull("token")) user.setToken(jsonUser.getString("token"));
            if (!jsonUser.isNull("groupId")) user.setGroupId(jsonUser.getString("groupId"));
        } catch (Exception e) {
            e.printStackTrace();
            user = null;
        }
        return user;
    }

    public static class Notification {
        private int mId;
        private String mTitle;
        private String mSubTitle;
        private String mUrlImage;
        private Date mDate;
        private User mAuthor;
        private int mGroup;

        public Notification() {

        }

        public User getAuthor() {
            return mAuthor;
        }

        public void setAuthor(User author) {
            mAuthor = author;
        }

        public int getGroup() {
            return mGroup;
        }

        public void setGroup(int group) {
            mGroup = group;
        }

        public int getId() {
            return mId;
        }

        public void setId(int id) {
            mId = id;
        }

        public String getTitle() {
            return mTitle;
        }

        public void setTitle(String title) {
            mTitle = title;
        }

        public String getSubTitle() {
            return mSubTitle;
        }

        public void setSubTitle(String subTitle) {
            mSubTitle = subTitle;
        }

        public String getUrlImage() {
            return mUrlImage;
        }

        public void setUrlImage(String urlImage) {
            mUrlImage = urlImage;
        }

        public Date getDate() {
            return mDate;
        }

        public void setDate(Date date) {
            mDate = date;
        }
    }

    public static User getLocalUserInfo(SharedPreferences settings) {
        User user = new User();
        user.setId(settings.getInt("user_id", 0));
        user.setNames(settings.getString("name", "error"));
        user.setEmail(settings.getString("email", "error"));
        user.setLogin(settings.getString("login", "error"));
        user.setPro(settings.getBoolean("pro", false));
        user.setImageId(settings.getInt("imageId", 0));
        user.setToken(settings.getString("token", "error"));
        user.setGroupId(settings.getString("group", "error"));
        return user;
    }

    public static class User {
        private int mId;
        private String mLogin;
        private String mNames;
        private int mImageId;
        private String mEmail;
        private boolean mPro = false;
        private String mToken;
        private String mGroupId;

        public User() {

        }

        public String getGroupId() {
            return mGroupId;
        }

        public void setGroupId(String groupId) {
            mGroupId = groupId;
        }

        public String getToken() {
            return mToken;
        }

        public void setToken(String token) {
            mToken = token;
        }

        public String getEmail() {
            return mEmail;
        }

        public void setEmail(String email) {
            mEmail = email;
        }

        public boolean isPro() {
            return mPro;
        }

        public void setPro(boolean pro) {
            mPro = pro;
        }

        public int getId() {
            return mId;
        }

        public void setId(int mId) {
            this.mId = mId;
        }

        public String getNames() {
            return mNames;
        }

        public void setNames(String mName) {
            this.mNames = mName;
        }

        public String getLogin() {
            return mLogin;
        }

        public void setLogin(String mLogin) {
            this.mLogin = mLogin;
        }

        public int getImageId() {
            return mImageId;
        }

        public void setImageId(int mImageId) {
            this.mImageId = mImageId;
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

        public Group() {

        }

        public ArrayList<User> getMembers() {
            return mMembers;
        }

        public void setMembers(ArrayList<User> mMembers) {
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

        public String getCity() {
            return mCity;
        }

        public void setCity(String mCity) {
            this.mCity = mCity;
        }

        public String getBuilding() {
            return mBuilding;
        }

        public void setBuilding(String mBuilding) {
            this.mBuilding = mBuilding;
        }

        public String getDescription() {
            return mDescription;
        }

        public void setDescription(String mDescription) {
            this.mDescription = mDescription;
        }

        public String getUrl() {
            return mUrl;
        }

        public void setUrl(String mUrl) {
            this.mUrl = mUrl;
        }

        public boolean getConfirmed() {
            return mConfirmed;
        }

        public void setConfirmed(boolean mConfirmed) {
            this.mConfirmed = mConfirmed;
        }

        public User getAuthor() {
            return mAuthor;
        }

        public void setAuthor(User author) {
            this.mAuthor = author;
        }

        public Date getDateCreated() {
            return mDateCreated;
        }

        public void setDateCreated(Date mDateCreated) {
            this.mDateCreated = mDateCreated;
        }

    }
}
