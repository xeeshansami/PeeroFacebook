package com.phool.svd;

import android.provider.BaseColumns;

/**
 * Created by azhar on 2/22/2018.
 */

public class HistoryContract {

    public static final class Histroylist implements BaseColumns {

        public static final String TABLE_NAME = "histroylist";
        public static final String COLUMN_TITLE_HISTROY = "title";
         public static final String COLUMN_URL_HISTROY = "url";
        public static final String COLUMN_TIMESTAMP = "timeStamp";

    }
}
