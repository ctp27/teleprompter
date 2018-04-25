package com.ctp.theteleprompter.services;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

import com.ctp.theteleprompter.TeleAppWidget;
import com.ctp.theteleprompter.data.SharedPreferenceUtils;
import com.ctp.theteleprompter.data.TeleContract;
import com.ctp.theteleprompter.model.Doc;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TeleWidgetService extends JobIntentService {

    private static final String ACTION_HANDLE_UPDATE_WIDGET= "update-widgets";

    public static void updateTeleWidgets(Context context){
        Intent intent = new Intent(context,TeleWidgetService.class);
        intent.setAction(ACTION_HANDLE_UPDATE_WIDGET);
        enqueueWork(context,TeleWidgetService.class,0,intent);
    }


    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        switch (intent.getAction()){
            case ACTION_HANDLE_UPDATE_WIDGET:
                handleActionUpdateWidgets();
                break;
        }
    }

    private void handleActionUpdateWidgets() {

        FirebaseUser user  = FirebaseAuth.getInstance().getCurrentUser();

        Doc doc = null;
        boolean isLoggedIn=false;

        if(user==null){
            /*  User has not logged in. So no pinned doc exists */
            doc = null;
            isLoggedIn = false;
        }
        else {
            isLoggedIn = true;

            int id = SharedPreferenceUtils.getPinnedId(this);

            if(id==-1) {
                /*  Not pinned any widget */
                doc = null;

            }else {
                /*  Pinned widget exists */
                /*  Prepare Uri for this Id */
                Uri uri = TeleContract.TeleEntry.getUriForMovieId(id);

                /*  Query the content provider  */
                Cursor cursor =
                        getContentResolver()
                                .query(uri,null,null,null,null);

                if(cursor!=null) {

                    /*  Move to first */
                    cursor.moveToFirst();

                    /*  Populate Doc POJO */
                    doc = new Doc(cursor);

                    /*  Close the cursor   */
                    cursor.close();
                }


            }

        }

        /*  Get the AppWidgetManager   */
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        /*  Get all the widget IDs  */
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, TeleAppWidget.class));

        /*  Update all the widgets  providing the extracted information */
        TeleAppWidget.updateAllWidgets(this,appWidgetManager,appWidgetIds,doc,isLoggedIn);

    }
}
