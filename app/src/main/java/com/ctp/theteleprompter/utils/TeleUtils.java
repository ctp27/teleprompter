package com.ctp.theteleprompter.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class TeleUtils{

    public static boolean isConnectedToNetwork(Context context){

        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    public static boolean isValidEmail(String email){

        return true;
    }


    public static boolean isValidPassword(String password){
        return true;
    }




}
