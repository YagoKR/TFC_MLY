package com.example.tfc.bbdd.definicion;

import static com.example.tfc.bbdd.definicion.DbContract.*;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {
    public static int DATABASE_VERSION = 1;
    public static String DATABASE_NOMBRE = "tfc.db";

    public SQLiteHelper(android.content.Context context) {
        super(context, DATABASE_NOMBRE, null, DATABASE_VERSION);
    }
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLITE_CREATE_USUARIO);
        db.execSQL(SQLITE_CREATE_CAMPANA);
        db.execSQL(SQLITE_CREATE_PERSONAJE);
        db.execSQL(SQLITE_CREATE_USUARIOS_CAMPANAS);
        db.execSQL(SQLITE_CREATE_INVENTARIO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQLITE_DELETE_USUARIO);
        db.execSQL(SQLITE_DELETE_CAMPANA);
        db.execSQL(SQLITE_DELETE_PERSONAJE);
        db.execSQL(SQLITE_DELETE_INVENTARIO);
        db.execSQL(SQLITE_DELETE_USUARIOS_CAMPANAS);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
