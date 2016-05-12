package com.brioal.linecode.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Brioal on 2016/3/24.
 */
public class CodeDataBaseHelper extends SQLiteOpenHelper {
    public static String CREATE_CODE_TABLE = "create table CodeItems(mId integer primary key autoincrement , mTitle , mDesc ,mCode , mAuthor , mTag , mTime , integer mRead)";
    public static String CREATE_TAG_TABLE = "create table PagerTags(mId integer primary key autoincrement , mTag)";

    public CodeDataBaseHelper(Context context, String name) {
        this(context, name, null, 1);
    }

    public CodeDataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CODE_TABLE);
        db.execSQL(CREATE_TAG_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
