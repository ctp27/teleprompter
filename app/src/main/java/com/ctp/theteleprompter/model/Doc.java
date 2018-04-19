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
    private int cloudId;



    public Doc(){
        isNew = false;
    }

    public Doc(Cursor data){

        id = data.getInt(data.getColumnIndex(TeleContract.TeleEntry._ID));
        cloudId = data.getInt(data.getColumnIndex(TeleContract.TeleEntry.COLUMN_CLOUD_ID));
        title = data.getString(data.getColumnIndex(TeleContract.TeleEntry.COLUMN_TITLE));
        text = data.getString(data.getColumnIndex(TeleContract.TeleEntry.COLUMN_TEXT));
        priority = data.getInt(data.getColumnIndex(TeleContract.TeleEntry.COLUMN_PRIORITY));
        userId = data.getString(data.getColumnIndex(TeleContract.TeleEntry.COLUMN_USER_NAME));
        isNew = false;
    }


    public int getId() {
        return id;
    }

    @Exclude
    public int getCloudId() {
        return cloudId;
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

    public void setCloudId(int cloudId) {
        this.cloudId = cloudId;
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

    @Exclude
    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
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

        if(userId !=null) {
            contentValues.put(TeleContract.TeleEntry.COLUMN_USER_NAME, "LOCAL");
        }

        if(cloudId!=-1){
            contentValues.put(TeleContract.TeleEntry.COLUMN_CLOUD_ID,cloudId);
        }
        if(priority!=-1){
            contentValues.put(TeleContract.TeleEntry.COLUMN_PRIORITY,priority);
        }
        return contentValues;
    }



    protected Doc(Parcel in) {
        id = in.readInt();
        cloudId = in.readInt();
        title = in.readString();
        text = in.readString();
        priority = in.readInt();
        userId = in.readString();
        isNew = in.readByte()!=0;

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
        parcel.writeInt(cloudId);
        parcel.writeString(title);
        parcel.writeString(text);
        parcel.writeInt(priority);
        parcel.writeString(userId);
        parcel.writeByte((byte) (isNew ? 1 : 0));
    }
}
