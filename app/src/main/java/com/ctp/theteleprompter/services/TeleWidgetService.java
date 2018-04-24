package com.ctp.theteleprompter.services;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

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
        }
    }

    private void handleActionUpdateWidgets() {

        

    }
}
