package ru.devdem.reminder.object;

import java.util.ArrayList;
import java.util.Date;

public class Group {
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
