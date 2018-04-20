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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DocService extends IntentService {


    public static final String ACTION_UPDATE = "action-update-doc";
    public static final String ACTION_DELETE = "action-delete-doc";
    public static final String ACTION_SYNC_STARTED = "com.ctp.theteleprompter.sync.started";
    public static final String ACTION_SYNC_END = "com.ctp.theteleprompter.sync.ended";


    private static final String ACTION_INSERT = "action-insert-doc";
    private static final String ACTION_CHANGE_POSITION = "action-query-doc";
    private static final String EXTRA_KEY = "key_extra_key";

    private static final String DATABASE_DOC_CHILD_PRIORITY= "priority";

    public static final String TAG = DocService.class.getSimpleName();
    private static final String DATABASE_CHILD_DOCS = "docs";
    private static final String ACTION_SYNC_DOCS = "action_sync_docs";

    public DocService() {
        super(DocService.class.getSimpleName());
    }

    private static FirebaseDatabase mDb;

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


    public static void moveDocs(Context context, Doc doc){
        Intent intent = new Intent(context,DocService.class);
        intent.putExtra(EXTRA_KEY,doc);
        intent.setAction(ACTION_CHANGE_POSITION);
        context.startService(intent);
    }

    public static void syncDocs(Context context, String userId){

        Intent intent = new Intent(context,DocService.class);
        intent.putExtra(EXTRA_KEY,userId);
        intent.setAction(ACTION_SYNC_DOCS);
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
            case ACTION_CHANGE_POSITION:
                handleActionChangePosition(intent);
                break;
            case ACTION_SYNC_DOCS:
                handleActionSyncDocs(intent);
        }

    }

    private void handleActionSyncDocs(Intent intent) {

        sendSyncBroadcast(true);

        final String userId = intent.getStringExtra(EXTRA_KEY);

        getFirebaseDatabaseReference().child(DATABASE_CHILD_DOCS)
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<ContentValues> contentValuesList = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){


                    Doc doc = snapshot.getValue(Doc.class);
                    doc.setUserId(userId);

                    contentValuesList.add(doc.getContentValues());
                }


                ContentValues[] contentValues = contentValuesList.toArray(new ContentValues[1]);

                getContentResolver()
                        .delete(TeleContract.TeleEntry.TELE_CONTENT_URI,null,null);

                getContentResolver()
                        .bulkInsert(TeleContract.TeleEntry.TELE_CONTENT_URI,contentValues);

                sendSyncBroadcast(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                sendSyncBroadcast(false);


            }
        });


    }

    private void sendSyncBroadcast(boolean isSyncOn){
        Intent intent = new Intent();
        if (isSyncOn) {
            intent.setAction(ACTION_SYNC_STARTED);
        }else {
            intent.setAction(ACTION_SYNC_END);
        }
        sendBroadcast(intent);
    }


    private void handleActionDeleteDoc(Intent intent) {

        Doc doc = intent.getParcelableExtra(EXTRA_KEY);

        Uri uri = doc.getUri();

        getContentResolver().delete(uri,null,null);

        doc.setUserId(SharedPreferenceUtils.getPrefUserId(this));


        /*  Delete from Firebase Database   */
        getFirebaseDatabaseReference()
                .child(DATABASE_CHILD_DOCS)
                .child(doc.getUserId())
                .child(doc.getCloudId())
                .removeValue();


    }



    private void handleActionUpdateDoc(Intent intent) {

        Doc doc = intent.getParcelableExtra(EXTRA_KEY);



        ContentValues contentValues = doc.getContentValues();

        getContentResolver().delete(TeleContract.TeleEntry.TELE_CONTENT_URI,
                TeleContract.TeleEntry._ID+"=?",
                new String[]{Integer.toString(doc.getId())});

        Uri uri = getContentResolver().insert(TeleContract.TeleEntry.TELE_CONTENT_URI,contentValues);

        String newId = uri.getLastPathSegment();
        doc.setPriority(Integer.parseInt(newId));

        getFirebaseDatabaseReference()
                .child(DATABASE_CHILD_DOCS)
                .child(doc.getUserId())
                .child(doc.getCloudId())
                .setValue(doc);


//        COMPLETE Update on FirebaseDatabase

    }


    /**
     * Inserts the new Doc into the local database and the Firebase Realtime Database
     * @param intent The intent passed while calling the service
     */

    private void handleActionInsertDoc(Intent intent) {

        /*  Retrieve the doc object from Intent Parcel */
        Doc doc = intent.getParcelableExtra(EXTRA_KEY);

        String cloudId = getFirebaseDatabaseReference()
                .child(DATABASE_CHILD_DOCS)
                .child(doc.getUserId())
                .push()
                .getKey();

        doc.setCloudId(cloudId);

        /*  Get ContentValues for this doc  */
        ContentValues contentValues = doc.getContentValues();



        /*  Insert the doc in Local Database    */
        Uri uri = getContentResolver().insert(TeleContract.TeleEntry.TELE_CONTENT_URI,contentValues);

        /*  If uri is null, error occured, return*/
        if(uri==null) {
            return;
        }


        /* Obtain the local database Id of the doc from the returned Uri */
        String idString = uri.getLastPathSegment();
        int id = Integer.parseInt(idString);

        /*  Update the last stored Id, for updates on orientation change    */
        SharedPreferenceUtils.setLastStoredId(this,id);
        SharedPreferenceUtils.setLastStoredCloudId(this,cloudId);

        /*  Prepare doc object for cloud database  */
        /*  Set the same id as the local database   */
        /*  Set the user ID of this doc (User Id is primary key for the user online) */
        /*  Set the priority to be same as the id for a new doc */
//        doc.setUserId(SharedPreferenceUtils.getPrefUserId(this));
        doc.setPriority(id);


        /*  Insert the doc into the firebase database   */

        getFirebaseDatabaseReference()
                .child(DATABASE_CHILD_DOCS)
                .child(doc.getUserId())
                .child(cloudId)
                .setValue(doc);
    }


    private void handleActionChangePosition(Intent intent){

        Doc doc = intent.getParcelableExtra(EXTRA_KEY);

        ContentValues contentValues = doc.getContentValues();


        getContentResolver().update(TeleContract.TeleEntry.TELE_CONTENT_URI,contentValues,
                TeleContract.TeleEntry._ID+"=?",new String[]{Integer.toString(doc.getId())});


        getFirebaseDatabaseReference()
                .child(DATABASE_CHILD_DOCS)
                .child(doc.getUserId())
                .child(doc.getCloudId())
                .child(DATABASE_DOC_CHILD_PRIORITY)
                .setValue(doc.getPriority());
    }


    private DatabaseReference getFirebaseDatabaseReference(){
        if(mDb==null) {
            mDb = FirebaseDatabase.getInstance();
            mDb.setPersistenceEnabled(true);

            mDb.getReference("docs").keepSynced(true);
        }
        return mDb.getReference();
    }

}
