package com.example.wendersonline.gpslocation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.time.Instant;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME    = "gpslocation.db";
    private static final int    DB_VERSION = 1;

    public static final String TABLE_LOCATION   = "Location";
    public static final String COL_LOC_ID       = "id";
    public static final String COL_LOC_DESC     = "descricao";
    public static final String COL_LOC_LAT      = "latitude";
    public static final String COL_LOC_LNG      = "longitude";

    public static final String TABLE_LOGS        = "Logs";
    public static final String COL_LOG_ID        = "id";
    public static final String COL_LOG_MSG       = "msg";
    public static final String COL_LOG_TIMESTAMP = "timestamp";
    public static final String COL_LOG_ID_LOC    = "id_location";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_LOCATION + " ("
                + COL_LOC_ID   + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_LOC_DESC + " TEXT, "
                + COL_LOC_LAT  + " REAL, "
                + COL_LOC_LNG  + " REAL"
                + ")");

        db.execSQL("CREATE TABLE " + TABLE_LOGS + " ("
                + COL_LOG_ID        + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_LOG_MSG       + " TEXT, "
                + COL_LOG_TIMESTAMP + " TEXT, "
                + COL_LOG_ID_LOC    + " INTEGER, "
                + "FOREIGN KEY (" + COL_LOG_ID_LOC + ") REFERENCES "
                + TABLE_LOCATION + "(" + COL_LOC_ID + ")"
                + ")");

        inserirLocation(db, "Cidade Natal",       -20.551,  -41.8155);
        inserirLocation(db, "Minha casa em Viçosa", -20.7568, -42.8808);
        inserirLocation(db, "DPI/UFV",            -20.765,  -42.8683);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
        onCreate(db);
    }

    private void inserirLocation(SQLiteDatabase db, String descricao, double lat, double lng) {
        ContentValues cv = new ContentValues();
        cv.put(COL_LOC_DESC, descricao);
        cv.put(COL_LOC_LAT,  lat);
        cv.put(COL_LOC_LNG,  lng);
        db.insert(TABLE_LOCATION, null, cv);
    }

    public void inserirLog(int opcao) {
        String msg;
        int idLocation;

        if (opcao == 0) {
            msg        = "Cidade Natal";
            idLocation = 1;
        } else if (opcao == 1) {
            msg        = "Viçosa";
            idLocation = 2;
        } else {
            msg        = "DPI";
            idLocation = 3;
        }

        ContentValues cv = new ContentValues();
        cv.put(COL_LOG_MSG,       msg);
        cv.put(COL_LOG_TIMESTAMP, Instant.now() + "");
        cv.put(COL_LOG_ID_LOC,    idLocation);

        getWritableDatabase().insert(TABLE_LOGS, null, cv);
    }

    public Cursor buscarTodosLogs() {
        return getReadableDatabase().rawQuery(
                "SELECT " + COL_LOG_ID + ", " + COL_LOG_MSG + ", " + COL_LOG_TIMESTAMP
                        + " FROM " + TABLE_LOGS
                        + " ORDER BY " + COL_LOG_ID,
                null);
    }

    public Cursor buscarLatLngDoLog(int logId) {
        return getReadableDatabase().rawQuery(
                "SELECT l." + COL_LOC_LAT + ", l." + COL_LOC_LNG
                        + " FROM " + TABLE_LOGS + " lg"
                        + " INNER JOIN " + TABLE_LOCATION + " l"
                        + " ON lg." + COL_LOG_ID_LOC + " = l." + COL_LOC_ID
                        + " WHERE lg." + COL_LOG_ID + " = ?",
                new String[]{String.valueOf(logId)});
    }
}