package com.phool.svd;

import android.provider.BaseColumns;

/**
 * Created by azhar on 2/16/2018.
 */

public class BookmarkContract {



    public static final class BookmarkEntrylist implements BaseColumns {

        public static final String TABLE_NAME = "bookmarklist";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_TIMESTAMP = "timeStamp";

    }
}
