package com.felix.speechdemo.Model;


import android.os.Parcel;
import android.os.Parcelable;

public class Semantic implements Parcelable {
    private Slots slots;

    public Slots getSlots() {
        return slots;
    }

    public void setSlots(Slots slots) {
        this.slots = slots;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.slots, flags);
    }

    public Semantic() {
    }

    protected Semantic(Parcel in) {
        this.slots = in.readParcelable(Slots.class.getClassLoader());
    }

    public static final Parcelable.Creator<Semantic> CREATOR = new Parcelable.Creator<Semantic>() {
        @Override
        public Semantic createFromParcel(Parcel source) {
            return new Semantic(source);
        }

        @Override
        public Semantic[] newArray(int size) {
            return new Semantic[size];
        }
    };
}
