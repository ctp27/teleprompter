package com.ctp.theteleprompter.model;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.ctp.theteleprompter.data.TeleContract;
import com.google.firebase.database.Exclude;


public class Doc implements Parcelable {

    private int id=-1;
    private String title;
    private String text;
    private int priority=-1;

    @Exclude
    private String userId;

    @Exclude
    private boolean isNew;

    @Exclude
    private int isTutorial;

    private String cloudId;

    private boolean isPing;


    public Doc(){
        isNew = false;
        isTutorial = 0;
        isPing = false;
    }

    public Doc(Cursor data){

        id = data.getInt(data.getColumnIndex(TeleContract.TeleEntry._ID));
        cloudId = data.getString(data.getColumnIndex(TeleContract.TeleEntry.COLUMN_CLOUD_ID));
        title = data.getString(data.getColumnIndex(TeleContract.TeleEntry.COLUMN_TITLE));
        text = data.getString(data.getColumnIndex(TeleContract.TeleEntry.COLUMN_TEXT));
        priority = data.getInt(data.getColumnIndex(TeleContract.TeleEntry.COLUMN_PRIORITY));
        userId = data.getString(data.getColumnIndex(TeleContract.TeleEntry.COLUMN_USER_NAME));
        isTutorial = data.getInt(data.getColumnIndex(TeleContract.TeleEntry.COLUMN_IS_TUTORIAL));
        isNew = false;
        isPing = false;
    }

    @Exclude
    public int getId() {
        return id;
    }

    public String getCloudId() {
        return cloudId;
    }

    public void setCloudId(String cloudId) {
        this.cloudId = cloudId;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    @Exclude
    public String getUserId() {
        return userId;
    }

    public int getPriority() {
        return priority;
    }

    public void setId(int id) {
        this.id = id;
    }


    public boolean isPing() {
        return isPing;
    }

    public void setPing(boolean ping) {
        isPing = ping;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getIsTutorial() {
        return isTutorial;
    }

    public void setIsTutorial(int isTutorial) {
        this.isTutorial = isTutorial;
    }

    @Exclude
    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    @Exclude
    public boolean isTutorial() {
        if(isTutorial==1){
            return true;
        }else {
            return false;
        }
    }

    public void setTutorial(boolean tutorial) {

        if(tutorial){
            isTutorial = 1;
        }
        else {
            isTutorial = 0;
        }

    }

    @Exclude
    public Uri getUri() {
        return ContentUris.withAppendedId(TeleContract.TeleEntry.TELE_CONTENT_URI, id);
    }

    @Exclude
    public ContentValues getContentValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(TeleContract.TeleEntry.COLUMN_TEXT,text);
        contentValues.put(TeleContract.TeleEntry.COLUMN_TITLE,title);
        contentValues.put(TeleContract.TeleEntry.COLUMN_IS_TUTORIAL,isTutorial);

        if(userId !=null) {
            contentValues.put(TeleContract.TeleEntry.COLUMN_USER_NAME, userId);
        }

        if(cloudId!=null){
            contentValues.put(TeleContract.TeleEntry.COLUMN_CLOUD_ID,cloudId);
        }
        if(priority!=-1){
            contentValues.put(TeleContract.TeleEntry.COLUMN_PRIORITY,priority);
        }


        return contentValues;
    }





    protected Doc(Parcel in) {
        id = in.readInt();
        cloudId = in.readString();
        title = in.readString();
        text = in.readString();
        priority = in.readInt();
        userId = in.readString();
        isNew = in.readByte()!=0;
        isTutorial = in.readInt();
        isPing = in.readByte()!=0;

    }

    @Exclude
    public static final Creator<Doc> CREATOR = new Creator<Doc>() {
        @Override
        public Doc createFromParcel(Parcel parcel) {
            return new Doc(parcel);
        }

        @Override
        public Doc[] newArray(int size) {
            return new Doc[size];
        }
    };

    @Exclude
    @Override
    public int describeContents() {
        return 0;
    }

    @Exclude
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(cloudId);
        parcel.writeString(title);
        parcel.writeString(text);
        parcel.writeInt(priority);
        parcel.writeString(userId);
        parcel.writeByte((byte) (isNew ? 1 : 0));
        parcel.writeInt(isTutorial);
        parcel.writeByte((byte) (isPing ? 1: 0));
    }
}
