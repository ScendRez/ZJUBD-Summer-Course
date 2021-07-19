package com.byted.camp.todolist.db;

import android.provider.BaseColumns;

import java.sql.Time;
import java.util.Date;

public final class TodoContract {

    // TODO 1. 定义创建数据库以及升级的操作

    private TodoContract() {
    }

    public static final String SQL_CREATE_ENTRIES=("CREATE TABLE "+TodoNote.TABLE_NAME+
            "("+TodoNote._ID+" INTEGER PRIMARY KEY,"+TodoNote.COLUMN_CONTENT+" TEXT,"+TodoNote.COLUMN_DATE+
            " INTEGER,"+TodoNote.COLUMN_ISFINISHED+" INTEGER,"+TodoNote.COLUMN_PRIORITY+" INTEGER)");
    public static final String SQL_UPGRADE_ENTRIES="ALTER TABLE "+TodoNote.TABLE_NAME+" ADD "+TodoNote.COLUMN_PRIORITY+" INTEGER";

    public static class TodoNote implements BaseColumns {
        // TODO 2.此处定义表名以及列明
        public static final String TABLE_NAME="todos";
        public static final String COLUMN_ISFINISHED="fnished";
        public static final String COLUMN_CONTENT="content";
        public static final String COLUMN_DATE="date";
        public static final String COLUMN_PRIORITY="Priority";
    }

}
