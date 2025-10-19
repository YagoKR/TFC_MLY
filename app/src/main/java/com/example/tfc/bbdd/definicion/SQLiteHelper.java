package com.example.tfc.bbdd.definicion;

import static com.example.tfc.bbdd.definicion.DbContract.SQLITE_CREATE_CAMPANA;
import static com.example.tfc.bbdd.definicion.DbContract.SQLITE_CREATE_USUARIO;
import static com.example.tfc.bbdd.definicion.DbContract.SQLITE_DELETE_CAMPANA;
import static com.example.tfc.bbdd.definicion.DbContract.SQLITE_DELETE_USUARIO;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {
    public static int DATABASE_VERSION = 1;
    public static String DATABASE_NOMBRE = "tfc.db";

    public SQLiteHelper(android.content.Context context) {
        super(context, DATABASE_NOMBRE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLITE_CREATE_USUARIO);
        db.execSQL(SQLITE_CREATE_CAMPANA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQLITE_DELETE_USUARIO);
        db.execSQL(SQLITE_DELETE_CAMPANA);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
