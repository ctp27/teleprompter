package com.ctp.theteleprompter.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TeleOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "teledocs.db";

    public static final int DATABASE_VERSION = 2;

    public TeleOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String CREATE_TABLE = "CREATE TABLE "  + TeleContract.TeleEntry.TABLE_NAME + " (" +
                TeleContract.TeleEntry._ID                + " INTEGER PRIMARY KEY, " +
                TeleContract.TeleEntry.COLUMN_CLOUD_ID + " TEXT, " +
                TeleContract.TeleEntry.COLUMN_TITLE   + " TEXT, " +
                TeleContract.TeleEntry.COLUMN_TEXT +" TEXT, "+
                TeleContract.TeleEntry.COLUMN_PRIORITY +" INTEGER, "+
                TeleContract.TeleEntry.COLUMN_USER_NAME + " TEXT NOT NULL "+
                ");";

        sqLiteDatabase.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TeleContract.TeleEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
