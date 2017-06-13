package com.tpv.yongdayang.soundrecorder.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tpv.yongdayang.soundrecorder.bean.RecordingItem;

import java.util.ArrayList;

/**
 * Created by YongDa.Yang on 2017/6/5.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "saved_recordings.db";
    private static final int DATABASE_VERSION = 2;
    public static final String TABLE_NAME = "saved_recordings";
    private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+"( "+
            "_id INTEGER PRIMARY KEY, " +   //id 主键
            "recording_name TEXT, "+  //文件名
            "path TEXT, "+   //文件路径
            "length INTEGER, "+  //文件长度
            "time_added INTEGER"+ //添加的时间
            " ) ";
    private Context mContext;

    public DBHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
        Log.v("CREATE","CONSTRUCTOR");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            db.execSQL(DBHelper.CREATE_TABLE);
        Log.v("CREATE","onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //获取指定位置的Item
    public RecordingItem getItemAt(int position) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.query(DBHelper.TABLE_NAME, null, null, null, null, null, null);
        if (c.moveToPosition(position)) {
            RecordingItem item = new RecordingItem();
            item.setId(c.getInt(c.getColumnIndex("_id")));
            item.setName(c.getString(c.getColumnIndex("recording_name")));
            item.setFilePath(c.getString(c.getColumnIndex("path")));
            item.setLength(c.getInt(c.getColumnIndex("length")));
            item.setTime(c.getLong(c.getColumnIndex("time_added")));
            c.close();
            return item;
        }
        return null;
    }
    //移除指定的Item
    public void removeItemWithId(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String[] whereArgs = { String.valueOf(id) };
        db.delete(DBHelper.TABLE_NAME, "_ID=?", whereArgs);
    }

    //获取行数
    public int getCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(DBHelper.TABLE_NAME, new String[]{"_id"}, null, null, null, null, null);
        int count = c.getCount();
        c.close();
        return count;
    }

    //插入记录
    public long insertRecording(String recordingName, String filePath, long length) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("recording_name", recordingName);
        cv.put("path", filePath);
        cv.put("length", length);
        cv.put("time_added", System.currentTimeMillis());
        long rowId = db.insert(DBHelper.TABLE_NAME, null, cv);  //插入到数据库

        return rowId;
    }

    //更新数据
    public void updateItem(long id, String recordingName, String filePath) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("recording_name", recordingName);
        cv.put("path", filePath);
        db.update(DBHelper.TABLE_NAME, cv, "_id=" + id, null);
    }

    public ArrayList<RecordingItem> getAllItem(){
        ArrayList<RecordingItem> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.query(DBHelper.TABLE_NAME, null, null, null, null, null, null);
        //Log.v("CursorCount", c.getCount()+"");
        while(c.moveToNext()) {
            RecordingItem item = new RecordingItem();
            item.setId(c.getInt(c.getColumnIndex("_id")));
            item.setName(c.getString(c.getColumnIndex("recording_name")));
            item.setFilePath(c.getString(c.getColumnIndex("path")));
            item.setLength(c.getInt(c.getColumnIndex("length")));
            item.setTime(c.getLong(c.getColumnIndex("time_added")));
            items.add(item);
        }
        return items;
    }

}
