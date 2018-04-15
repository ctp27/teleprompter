package com.ctp.theteleprompter.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class TeleContentProvider extends ContentProvider {

    public static final int DOC = 900;
    public static final int DOC_WITH_ID = 902;

    private TeleOpenHelper theHelper;

    private static UriMatcher theMatcher = buildUriMatcher();



    @Override
    public boolean onCreate() {
        Context context = getContext();
        theHelper = new TeleOpenHelper(context);
        return true;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;

        switch (theMatcher.match(uri)) {

            case DOC_WITH_ID: {

                String docId = uri.getLastPathSegment();

                String[] selectionArguments = new String[]{docId};

                cursor = theHelper.getReadableDatabase().query(

                        TeleContract.TeleEntry.TABLE_NAME,
                        projection,
                        TeleContract.TeleEntry._ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);

                break;
            }

            case DOC: {
                cursor = theHelper.getReadableDatabase().query(
                        TeleContract.TeleEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        final SQLiteDatabase db = theHelper.getWritableDatabase();

        int match = theMatcher.match(uri);

        Uri returnUri;

        switch (match) {
            case DOC:

                long id = db.insert(TeleContract.TeleEntry.TABLE_NAME, null, contentValues);


                if ( id > 0 ) {

                    ContentValues cv = new ContentValues();
                    cv.put(TeleContract.TeleEntry.COLUMN_PRIORITY,id);

                    String selection = TeleContract.TeleEntry._ID + " =?";
                    String[] selectionArgs = new String[]{Long.toString(id)};

                    db.update(TeleContract.TeleEntry.TABLE_NAME,cv,selection,selectionArgs);

                    returnUri = ContentUris.withAppendedId(TeleContract.TeleEntry.TELE_CONTENT_URI, id);


                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);


        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int numRowsDeleted = 0;

        if (null == selection) selection = "1";

        switch (theMatcher.match(uri)) {

            case DOC:
                numRowsDeleted = theHelper.getWritableDatabase().delete(
                        TeleContract.TeleEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

                break;

            case DOC_WITH_ID:

                String id = uri.getLastPathSegment();

                String newSelection = TeleContract.TeleEntry._ID + " =?";

                String[] newSelectionArgs = {id};


                numRowsDeleted = theHelper.getWritableDatabase().delete(
                        TeleContract.TeleEntry.TABLE_NAME,
                        newSelection,
                        newSelectionArgs);

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        /* If we actually deleted any rows, notify that a change has occurred to this URI */
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = theHelper.getWritableDatabase();

        int count = 0;

        switch (theMatcher.match(uri)){

            case DOC:
                count = db.update(TeleContract.TeleEntry.TABLE_NAME,contentValues,selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(count!=0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return count;
    }



    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {

        final SQLiteDatabase db = theHelper.getWritableDatabase();

        switch (theMatcher.match(uri)) {

            case DOC:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(TeleContract.TeleEntry.TABLE_NAME, null, value);
                        if (_id != -1) {

                            ContentValues cv = new ContentValues();
                            cv.put(TeleContract.TeleEntry.COLUMN_PRIORITY,_id);

                            String selection = TeleContract.TeleEntry._ID + " =?";
                            String[] selectionArgs = new String[]{Long.toString(_id)};

                            long _id2 = db.update(TeleContract.TeleEntry.TABLE_NAME,cv,selection,selectionArgs);

                            if(_id2!=-1) {
                                rowsInserted++;
                            }
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    private static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(TeleContract.AUTHORITY, TeleContract.PATH_FAVORITES, DOC);
        uriMatcher.addURI(TeleContract.AUTHORITY, TeleContract.PATH_FAVORITES + "/#", DOC_WITH_ID);

        return uriMatcher;
    }
}
