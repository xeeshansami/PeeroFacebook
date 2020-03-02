package com.phool.svd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by azhar on 2/16/2018.
 */

public class BookmarkDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bookmarklist.db";
    private static final int DATABASE_VERSION = 3;
    public BookmarkDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_WAITLIST_TABLE = "CREATE TABLE  " +
                BookmarkContract.BookmarkEntrylist.TABLE_NAME + " (" +
                BookmarkContract.BookmarkEntrylist._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                BookmarkContract.BookmarkEntrylist.COLUMN_TITLE + " TEXT NOT NULL," +
                BookmarkContract.BookmarkEntrylist.COLUMN_URL + " TEXT NOT NULL," +
                BookmarkContract.BookmarkEntrylist.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" + ");";
        final String SQL_CREATE_HISTROY_TABLE = "CREATE TABLE  " +
                HistoryContract.Histroylist.TABLE_NAME + " (" +
                HistoryContract.Histroylist._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                HistoryContract.Histroylist.COLUMN_TITLE_HISTROY + " TEXT NOT NULL," +
                HistoryContract.Histroylist.COLUMN_URL_HISTROY + " TEXT NOT NULL," +
                HistoryContract.Histroylist.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" + ");";
        sqLiteDatabase.execSQL(SQL_CREATE_WAITLIST_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_HISTROY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BookmarkContract.BookmarkEntrylist.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + HistoryContract.Histroylist.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }}
