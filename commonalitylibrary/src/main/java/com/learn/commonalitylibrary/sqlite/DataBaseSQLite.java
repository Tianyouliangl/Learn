package com.learn.commonalitylibrary.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataBaseSQLite extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "learn.db";  //数据库名字
    private static final int DATABASE_VERSION = 1;         //数据库版本号


    public DataBaseSQLite(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
