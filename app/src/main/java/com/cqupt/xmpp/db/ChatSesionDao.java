package com.cqupt.xmpp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cqupt.xmpp.bean.ChatSession;

import java.util.ArrayList;

/**
 * Created by tiandawu on 2016/4/13.
 */
public class ChatSesionDao {

    private DBHelper mDBHelper;

    public ChatSesionDao(Context context) {
        mDBHelper = new DBHelper(context);
    }


    /**
     * 判断数据库中是否已经有这个会话
     *
     * @param from
     * @param to
     * @return
     */
    public boolean isExistTheSession(String from, String to) {

        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        String sql = "select * from " + DBColumns.SESSION_TABLE_NAME + " where " + DBColumns.SESSION_FROM + "=? and " + DBColumns.SESSION_TO + "=?";
        String[] args = new String[]{from, to};
        Cursor cursor = db.rawQuery(sql, args);
        boolean isExist = false;
        while (cursor.moveToNext()) {
            isExist = true;
            break;
        }
        cursor.close();
        db.close();
        return isExist;
    }


    /**
     * 插入一条会话到数据库
     *
     * @param session
     */
    public int insert(ChatSession session) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBColumns.SESSION_FROM, session.getFrom());
        values.put(DBColumns.SESSION_TO, session.getTo());
        values.put(DBColumns.SESSION_BODY, session.getBody());
        values.put(DBColumns.SESSION_OWNER, session.getOwner());
        values.put(DBColumns.SESSION_TIME, session.getTime());
        db.insert(DBColumns.SESSION_TABLE_NAME, null, values);
        db.close();
        int msgid = queryTheLastMsgId();//返回新插入记录的id
        return msgid;
    }


    /**
     * 更新一个会话
     *
     * @param session
     * @return
     */
    public long updateSession(ChatSession session) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBColumns.SESSION_FROM, session.getFrom());
        values.put(DBColumns.SESSION_TO, session.getTo());
        values.put(DBColumns.SESSION_BODY, session.getBody());
        values.put(DBColumns.SESSION_OWNER, session.getOwner());
        values.put(DBColumns.SESSION_TIME, session.getTime());
        long row = db.update(DBColumns.SESSION_TABLE_NAME, values, DBColumns.SESSION_FROM + " = ? and " + DBColumns.SESSION_TO + " = ?",
                new String[]{session.getFrom(), session.getTo()});
        db.close();
        return row;
    }


    /**
     * 查询所有会话
     *
     * @return
     */
    public ArrayList<ChatSession> queryMsg() {
        ArrayList<ChatSession> list = new ArrayList<>();
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        String sql = "select * from " + DBColumns.SESSION_TABLE_NAME + ";";
        Cursor cursor = db.rawQuery(sql, null);
        ChatSession session = null;
        while (cursor.moveToNext()) {
            session = new ChatSession();
            session.setFrom(cursor.getString(cursor.getColumnIndex(DBColumns.SESSION_FROM)));
            session.setTo(cursor.getString(cursor.getColumnIndex(DBColumns.SESSION_TO)));
            session.setBody(cursor.getString(cursor.getColumnIndex(DBColumns.SESSION_BODY)));
            session.setOwner(cursor.getString(cursor.getColumnIndex(DBColumns.SESSION_OWNER)));
            session.setTime(cursor.getString(cursor.getColumnIndex(DBColumns.SESSION_TIME)));
            list.add(0, session);
        }
        cursor.close();
        db.close();
        return list;
    }


    /**
     * 查询最新一条记录的id
     *
     * @return 最后一条记录的id
     */
    public int queryTheLastMsgId() {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        String sql = "select _id" + " from " + DBColumns.SESSION_TABLE_NAME + " order by _id" + " desc limit 1";
        String[] args = new String[]{};
        Cursor cursor = db.rawQuery(sql, args);
        int id = -1;
        if (cursor.moveToNext()) {
            id = cursor.getInt(cursor.getColumnIndex("_id"));
        }
        cursor.close();
        db.close();
        return id;
    }

    /**
     * 根据节点名删除一个会话
     *
     * @return
     */
    public long deleteSessionByFrom(String nodeName) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        long row = db.delete(DBColumns.SESSION_TABLE_NAME, DBColumns.SESSION_FROM + " = ?", new String[]{nodeName});
        db.close();
        return row;
    }

}
