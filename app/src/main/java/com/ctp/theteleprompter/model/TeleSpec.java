package com.ctp.theteleprompter.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TeleSpec implements Parcelable{


    private int scrollSpeed;
    private int fontSize;
    private String title;
    private String content;
    private int backgroundColor;
    private int fontColor;

    public TeleSpec(){}


    public int getScrollSpeed() {
        return scrollSpeed;
    }

    public void setScrollSpeed(int scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getFontColor() {
        return fontColor;
    }

    public void setFontColor(int fontColor) {
        this.fontColor = fontColor;
    }

    public static final Creator<TeleSpec> CREATOR = new Creator<TeleSpec>(){

        @Override
        public TeleSpec createFromParcel(Parcel parcel) {
            return new TeleSpec(parcel);
        }

        @Override
        public TeleSpec[] newArray(int size) {
            return new TeleSpec[size];
        }
    };

    private TeleSpec(Parcel in){

        scrollSpeed = in.readInt();
        fontSize = in.readInt();
        title = in.readString();
        content = in.readString();
        backgroundColor = in.readInt();
        fontColor = in.readInt();

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeInt(scrollSpeed);
        parcel.writeInt(fontSize);
        parcel.writeString(title);
        parcel.writeString(content);
        parcel.writeInt(backgroundColor);
        parcel.writeInt(fontColor);

    }
}
