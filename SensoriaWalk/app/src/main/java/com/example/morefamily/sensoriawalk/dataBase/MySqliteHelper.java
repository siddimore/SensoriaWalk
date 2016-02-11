package com.example.morefamily.sensoriawalk.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.morefamily.sensoriawalk.Stat.DBStatistics;

import java.util.LinkedList;
import java.util.List;


/**
 * Created by MoreFamily on 1/10/2016.
 */

public class MySqliteHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "StatDB";

    // Statistics table name
    private static final String TABLE_STATS = "stats";

    // Statistics Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_SPEED = "Speed";
    private static final String KEY_DISTANCE = "Distance";
    private static final String KEY_STEPS = "Steps";
    private static final String KEY_STARTTIME = "Starttime";
    private static final String KEY_ENDTIME = "Endtime";

    private static final String[] COLUMNS = {KEY_ID,KEY_SPEED,KEY_DISTANCE,KEY_STEPS};


    public MySqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create Stats table
        String CREATE_STAT_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_STATS + " (" + KEY_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        KEY_STARTTIME + " VARCHAR(250) NOT NULL, " +
                        KEY_ENDTIME + " VARCHAR(250) NOT NULL, " +
                        KEY_SPEED + " VARCHAR(250) NOT NULL, " +
                        KEY_DISTANCE + " VARCHAR(250) NOT NULL), " +
                        KEY_STEPS + " VARCHAR(250) NOT NULL)";

        // create Stat table
        db.execSQL(CREATE_STAT_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older Stat table if existed
        db.execSQL("DROP TABLE IF EXISTS");

        // create fresh Stats table
        this.onCreate(db);
    }

    public void addStat(DBStatistics Statistics){
        //for logging
        Log.d("AddStats", Statistics.toString());

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_STARTTIME, Statistics.getmStartTime()); // get StartTime
        values.put(KEY_ENDTIME, Statistics.getmEndTime()); // get EndTime
        values.put(KEY_STEPS, Statistics.getmSteps()); // get Steps
        values.put(KEY_DISTANCE, Statistics.getmDistance()); // get Distance
        //values.put(KEY_SPEED, Statistics.getmSpeed()); // get Speed

        // 3. insert
        db.insert(TABLE_STATS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public DBStatistics getStat(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_STATS, // a. table
                        COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build book object
        DBStatistics sessionStat = new DBStatistics();
        sessionStat.setId(Integer.parseInt(cursor.getString(0)));
        sessionStat.setmStartTime(cursor.getString(1));
        sessionStat.setmEndTime(cursor.getString(2));
        sessionStat.setmSteps(cursor.getString(3));
        sessionStat.setmDistance(cursor.getString(4));
        sessionStat.setmSpeed(cursor.getString(5));

        Log.d("getStat(" + id + ")", sessionStat.toString());

        // 5. return book
        return sessionStat;
    }

    // Get All Statistics
    public List<DBStatistics> getAllStats() {

        List<DBStatistics> sessionStatList = new LinkedList<DBStatistics>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_STATS;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build stat and add it to list
        DBStatistics sessionStat = null;
        if (cursor.moveToFirst()) {
            do {
                sessionStat = new DBStatistics();
                sessionStat.setId(Integer.parseInt(cursor.getString(0)));
                sessionStat.setmStartTime(cursor.getString(1));
                sessionStat.setmEndTime(cursor.getString(2));
                sessionStat.setmSteps(cursor.getString(3));
                sessionStat.setmDistance(cursor.getString(4));
                //sessionStat.setmSpeed(cursor.getString(5));
                Log.d("GetStats", sessionStat.toString());

                // Add stat to sessionStatList
                sessionStatList.add(sessionStat);
            } while (cursor.moveToNext());
        }

        Log.d("getAllStats()", sessionStatList.toString());

        // return stat
        return sessionStatList;
    }

    // Updating single Session
    public int updateStat(DBStatistics sessionStat) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("StartTime", sessionStat.getmStartTime()); // get StartTime
        values.put("EndTime", sessionStat.getmEndTime()); // get EndTime
        values.put("Steps", sessionStat.getmSteps()); // get Steps
        values.put("Distance", sessionStat.getmDistance()); // get Distance
        values.put("Speed", sessionStat.getmSpeed()); // get Speed

        // 3. updating row
        int i = db.update(TABLE_STATS, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(sessionStat.getId()) }); //selection args

        // 4. close
        db.close();

        return i;
    }

    // Deleting single SessionStat
    public void deleteStat(DBStatistics sessionStat) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_STATS,
                KEY_ID + " = ?",
                new String[]{String.valueOf(sessionStat.getId())});

        // 3. close
        db.close();

        Log.d("deleteStat", sessionStat.toString());

    }
}
