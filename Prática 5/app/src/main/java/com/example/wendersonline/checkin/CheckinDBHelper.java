package com.example.wendersonline.checkin;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CheckinDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "checkin.db";
    private static final int DB_VERSION = 1;

    public CheckinDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Categoria (" +
                "idCategoria INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT NOT NULL)");

        db.execSQL("CREATE TABLE Checkin (" +
                "Local TEXT PRIMARY KEY, " +
                "qtdVisitas INTEGER NOT NULL, " +
                "cat INTEGER NOT NULL, " +
                "latitude TEXT NOT NULL, " +
                "longitude TEXT NOT NULL, " +
                "CONSTRAINT fkey0 FOREIGN KEY (cat) REFERENCES Categoria (idCategoria))");

        String[] categorias = {"Restaurante", "Bar", "Cinema", "Universidade",
                "Estádio", "Parque", "Outros"};
        for (String nome : categorias) {
            ContentValues cv = new ContentValues();
            cv.put("nome", nome);
            db.insert("Categoria", null, cv);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Checkin");
        db.execSQL("DROP TABLE IF EXISTS Categoria");
        onCreate(db);
    }
}