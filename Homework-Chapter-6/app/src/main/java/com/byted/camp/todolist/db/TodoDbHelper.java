package com.byted.camp.todolist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TodoDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "todo.db";
    private static final int DB_VERSION = 2;

    public TodoDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO: 2021/7/19 3. 这里执行创建数据库操作
        Log.i("DEBUG", "onCreate:2 ");
        db.execSQL(TodoContract.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: 2021/7/19 4. 这里执行升级数据库操作
        for(int i=oldVersion;i<newVersion;i++)
        {
            switch (i){
                case 1: db.execSQL(TodoContract.SQL_UPGRADE_ENTRIES);
                break;
            }
        }
    }
}
