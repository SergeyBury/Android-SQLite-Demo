package by.buryser;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.database.sqlite.SQLiteDatabase.*;

public class DBAdapter {
    private static final String DB_NAME = "myDatabase.db";
    private static final String DB_TABLE = "mainTable";
    private static final int DB_VERSION = 1;

    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final int NAME_COLUMN = 1;

    private static final String DB_CREATE = "create table " + DB_TABLE + " ("
            + KEY_ID + " integer primary key autoincrement, "
            + KEY_NAME + " text not null);";

    private SQLiteDatabase myDatabase;
    private DbHelper dbHelper;
    private final Context context;

    public DBAdapter(Context _context) {
        context = _context;
        dbHelper = new DbHelper(context, DB_NAME, null, DB_VERSION);
        Log.w("adapter", "db adapter constructor!");
    }

    public DBAdapter open() throws SQLException {
        try {
            myDatabase = dbHelper.getWritableDatabase();
            Log.w("adapter", "db adapter open!");
        } catch (SQLiteException e) {
            myDatabase = dbHelper.getReadableDatabase();
            Log.w("adapter", "db adapter sql trouble!");
        }
        return this;
    }

    public void close() {
        myDatabase.close();
    }

    //insert
    public long insertUser(User user) {
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_NAME, user.getName());
        return myDatabase.insert(DB_TABLE, null, newValues);
    }

    //read
    public User getUser(long _rowIndex) {
        String where = KEY_ID + "=" + _rowIndex;
        Cursor cursor = myDatabase.query(DB_TABLE, new String[]{KEY_ID, KEY_NAME},
                where, null, null, null, null);
        User user = new User();
        if (cursor.moveToFirst()) {
            do {
                user.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                user.setName(cursor.getString(cursor.getColumnIndex("name")));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return user;
    }

    //readAll
    public List<User> getAllUsers() {
        Cursor cursor = myDatabase.query(DB_TABLE, new String[]{KEY_ID, KEY_NAME},
                null, null, null, null, null);
        List<User> users = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                user.setName(cursor.getString(cursor.getColumnIndex("name")));
                users.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return users;
    }

    //update
    public boolean updateUser(long _rowIndex, User user) {
        ContentValues updateValues = new ContentValues();
        updateValues.put(KEY_NAME, user.getName());
        String where = KEY_ID + "=" + _rowIndex;
        return myDatabase.update(DB_TABLE, updateValues, where, null) > 0;
    }

    //delete
    public boolean removeUser(long _rowIndex) {
        return myDatabase.delete(DB_TABLE, KEY_ID + "=" + _rowIndex, null) > 0;
    }

    private static class DbHelper extends SQLiteOpenHelper {
        public DbHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase _db) {
            _db.execSQL(DB_CREATE);
            Log.w("adapter", "db created!!!");
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
            Log.w("adapter", "Upgrading from version " + _oldVersion
                    + " to " + _newVersion
                    + ", which will destroy all old data");
            _db.execSQL("drop table if exists " + DB_TABLE);
            onCreate(_db);
        }
    }
}
