package ru.devdem.reminder.object;

import java.util.Date;

import ru.devdem.reminder.controllers.ObjectsController;
import ru.devdem.reminder.object.User;

public class Notification {
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
