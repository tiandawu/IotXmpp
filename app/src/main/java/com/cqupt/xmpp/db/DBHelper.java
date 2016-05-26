package com.cqupt.xmpp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tiandawu on 2016/4/10.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String PRIMERY_KEY = "_id";
    private static final String DATABASE_NAME = "iotxmpp.db";
    public static final int DATABASE_VERSION = 1;

    /**
     * 创建message表的SQL语句
     */
    private static final String SQL_CREATE_MESSAGE = "CREATE TABLE IF NOT EXISTS " + DBColumns.MSG_TABLE_NAME +
            "(" + PRIMERY_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + DBColumns.MSG_FROM + TEXT_TYPE + COMMA_SEP
            + DBColumns.MSG_TO + TEXT_TYPE + COMMA_SEP
            + DBColumns.MSG_BODY + TEXT_TYPE + COMMA_SEP
            + DBColumns.MSG_OWNER + TEXT_TYPE + COMMA_SEP
            + DBColumns.MSG_FLAG + TEXT_TYPE + COMMA_SEP
            + DBColumns.MSG_TIME + TEXT_TYPE + ");";

    /**
     * 删除message表的SQL语句
     */
    private static final String SQL_DELETE_MESSAGE = "DROP TABLE IF EXISTS " + DBColumns.MSG_TABLE_NAME + ";";


    /**
     * 创建session表的SQL语句
     */
    private static final String SQL_CREATE_SESSION = "CREATE TABLE IF NOT EXISTS " + DBColumns.SESSION_TABLE_NAME +
            "(" + PRIMERY_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + DBColumns.SESSION_FROM + TEXT_TYPE + COMMA_SEP
            + DBColumns.SESSION_TO + TEXT_TYPE + COMMA_SEP
            + DBColumns.SESSION_BODY + TEXT_TYPE + COMMA_SEP
            + DBColumns.SESSION_OWNER + TEXT_TYPE + COMMA_SEP
            + DBColumns.SESSION_TIME + TEXT_TYPE + ");";

    /**
     * 删除session表的SQL语句
     */
    private static final String SQL_DELETE_SESSION = "DROP TABLE IF EXISTS " + DBColumns.SESSION_TABLE_NAME + ";";

    /**
     * 创建node_status表的sql语句，主要用来记录节点被订阅的状态
     */
    private static final String SQL_CREATE_NODE_STATUS = "CREATE TABLE IF NOT EXISTS " + DBColumns.NODE_TABLE_NAME +
            "(" + PRIMERY_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + DBColumns.NODE_NAME + TEXT_TYPE + COMMA_SEP
            + DBColumns.NODE_PERIOD + TEXT_TYPE + COMMA_SEP
            + DBColumns.NODE_HIGHLIMIT + TEXT_TYPE + COMMA_SEP
            + DBColumns.NODE_LOWLIMIT + TEXT_TYPE + ");";

    /**
     * 删除node_status表的SQL语句
     */
    private static final String SQL_DELETE_NODE_STATUS = "DROP TABLE IF EXISTS " + DBColumns.NODE_TABLE_NAME + ";";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

//        Log.e("tt", "messageSQL=" + SQL_CREATE_MESSAGE);
//        Log.e("tt", "sessionSQL=" + SQL_CREATE_SESSION);
//        Log.e("tt", "nodeStatusSQL=" + SQL_CREATE_NODE_STATUS);
//        Log.e("tt", "delete_node=" + SQL_DELETE_NODE_STATUS);
//        Log.e("tt", "delete_message=" + SQL_DELETE_MESSAGE);
//        Log.e("tt", "delete_session=" + SQL_DELETE_SESSION);
        db.execSQL(SQL_CREATE_MESSAGE);
        db.execSQL(SQL_CREATE_SESSION);
        db.execSQL(SQL_CREATE_NODE_STATUS);
    }

    /**
     * 数据库升级调用
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_MESSAGE);
        db.execSQL(SQL_DELETE_SESSION);
        db.execSQL(SQL_DELETE_NODE_STATUS);
        onCreate(db);
    }

    /**
     * 数据库降级调用
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
