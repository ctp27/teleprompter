package com.ctp.theteleprompter;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.content.res.AppCompatResources;
import android.widget.RemoteViews;

import com.ctp.theteleprompter.data.SharedPreferenceUtils;
import com.ctp.theteleprompter.model.Doc;
import com.ctp.theteleprompter.model.TeleSpec;
import com.ctp.theteleprompter.services.TeleWidgetService;

/**
 * Implementation of App Widget functionality.
 */
public class TeleAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, Doc doc, boolean isLoggedIn) {
        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = getRemoteViews(context,doc,isLoggedIn);
//        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    public static void updateAllWidgets(Context context, AppWidgetManager appWidgetManager,
                                        int[] appWidgetIds, Doc doc, boolean isLoggedIn){
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId,doc,isLoggedIn);
        }
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        TeleWidgetService.updateTeleWidgets(context);

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }



    private static RemoteViews getRemoteViews(Context context, Doc doc, boolean isLoggedIn){



        PendingIntent newDocPendingIntent =null;
        PendingIntent editDocPendingIntent = null;
        PendingIntent slideShowPendingIntent = null;

        if(isLoggedIn){
            /*  User is logged in   */
            newDocPendingIntent = getNewDocPendingIntent(context);

            if(doc==null){
                    /* User is logged in but hasn't pinned a doc    */
                    /*  Open Main acitivity for edit and slideshow  */
                editDocPendingIntent = getMainActivityPendingIntent(context);
                slideShowPendingIntent = getMainActivityPendingIntent(context);

            }
            else {
                /*  User is logged in and user is has pinned a doc  */
                editDocPendingIntent = getPinnedDocPendingIntent(context,doc);
                slideShowPendingIntent = getPinnedDocSlideShowPendingIntent(context,doc);
            }

        }else {
            /*  User is not logged in */
            newDocPendingIntent = getLoginPendingIntent(context);
            slideShowPendingIntent = getLoginPendingIntent(context);
            editDocPendingIntent = getLoginPendingIntent(context);
        }

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.tele_app_widget);
        setWidgetIcons(views,context);
        views.setOnClickPendingIntent(R.id.widget_new_doc,newDocPendingIntent);
        views.setOnClickPendingIntent(R.id.widget_edit_pin_doc,editDocPendingIntent);
        views.setOnClickPendingIntent(R.id.widget_play_pinned_doc,slideShowPendingIntent);

        return views;
    }


    private static void setWidgetIcons(RemoteViews remoteViews, Context context) {



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            remoteViews.setImageViewResource(R.id.widget_new_doc, R.drawable.ic_add_white_24dp);
            remoteViews.setImageViewResource(R.id.widget_edit_pin_doc,R.drawable.ic_edit_black_24dp);
            remoteViews.setImageViewResource(R.id.widget_play_pinned_doc,R.drawable.ic_play_arrow_white_24dp);

        } else {

            setPreLollipopImageResource(remoteViews,
                    context,R.id.widget_new_doc,R.drawable.ic_add_white_24dp);
            setPreLollipopImageResource(remoteViews,
                    context,R.id.widget_edit_pin_doc,R.drawable.ic_edit_black_24dp);
            setPreLollipopImageResource(remoteViews,
                    context,R.id.widget_play_pinned_doc,R.drawable.ic_play_arrow_white_24dp);

        }

    }

    private static void setPreLollipopImageResource(RemoteViews remoteViews,Context context, int viewId,int drawable) {
        Drawable d = AppCompatResources.getDrawable(context, drawable);
        Bitmap b = Bitmap.createBitmap(d.getIntrinsicWidth(),
                d.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        d.setBounds(0, 0, c.getWidth(), c.getHeight());
        d.draw(c);
        remoteViews.setImageViewBitmap(viewId, b);
    }


    private static PendingIntent getMainActivityPendingIntent(Context context){

        Intent intent = new Intent(context,MainActivity.class);
        intent.putExtra(MainActivity.INTENT_EXTRA_NO_PINNED_DOC,true);

        return PendingIntent
                .getActivity(context,
                        101,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static PendingIntent getNewDocPendingIntent(Context context){


        Doc doc = new Doc();
        doc.setTitle("");
        doc.setText("");
        doc.setNew(true);
        doc.setUserId(SharedPreferenceUtils.getPrefUserId(context));

        Intent intent = new Intent(context,DocEditActivity.class);
        intent.putExtra(DocEditActivity.EXTRA_PARCEL_KEY,doc);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addNextIntentWithParentStack(intent);

        return taskStackBuilder
                .getPendingIntent(210, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    private static PendingIntent getPinnedDocPendingIntent(Context context, Doc doc){

        Intent intent = new Intent(context, DocEditActivity.class);
        intent.putExtra(DocEditActivity.EXTRA_PARCEL_KEY, doc);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addNextIntentWithParentStack(intent);

        return taskStackBuilder
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static PendingIntent getLoginPendingIntent(Context context){

        Intent intent = new Intent(context,LandingPageActivity.class);

        return PendingIntent
                .getActivity(context,
                        201,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

    }


    private static PendingIntent getPinnedDocSlideShowPendingIntent(Context context, Doc doc){

        Intent intentA = new Intent(context,MainActivity.class);

        Intent intentB = new Intent(context,DocEditActivity.class);
        intentB.putExtra(DocEditActivity.EXTRA_PARCEL_KEY,doc);


        TeleSpec teleSpec = new TeleSpec();
        teleSpec.setBackgroundColor(SharedPreferenceUtils.getDefaultBackgroundColor(context));
        teleSpec.setFontColor(SharedPreferenceUtils.getDefaultTextColor(context));
        teleSpec.setTitle(doc.getTitle());
        teleSpec.setContent(doc.getText());
        teleSpec.setFontSize(SharedPreferenceUtils.getDefaultFontSize(context));
        teleSpec.setScrollSpeed(SharedPreferenceUtils.getDefaultScrollSpeed(context));

        Intent intentC = new Intent(context,SlideShowActivity.class);
        intentC.putExtra(SlideShowActivity.INTENT_PARCELABLE_EXTRA_KEY,teleSpec);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);

        taskStackBuilder.addNextIntent(intentA);
        taskStackBuilder.addNextIntent(intentB);
        taskStackBuilder.addNextIntent(intentC);

        return taskStackBuilder.getPendingIntent(3,PendingIntent.FLAG_UPDATE_CURRENT);
    }



}

