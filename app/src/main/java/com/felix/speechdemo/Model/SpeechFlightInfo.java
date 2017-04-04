package com.flightmanager.speechdemo.Model;


import android.os.Parcel;
import android.os.Parcelable;

public class SpeechFlightInfo implements Parcelable {
    private Semantic semantic;

    private String rc;//0 操作成功  1  无效请求    2   服务器内部错误     3   业务操作失败      4    服务不理解或不能处理该文本

    private String opration;

    private String service;

    private String text;

    public String getOpration() {
        return opration;
    }

    public void setOpration(String opration) {
        this.opration = opration;
    }

    public String getRc() {
        return rc;
    }

    public void setRc(String rc) {
        this.rc = rc;
    }

    public Semantic getSemantic() {
        return semantic;
    }

    public void setSemantic(Semantic semantic) {
        this.semantic = semantic;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.semantic, flags);
        dest.writeString(this.rc);
        dest.writeString(this.opration);
        dest.writeString(this.service);
        dest.writeString(this.text);
    }

    public SpeechFlightInfo() {
    }

    protected SpeechFlightInfo(Parcel in) {
        this.semantic = in.readParcelable(Semantic.class.getClassLoader());
        this.rc = in.readString();
        this.opration = in.readString();
        this.service = in.readString();
        this.text = in.readString();
    }

    public static final Parcelable.Creator<SpeechFlightInfo> CREATOR = new Parcelable.Creator<SpeechFlightInfo>() {
        @Override
        public SpeechFlightInfo createFromParcel(Parcel source) {
            return new SpeechFlightInfo(source);
        }

        @Override
        public SpeechFlightInfo[] newArray(int size) {
            return new SpeechFlightInfo[size];
        }
    };
}
