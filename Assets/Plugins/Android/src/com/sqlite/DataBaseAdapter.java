package com.sqlite;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Debug;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DataBaseAdapter
{
    public static String NULL = "NULL";
    private Activity activity;
    private DataBaseHelper dataBaseHelper;

    public DataBaseAdapter(Activity app)
    { activity = app; }

    public void  Open(String name)
    { dataBaseHelper = new DataBaseHelper(activity.getApplicationContext(), name, 1); }

    public void ExecuteSQLReadable(String arg)
    { if (dataBaseHelper != null) dataBaseHelper.getReadableDatabase().execSQL(arg); }

    public void ExecuteSQLWritable(String arg)
    { if (dataBaseHelper != null) dataBaseHelper.getWritableDatabase().execSQL(arg); }

    public String Select(DataBaseParams dbParams)
    {
        JSONObject json = new JSONObject();
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(dbParams.distinct, dbParams.tableName,
                dbParams.GetColumns(), dbParams.selection, dbParams.GetSelectArgs(),
                dbParams.groupBy, dbParams.having, dbParams.orderBuy, dbParams.limit);

        if (cursor.moveToFirst())
        {
            JSONArray[] jsonArrays = new JSONArray[cursor.getColumnCount()];
            for (int i = 0; i < jsonArrays.length; i++)
                jsonArrays[i] = new JSONArray();
            String[] column = cursor.getColumnNames();
            do
                for (int i = 0; i < cursor.getColumnCount(); i++)
                    jsonArrays[i].put(cursor.getString(i));
            while (cursor.moveToNext());
            try
            {
                for (int i = 0; i < jsonArrays.length; i++)
                    json.put(column[i], jsonArrays[i]);
            }
            catch (JSONException e) {json = new JSONObject(); }
        }

         if (!(cursor == null || cursor.isClosed()))
            cursor.close();

        return json.toString();
    }

    public long Insert(DataBaseParams dbParams)
    {
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        return sqLiteDatabase.insert(dbParams.tableName, dbParams.allowNull ? NULL : null,
                dbParams.GetDataValues());
    }

    public int Update(DataBaseParams dbParams)
    {
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        return sqLiteDatabase.update(dbParams.tableName, dbParams.GetDataValues(),
                dbParams.whereClause, dbParams.GetWereArgs());
    }

    public long Replace(DataBaseParams dbParams)
    {
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        return sqLiteDatabase.replace(dbParams.tableName, dbParams.allowNull ? NULL : null,
                dbParams.GetDataValues());
    }

    public void Close()
    { dataBaseHelper.close(); }
}
