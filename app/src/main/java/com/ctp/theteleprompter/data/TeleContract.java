package com.ctp.theteleprompter.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class TeleContract {

    public static final String AUTHORITY = "com.ctp.theteleprompter.data";

   public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_FAVORITES = "docs";



    public static final class TeleEntry implements BaseColumns{

        public static final Uri TELE_CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITES).build();

        public static Uri getUriForMovieId(int id){
            return TELE_CONTENT_URI.buildUpon().appendPath(Integer.toString(id)).build();
        }



        public static final String TABLE_NAME="docs";

        public static final String COLUMN_CLOUD_ID = "cloud_id";

        public static final String COLUMN_TITLE = "doc_title";


        public static final String COLUMN_TEXT = "doc_text";

        public static final String COLUMN_USER_NAME = "user_name";

        public static final String COLUMN_PRIORITY = "priority";


    }

}
