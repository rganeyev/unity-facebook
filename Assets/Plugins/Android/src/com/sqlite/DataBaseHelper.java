package com.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper
{
    public DataBaseHelper(Context context, String name, int version)
    { super(context, name, null, version); }

    @Override
    public void onCreate(SQLiteDatabase db)
    { /**db.execSQL("CREATE TABLE IF NOT EXISTS todo (_id integer primary key autoincrement, "
			+ "category text not null, summary text not null, description text not null);"); Log.i("DBH", db.getPath()); */}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
