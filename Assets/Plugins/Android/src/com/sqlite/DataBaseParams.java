package com.sqlite;

import android.content.ContentValues;

public class DataBaseParams
{
    public String separator = ";";

    //Universal
    public String tableName;
    public String columns;

    //Select
    public boolean distinct;
    public String selection;
    public String selectArgs;
    public String groupBy;
    public String having;
    public String orderBuy;
    public String limit;

    //Insert
    public boolean allowNull;
    public String dataValues;

    //Update
    public String whereClause;
    public String whereArgs;

    public String[] GetColumns() { return (columns == null) ? null : columns.split(separator); }

    public String[] GetSelectArgs() { return (selectArgs == null) ? null : selectArgs.split(separator); }

    public String[] GetData() { return (dataValues == null) ? null : dataValues.split(separator); }

    public String[] GetWereArgs() { return (whereArgs == null) ? null : whereArgs.split(separator); }

    public ContentValues GetDataValues()
    {
        String[] columnNames = GetColumns();
        String[] data = GetData();
        if (data == null || columnNames == null || columnNames.length != data.length)
            return null;
        ContentValues contentValues = new ContentValues(columnNames.length);

        for (int i = 0; i < columnNames.length; i++)
            contentValues.put(columnNames[i], data[i]);

        return contentValues;
    }

    public void ClearData()
    {
        distinct = false;
        tableName = null;
        columns = null;
        selection = null;
        selectArgs = null;
        groupBy = null;
        having = null;
        orderBuy = null;
        limit = null;
        allowNull = false;
        dataValues = null;
        whereClause = null;
        whereClause = null;
    }

    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        if (distinct)
            sb.append("distinct: true\n");
        if (tableName != null)
            sb.append("table name: ").append(tableName).append('\n');
        if (columns != null)
        {
            sb.append("columns: ").append(columns).append('\n');
            for (String str : GetColumns())
                sb.append(str).append('\n');
        }
        if (selection != null)
            sb.append("selection: ").append(selection).append('\n');
        if (selectArgs != null)
        {
            sb.append("selectArgs: ").append(selectArgs).append('\n');
            for (String str : GetSelectArgs())
                sb.append(str).append('\n');
        }
        if (groupBy != null)
            sb.append("groupBy: ").append(groupBy).append('\n');
        if (having != null)
            sb.append("having: ").append(having).append('\n');
        if (orderBuy != null)
            sb.append("orderBuy: ").append(orderBuy).append('\n');
        if (limit != null)
            sb.append("limit: ").append(limit).append('\n');
        if (allowNull)
            sb.append("allow null: true\n");
        if (dataValues != null)
        {
            sb.append("dataValues: ").append(dataValues).append('\n');
            for (String str : GetData())
                sb.append(str).append('\n');
        }
        if (whereClause != null)
            sb.append("whereClause: ").append(whereClause).append('\n');
        if (whereArgs != null)
        {
            sb.append("whereArgs: ").append(whereArgs).append('\n');
            for (String str : GetWereArgs())
                sb.append(str).append('\n');
        }
        return sb.toString();
    }
}
