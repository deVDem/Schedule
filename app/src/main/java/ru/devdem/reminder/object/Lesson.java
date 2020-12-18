package ru.devdem.reminder.object;

import java.util.Date;
import java.util.UUID;

import static java.util.UUID.randomUUID;

public class Lesson {
    private UUID mId;
    private int mNumber;
    private String mNumberText;
    private int mDay;
    private String mName;
    private Date mStart;
    private Date mEnd;
    private String mCab;
    private boolean mZamena;
    private String mDescription;

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }


    public boolean isZamena() {
        return mZamena;
    }

    public void setZamena(boolean zamena) {
        mZamena = zamena;
    }

    public String getCab() {
        return mCab;
    }

    public void setCab(String cab) {
        mCab = cab;
    }


    public Lesson() {
        this(randomUUID());
    }

    Lesson(UUID uuid) {
        mId = uuid;
    }

    public String getNumberText() {
        return mNumberText;
    }

    public void setNumberText(String numberText) {
        mNumberText = numberText;
    }

    public UUID getId() {
        return mId;
    }

    public int getNumber() {
        return mNumber;
    }

    public void setNumber(int number) {
        mNumber = number;
    }

    public int getDay() {
        return mDay;
    }

    public void setDay(int day) {
        mDay = day;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Date getStart() {
        return mStart;
    }

    public void setStart(Date start) {
        mStart = start;
    }

    public Date getEnd() {
        return mEnd;
    }

    public void setEnd(Date end) {
        mEnd = end;
    }
}
