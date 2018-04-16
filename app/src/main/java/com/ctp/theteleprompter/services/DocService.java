package com.ctp.theteleprompter.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.ctp.theteleprompter.data.SharedPreferenceUtils;
import com.ctp.theteleprompter.data.TeleContract;
import com.ctp.theteleprompter.model.Doc;

public class DocService extends IntentService {


    public static final String ACTION_UPDATE = "action-update-doc";
    public static final String ACTION_DELETE = "action-delete-doc";
    public static final String PERSIST_ACTION_BROADCAST = "com.ctp.theteleprompter.CARD_SERVICE";
    public static final String BROADCAST_ACTION_EXTRA = "broadcast-action-extra";

    private static final String ACTION_INSERT = "action-insert-doc";
    private static final String ACTION_QUERY = "action-query-doc";
    private static final String EXTRA_KEY = "key_extra_key";
    public static final String EXTRA_OBJECT_ ="old -position";


    public DocService() {
        super(DocService.class.getSimpleName());
    }


    public static void insertDoc(Context context, Doc doc){
        Intent intent = new Intent(context,DocService.class);
        intent.putExtra(EXTRA_KEY,doc);
        intent.setAction(ACTION_INSERT);
        context.startService(intent);
    }

    public static void updateDoc(Context context, Doc doc){
        Intent intent = new Intent(context,DocService.class);
        intent.putExtra(EXTRA_KEY,doc);
        intent.setAction(ACTION_UPDATE);

        context.startService(intent);
    }

    public static void deleteDoc(Context context, Doc doc){
        Intent intent = new Intent(context,DocService.class);
        intent.putExtra(EXTRA_KEY,doc);
        intent.setAction(ACTION_DELETE);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        String action = intent.getAction();

        switch (action){
            case ACTION_INSERT:
                handleActionInsertDoc(intent);
                break;
            case ACTION_UPDATE:
                handleActionUpdateDoc(intent);
                break;
            case ACTION_DELETE:
                handleActionDeleteDoc(intent);
                break;
        }

    }


    private void handleActionDeleteDoc(Intent intent) {

        Doc doc = intent.getParcelableExtra(EXTRA_KEY);

        Uri uri = doc.getUri();

        getContentResolver().delete(uri,null,null);



//        TODO: Delete from firebase database

    }

    private void handleActionUpdateDoc(Intent intent) {

        Doc doc = intent.getParcelableExtra(EXTRA_KEY);


        ContentValues contentValues = doc.getContentValues();

        getContentResolver().update(TeleContract.TeleEntry.TELE_CONTENT_URI,contentValues,
                TeleContract.TeleEntry._ID+"=?",new String[]{Integer.toString(doc.getId())});


//        TODO: Update on FirebaseDatabase

    }

    private void handleActionInsertDoc(Intent intent) {

        Doc doc = intent.getParcelableExtra(EXTRA_KEY);
        ContentValues contentValues = doc.getContentValues();

        Uri uri = getContentResolver().insert(TeleContract.TeleEntry.TELE_CONTENT_URI,contentValues);

        if(uri==null) {
            return;
        }

        String id = uri.getLastPathSegment();
        SharedPreferenceUtils.setLastStoredId(this,Integer.parseInt(id));

//        TODO: Insert into Firebase Cloud Database
    }
}
