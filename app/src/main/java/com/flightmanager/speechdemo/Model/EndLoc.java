package com.flightmanager.speechdemo.Model;


import android.os.Parcel;
import android.os.Parcelable;

public class EndLoc implements Parcelable {
    private String type;

    private String poi;

    private String city;

    private String cityAddr;

    public String getCityAddr() {
        return cityAddr;
    }

    public void setCityAddr(String cityAddr) {
        this.cityAddr = cityAddr;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPoi() {
        return poi;
    }

    public void setPoi(String poi) {
        this.poi = poi;
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
        dest.writeString(this.type);
        dest.writeString(this.poi);
        dest.writeString(this.city);
        dest.writeString(this.cityAddr);
    }

    public EndLoc() {
    }

    protected EndLoc(Parcel in) {
        this.type = in.readString();
        this.poi = in.readString();
        this.city = in.readString();
        this.cityAddr = in.readString();
    }

    public static final Parcelable.Creator<EndLoc> CREATOR = new Parcelable.Creator<EndLoc>() {
        @Override
        public EndLoc createFromParcel(Parcel source) {
            return new EndLoc(source);
        }

        @Override
        public EndLoc[] newArray(int size) {
            return new EndLoc[size];
        }
    };
}
