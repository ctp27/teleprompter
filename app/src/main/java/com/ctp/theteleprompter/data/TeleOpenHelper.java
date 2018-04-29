package com.ctp.theteleprompter.data;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TeleOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "teledocs.db";

    public static final int DATABASE_VERSION = 5;
    private static final String TAG = TeleOpenHelper.class.getSimpleName();

    private Resources mResources;
    private Context context;

    public TeleOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mResources = context.getResources();
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String CREATE_TABLE = "CREATE TABLE "  + TeleContract.TeleEntry.TABLE_NAME + " (" +
                TeleContract.TeleEntry._ID                + " INTEGER PRIMARY KEY, " +
                TeleContract.TeleEntry.COLUMN_CLOUD_ID + " TEXT, " +
                TeleContract.TeleEntry.COLUMN_TITLE   + " TEXT, " +
                TeleContract.TeleEntry.COLUMN_TEXT +" TEXT, "+
                TeleContract.TeleEntry.COLUMN_PRIORITY +" INTEGER, "+
                TeleContract.TeleEntry.COLUMN_USER_NAME + " TEXT NOT NULL, "+
                TeleContract.TeleEntry.COLUMN_IS_TUTORIAL + " INTEGER"+
                ");";

        sqLiteDatabase.execSQL(CREATE_TABLE);
//        addTutorialDocs(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TeleContract.TeleEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
        Log.d(TAG,"On upgrade called");

    }


    /**
     * Adds Tutorial docs when the database is created.
     * @param db The SQLiteDatabase instance
     */
//    private void addTutorialDocs(SQLiteDatabase db) {
//
//        try {
////            db.beginTransaction();
////            try {
//                insertTutorialDocs(db);
////                db.setTransactionSuccessful();
////            } finally {
////                db.endTransaction();
////            }
//        } catch (IOException | JSONException e) {
//            Log.e(TAG, "Unable to pre-fill database", e);
//        }
//    }



//    /**
//     * Reads the Json raw file containing the tutorial doc data and adds them into
//     * the database.
//     * @param db The SQLlite database instance
//     * @throws IOException
//     * @throws JSONException
//     */
//    private void insertTutorialDocs(SQLiteDatabase db) throws IOException,JSONException{
//
//        StringBuilder builder = new StringBuilder();
//        InputStream in = mResources.openRawResource(R.raw.tutorial_docs);
//        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//
//        String line;
//        while ((line = reader.readLine()) != null) {
//            builder.append(line);
//        }
//
//        JSONObject root = new JSONObject(builder.toString());
//
//
//        JSONArray docsArray = root.getJSONArray("docs");
//
//        for(int i=0; i<docsArray.length(); i++){
//
//            JSONObject thisObject = docsArray.getJSONObject(i);
//
//            String title = thisObject.getString("title");
//
//            String text = thisObject.getString("text");
//
//            boolean isTutorial = thisObject.getBoolean("isTutorial");
//
//            Log.d("blahwah","Writing "+isTutorial);
//
//            String userId = thisObject.getString("userId");
//
//            Doc doc = new Doc();
//            doc.setTitle(title);
//            doc.setText(text);
//            doc.setTutorial(isTutorial);
//            doc.setUserId(SharedPreferenceUtils.getPrefUserId(context));
//
//            Log.d("blahwah","Written in object "+doc.isTutorial());
//            DocService.insertDoc(context,doc);
////            ContentValues cv = doc.getContentValues();
////
////            long id = db.insert(TeleContract.TeleEntry.TABLE_NAME,null,cv);
////
////            ContentValues cv1 = new ContentValues();
////            cv1.put(TeleContract.TeleEntry.COLUMN_PRIORITY,id);
////
////            String selection = TeleContract.TeleEntry._ID + " =?";
////            String[] selectionArgs = new String[]{Long.toString(id)};
////            db.update(TeleContract.TeleEntry.TABLE_NAME,cv1,selection,selectionArgs);
//        }
//    }
}
