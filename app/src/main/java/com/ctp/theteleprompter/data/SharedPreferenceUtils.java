package com.ctp.theteleprompter.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

import com.ctp.theteleprompter.R;

public class SharedPreferenceUtils {

    public static int getDefaultBackgroundColor(Context context){
        String key = context.getString(R.string.pref_background_color_key);
        int defaultValue = Color.BLACK;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(key,defaultValue);
    }

    public static int getDefaultTextColor(Context context){
        String key = context.getString(R.string.pref_text_color_key);
        int defaultValue = Color.WHITE;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(key,defaultValue);
    }

    public static void setDefaultBackgroundColor(Context context, int color){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(context.getString(R.string.pref_background_color_key), color);
        editor.apply();
    }

    public static void setDefaultTextColor(Context context, int color){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(context.getString(R.string.pref_text_color_key), color);
        editor.apply();
    }


    public static void setPrefUsername(Context context, String username){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(context.getString(R.string.pref_user_name_key), username);
        editor.apply();
    }


    public static String getPrefUsername(Context context){
        String defaultValue = "Local";
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(context.getString(R.string.pref_user_name_key),defaultValue);
    }

    public static void setPrefEmail(Context context, String email){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(context.getString(R.string.pref_email_key), email);
        editor.apply();
    }


    public static String getPrefEmail(Context context){
        String defaultValue = "Local User";
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(context.getString(R.string.pref_email_key),defaultValue);
    }

    public static void setPrefUserId(Context context, String uid){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(context.getString(R.string.pref_uid_key), uid);
        editor.apply();
    }

    public static String getPrefUserId(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(context.getString(R.string.pref_uid_key),"-1");
    }

    public static int getLastStoredId(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(context.getString(R.string.pref_last_id_key),-1);

    }

    public static void setLastStoredId(Context context, int id){
        String key = context.getString(R.string.pref_last_id_key);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, id);
        editor.apply();
    }

    public static int getDefaultFontSize(Context context){
        String defaultValue = context.getString(R.string.pref_fontsize_medium_value);
        String fontSizeKey = context.getString(R.string.pref_fontsize_key);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String fontSize = sp.getString(fontSizeKey,defaultValue);
        return Integer.parseInt(fontSize);
    }

    public static int getDefaultScrollSpeed(Context context){
        String defaultValue = context.getString(R.string.pref_speed_value_2);
        String speedKey = context.getString(R.string.pref_speed_key);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String fontSize = sp.getString(speedKey,defaultValue);
        return Integer.parseInt(fontSize);
    }

    public static boolean isTextMirrored(Context context){
        boolean defaultBoolean = context.getResources().getBoolean(R.bool.pref_mirror_text_default);
        String mirrorKey = context.getString(R.string.pref_mirror_text_key);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(mirrorKey,defaultBoolean);
    }

}
