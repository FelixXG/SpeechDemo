package com.flightmanager.speechdemo.Model;


import android.os.Parcel;
import android.os.Parcelable;

public class StartDate implements Parcelable {
    private String date;

    private String type;

    private String dateOrig;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDateOrig() {
        return dateOrig;
    }

    public void setDateOrig(String dateOrig) {
        this.dateOrig = dateOrig;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);
        dest.writeString(this.type);
        dest.writeString(this.dateOrig);
    }

    public StartDate() {
    }

    protected StartDate(Parcel in) {
        this.date = in.readString();
        this.type = in.readString();
        this.dateOrig = in.readString();
    }

    public static final Parcelable.Creator<StartDate> CREATOR = new Parcelable.Creator<StartDate>() {
        @Override
        public StartDate createFromParcel(Parcel source) {
            return new StartDate(source);
        }

        @Override
        public StartDate[] newArray(int size) {
            return new StartDate[size];
        }
    };
}
