package ru.devdem.reminder.object;

public class User {
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
