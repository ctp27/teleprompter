package com.ctp.theteleprompter.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TeleUtils{

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean isConnectedToNetwork(Context context){

        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();


    }

    public static boolean isValidEmail(String email){
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(email);
        return matcher.find();
    }


    public static boolean isValidPassword(String password){
        return password.length()>6 && !password.trim().isEmpty();
    }



    public static ActionCodeSettings getActionCodeSettingsForUser(FirebaseUser user){

        String url = "http://www.teleprompter.com/verify?uid=" + user.getUid();

        return ActionCodeSettings.newBuilder()
                .setUrl(url)
                // The default for this is populated with the current android package name.
                .setAndroidPackageName("com.ctp.theteleprompter", false, null)
                .setHandleCodeInApp(false)
                .build();

    }





}
