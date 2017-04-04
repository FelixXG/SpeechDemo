package com.felix.speechdemo.Model;


import android.os.Parcel;
import android.os.Parcelable;

public class Slots implements Parcelable {
    private StartDate startDate;

    private String airline;

    private String flightno;

    public String getFlightno() {
        return flightno;
    }

    public void setFlightno(String flightno) {
        this.flightno = flightno;
    }

    private EndLoc endLoc;

    private StartLoc startLoc;

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public EndLoc getEndLoc() {
        return endLoc;
    }

    public void setEndLoc(EndLoc endLoc) {
        this.endLoc = endLoc;
    }

    public StartDate getStartDate() {
        return startDate;
    }

    public void setStartDate(StartDate startDate) {
        this.startDate = startDate;
    }

    public StartLoc getStartLoc() {
        return startLoc;
    }

    public void setStartLoc(StartLoc startLoc) {
        this.startLoc = startLoc;
    }

    public Slots() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.startDate, flags);
        dest.writeString(this.airline);
        dest.writeString(this.flightno);
        dest.writeParcelable(this.endLoc, flags);
        dest.writeParcelable(this.startLoc, flags);
    }

    protected Slots(Parcel in) {
        this.startDate = in.readParcelable(StartDate.class.getClassLoader());
        this.airline = in.readString();
        this.flightno = in.readString();
        this.endLoc = in.readParcelable(EndLoc.class.getClassLoader());
        this.startLoc = in.readParcelable(StartLoc.class.getClassLoader());
    }

    public static final Creator<Slots> CREATOR = new Creator<Slots>() {
        @Override
        public Slots createFromParcel(Parcel source) {
            return new Slots(source);
        }

        @Override
        public Slots[] newArray(int size) {
            return new Slots[size];
        }
    };
}
