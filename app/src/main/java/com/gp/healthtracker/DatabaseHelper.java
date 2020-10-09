package com.gp.healthtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;

    public static final String DATABASE_NAME = "tracker.db";
    public static final String TABLE_NAME = "activity_table";
    public static final String COL_ID = "id";
    public static final String COL_TIME = "remindTime";
    public static final String COL_CATEGORY = "category";
    public static final String COL_NOTE = "note";
    public static final String COL_IS_DONE = "isDone";
    public static final String COL_REPEAT = "repeat";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "create table " + TABLE_NAME + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_TIME + " TIME," +
                COL_CATEGORY + " TEXT," +
                COL_NOTE + " TEXT," +
                COL_IS_DONE + " BOOLEAN CHECK (" + COL_IS_DONE + " IN (0, 1))," +
                COL_REPEAT + " TEXT )"
                ;
        db.execSQL(createTable);
    }

    public String getTotalCount() {
        SQLiteDatabase db = getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        long countD = DatabaseUtils.queryNumEntries(db, TABLE_NAME, COL_IS_DONE+"=?", new String[]{"1"});

        String res = Long.toString(countD) + "/" + Long.toString(count);
        return res;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
//        onCreate(db);
    }

    public long addData(String time, String category, String note, String repeat) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_TIME, time);
        contentValues.put(COL_CATEGORY, category);
        contentValues.put(COL_NOTE, note);
        contentValues.put(COL_REPEAT, repeat);
        contentValues.put(COL_IS_DONE, false);

        long result = db.insert(TABLE_NAME, null, contentValues);

        return result;
//        if (result == -1) {
//            return false;
//        } else {
//            return true;
//        }
    }

    public Cursor retrieveData(String category) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE category = \"" + category + "\"", null);
        return data;
    }

    public void updateData(String row_id, String time, String note) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TIME, time);
        contentValues.put(COL_NOTE, note);

        long result = db.update(TABLE_NAME, contentValues, "id=?", new String[]{row_id});

        if (result == -1) {
            Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteRow(String row_id) {
        SQLiteDatabase db = getWritableDatabase();
        long result = db.delete(TABLE_NAME, "id=?", new String[]{row_id});

        if (result == -1) {
            Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
        }
    }

    public void toggleIsDone(boolean isChecked, String row_id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_IS_DONE, isChecked);

        long result = db.update(TABLE_NAME, contentValues, "id=?", new String[]{row_id});

        if (result == -1) {
//            Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(context, "Successfully updated", Toast.LENGTH_SHORT).show();
        }
    }


    public void refreshData() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("update " + TABLE_NAME + " set " + COL_IS_DONE + " = 1");
//        Toast.makeText(context, "Updated to 1", Toast.LENGTH_SHORT).show();
    }
}
