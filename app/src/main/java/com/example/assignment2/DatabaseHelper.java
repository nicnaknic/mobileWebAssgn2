package com.example.assignment2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "TaskAppDB.db";
    public static final String TASK_TABLE = "task_table";
    public static final String CLIENT_TABLE = "client_table";

    public static final String TASK_COL0 = "ID";
    public static final String TASK_COL1 = "task_id";
    public static final String TASK_COL2 = "task_desc";
    public static final String TASK_COL3 = "deadline";
    public static final String TASK_COL4 = "client_id";

    public static final String CLIENT_COL0 = "ID";
    public static final String CLIENT_COL1 = "client_id";
    public static final String CLIENT_COL2 = "name";
    public static final String CLIENT_COL3 = "address";
    public static final String CLIENT_COL4 = "birth_date";
    public static final String CLIENT_COL5 = "photo_url";

    Context mContext;


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null , 1);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TASK_TABLE + "( "
                    + TASK_COL0 + " INTEGER PRIMARY KEY, "
                    + TASK_COL1 + " INTEGER, "
                    + TASK_COL2 + " TEXT, "
                    + TASK_COL3 + " TEXT, "
                    + TASK_COL4 + " INTEGER )" );
        db.execSQL("CREATE TABLE " + CLIENT_TABLE + "( "
                    + CLIENT_COL0 + " INTEGER PRIMARY KEY, "
                    + CLIENT_COL1 + " INTEGER, "
                    + CLIENT_COL2 + " TEXT, "
                    + CLIENT_COL3 + " TEXT, "
                    + CLIENT_COL4 + " TEXT, "
                    + CLIENT_COL5 + " TEXT )" );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TASK_TABLE );
        db.execSQL("DROP TABLE IF EXISTS " + CLIENT_TABLE );

        onCreate(db);
    }

    public void clearDataBase() {
        mContext.deleteDatabase("TaskAppDB.db");
    }

    public boolean insertTaskData(int taskID, String taskDesc, String deadline, int clientID) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(TASK_COL1, taskID);
        contentValues.put(TASK_COL2, taskDesc);
        contentValues.put(TASK_COL3, deadline);
        contentValues.put(TASK_COL4, clientID);

        long result = db.insert(TASK_TABLE, null, contentValues);


        if(result == -1){
            return false;
        } else { return true; }

        //Follow this tutorial for more help.
        //https://youtu.be/kDZES1wtKUY?t=1935
    }

    public boolean insertClientData(int clientID, String name, String address, String birthDate, String photoURL) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(CLIENT_COL1, clientID);
        contentValues.put(CLIENT_COL2, name);
        contentValues.put(CLIENT_COL3, address);
        contentValues.put(CLIENT_COL4, birthDate);
        contentValues.put(CLIENT_COL5, photoURL);

        long result = db.insert(CLIENT_TABLE, null, contentValues);

        if(result == -1){
            return false;
        } else { return true; }
    }

    public Cursor getCompleteTaskList() {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor res = db.rawQuery("SELECT " + TASK_COL1 + ", " + TASK_COL2 + ", " +
                "                       " + TASK_COL3 + ", " + TASK_TABLE+ "." + TASK_COL4 + ", " +
                "                       " + CLIENT_COL2 + ", " + CLIENT_COL3 + ", " + CLIENT_COL4 + ", " + CLIENT_COL5 +
                "                      FROM " + TASK_TABLE +
                "                      INNER JOIN " + CLIENT_TABLE +
                "                      ON " + CLIENT_TABLE+ "." + CLIENT_COL1 + " = " + TASK_TABLE+ "." + TASK_COL4, null);

        return res;
    }

    public Cursor getCompleteClientList() {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor res = db.rawQuery("SELECT " + CLIENT_COL1 + ", " + CLIENT_COL2 +
                "                      FROM " + CLIENT_TABLE, null);

        return res;

    }

    public Cursor getTaskDescription( int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor res = db.rawQuery("SELECT " + TASK_COL2 +
                "                      FROM " + TASK_TABLE +
                "                      WHERE " + TASK_COL1 + " = " + id, null);

        return res;
    }

    public Cursor getTaskDeadline( int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor res = db.rawQuery("SELECT " + TASK_COL3 +
                "                      FROM " + TASK_TABLE +
                "                      WHERE " + TASK_COL1 + " = " + id, null);

        return res;
    }

    public Cursor getTaskClientID( int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor res = db.rawQuery("SELECT " + TASK_COL4 +
                "                      FROM " + TASK_TABLE +
                "                      WHERE " + TASK_COL1 + " = " + id, null);

        return res;
    }

    public Cursor getClientAddress( int id ) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor res = db.rawQuery("SELECT " + CLIENT_COL3 +
                "                      FROM " + CLIENT_TABLE +
                "                      WHERE " + CLIENT_COL1 + " = " + id, null);

        return res;
    }

    public void deleteTaskFromDatabase( int id ) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] whereArgs = new String[] { String.valueOf(id) };
        db.delete(TASK_TABLE, TASK_COL1 + " =? ", whereArgs );

    }

    public void updateTaskFromDatabase(int id, String description, String deadline ) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(TASK_COL2, description);
        contentValues.put(TASK_COL3, deadline);

        db.update(TASK_TABLE, contentValues, TASK_COL1 + " = " + id, null);
    }

    public void updateTaskClientFromDatabase(int id, int newClientID ) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(TASK_COL4, newClientID);

        db.update(TASK_TABLE, contentValues, TASK_COL1 + " = " + id, null);
    }
}
