package com.cqupt.xmpp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cqupt.xmpp.bean.ChatMessage;

import java.util.ArrayList;

/**
 * Created by tiandawu on 2016/4/11.
 */
public class ChatMsgDao {

    private DBHelper helper;

    public ChatMsgDao(Context context) {
        helper = new DBHelper(context);
    }


    /**
     * 插入一条消息到数据库
     *
     * @param msg
     */
    public int insert(ChatMessage msg) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBColumns.MSG_FROM, msg.getFrom());
        values.put(DBColumns.MSG_TO, msg.getTo());
        values.put(DBColumns.MSG_BODY, msg.getBody());
        values.put(DBColumns.MSG_OWNER, msg.getOwner());
        values.put(DBColumns.MSG_TIME, msg.getTime());
        db.insert(DBColumns.MSG_TABLE_NAME, null, values);
        db.close();
        int msgid = queryTheLastMsgId();//返回新插入记录的id
        return msgid;
    }


    /**
     * 清空所有聊天记录
     */
    public void deleteTableData() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(DBColumns.MSG_TABLE_NAME, null, null);
        db.close();
    }


    /**
     * 查询列表,每页返回15条,依据id逆序查询，将时间最早的记录添加进list的最前面
     *
     * @return
     */
    public ArrayList<ChatMessage> queryMsg(String from, String to, int offset) {
        ArrayList<ChatMessage> list = new ArrayList<ChatMessage>();
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "select * from " + DBColumns.MSG_TABLE_NAME + " where " + DBColumns.MSG_FROM + "=? and " + DBColumns.MSG_TO + "=? order by _id desc limit ?,?";
        String[] args = new String[]{from, to, String.valueOf(offset), "15"};
        Cursor cursor = db.rawQuery(sql, args);
        ChatMessage msg = null;
        while (cursor.moveToNext()) {
            msg = new ChatMessage();
            msg.setFrom(cursor.getString(cursor.getColumnIndex(DBColumns.MSG_FROM)));
            msg.setTo(cursor.getString(cursor.getColumnIndex(DBColumns.MSG_TO)));
            msg.setBody(cursor.getString(cursor.getColumnIndex(DBColumns.MSG_BODY)));
            msg.setOwner(cursor.getString(cursor.getColumnIndex(DBColumns.MSG_OWNER)));
            msg.setTime(cursor.getString(cursor.getColumnIndex(DBColumns.MSG_TIME)));
            list.add(0, msg);
        }
        cursor.close();
        db.close();
        return list;
    }


    /**
     * 查询最新一条记录
     *
     * @return
     */
    public ChatMessage queryTheLastMsg() {
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "select * from " + DBColumns.MSG_TABLE_NAME + " order by _id desc limit 1";
        String[] args = new String[]{};
        Cursor cursor = db.rawQuery(sql, args);

        ChatMessage msg = null;
        while (cursor.moveToNext()) {
            msg = new ChatMessage();
            msg.setFrom(cursor.getString(cursor.getColumnIndex(DBColumns.MSG_FROM)));
            msg.setTo(cursor.getString(cursor.getColumnIndex(DBColumns.MSG_TO)));
            msg.setBody(cursor.getString(cursor.getColumnIndex(DBColumns.MSG_BODY)));
            msg.setOwner(cursor.getString(cursor.getColumnIndex(DBColumns.MSG_OWNER)));
            msg.setTime(cursor.getString(cursor.getColumnIndex(DBColumns.MSG_TIME)));
        }
        cursor.close();
        db.close();
        return msg;
    }

    /**
     * 查询最新一条记录的id
     *
     * @return 最后一条记录的id
     */
    public int queryTheLastMsgId() {
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "select _id" + " from " + DBColumns.MSG_TABLE_NAME + " order by _id" + " desc limit 1";
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
}
