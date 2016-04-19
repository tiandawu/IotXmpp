package com.cqupt.xmpp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cqupt.xmpp.bean.NodeSubStatus;

/**
 * Created by tiandawu on 2016/4/18.
 */
public class NodeStatusDao {

    private DBHelper helper;

    public NodeStatusDao(Context context) {
        helper = new DBHelper(context);
    }


    /**
     * 插入一条订阅状态到数据库
     *
     * @param node
     */
    public int insert(NodeSubStatus node) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DBColumns.NODE_NAME, node.getNodeName());
        values.put(DBColumns.NODE_PERIOD, node.getPeriod());
        values.put(DBColumns.NODE_HIGHLIMIT, node.getHighLimit());
        values.put(DBColumns.NODE_LOWLIMIT, node.getLowLimit());
        db.insert(DBColumns.NODE_TABLE_NAME, null, values);
        db.close();
        int msgid = queryTheLastMsgId();//返回新插入记录的id
        return msgid;
    }


    /**
     * 判断数据库中是否已经有这个节点
     *
     * @param nodeName
     * @return
     */
    public boolean isExistTheNode(String nodeName) {

        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "select * from " + DBColumns.NODE_TABLE_NAME + " where " + DBColumns.NODE_NAME + "=?";
        String[] args = new String[]{nodeName};
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
     * 更新一个节点
     *
     * @param node
     * @return
     */
    public long updateNodeInfo(NodeSubStatus node) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBColumns.NODE_NAME, node.getNodeName());
        values.put(DBColumns.NODE_PERIOD, node.getPeriod());
        values.put(DBColumns.NODE_HIGHLIMIT, node.getHighLimit());
        values.put(DBColumns.NODE_LOWLIMIT, node.getLowLimit());
        long row = db.update(DBColumns.NODE_TABLE_NAME, values, DBColumns.NODE_NAME + "=?",
                new String[]{node.getNodeName()});
        db.close();
        return row;
    }


    /**
     * 查询一个节点的数据
     *
     * @return
     */
    public NodeSubStatus queryOneNodeInfo(String nodeName) {
        NodeSubStatus node = new NodeSubStatus();
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "select * from " + DBColumns.NODE_TABLE_NAME + " where node_name=\"" + nodeName + "\";";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            node.setNodeName(nodeName);
            node.setPeriod(cursor.getString(cursor.getColumnIndex(DBColumns.NODE_PERIOD)));
            node.setHighLimit(cursor.getString(cursor.getColumnIndex(DBColumns.NODE_HIGHLIMIT)));
            node.setLowLimit(cursor.getString(cursor.getColumnIndex(DBColumns.NODE_LOWLIMIT)));
        }
        cursor.close();
        db.close();
        return node;
    }


    /**
     * 根据节点名删除节点
     *
     * @return
     */
    public long deleteMsgByNodeName(String nodeName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        long row = db.delete(DBColumns.NODE_TABLE_NAME, DBColumns.NODE_NAME + " = ?", new String[]{nodeName});
        db.close();
        return row;
    }


    /**
     * 清空所有记录
     */
    public boolean deleteTableData() {
        SQLiteDatabase db = helper.getWritableDatabase();
        int result = db.delete(DBColumns.NODE_TABLE_NAME, null, null);
        db.close();
        if (result > 0) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 查询最新一条记录的id
     *
     * @return 最后一条记录的id
     */
    public int queryTheLastMsgId() {
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "select _id" + " from " + DBColumns.NODE_TABLE_NAME + " order by _id" + " desc limit 1";
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
