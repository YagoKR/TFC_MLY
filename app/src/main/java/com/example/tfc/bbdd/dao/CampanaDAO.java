package com.example.tfc.bbdd.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.example.tfc.bbdd.definicion.DbContract;
import com.example.tfc.bbdd.definicion.SQLiteHelper;
import com.example.tfc.bbdd.entidades.Campana;

import java.util.ArrayList;

public class CampanaDAO {
    private SQLiteDatabase db;

    public CampanaDAO(Context context) {
        SQLiteHelper dbHelper = new SQLiteHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public long insertarDatos(Campana campana) {
        ContentValues values = new ContentValues();
        values.put("Nombre_campaña", campana.getNombreCampanha());
        values.put("Descripcion", campana.getDescripcion());
        values.put("Imagen_Campaña", campana.getImagenCampanha());

        return db.insert ("Campañas", null, values);
    }

    public int borrarCampana(String nombreCampana) {
        return db.delete("Campañas", "Nombre_campaña = ?", new String[]{nombreCampana});
    }

    public int actualizarCampana(Campana campana, int id) {
        ContentValues values = new ContentValues();
        values.put("Nombre_campaña", campana.getNombreCampanha());
        values.put("Descripcion", campana.getDescripcion());
        values.put("Imagen_Campaña", campana.getImagenCampanha());

        return db.update(
                "Campañas",
                values,
                "_id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public Campana obtenerCampanaPorId(int idCampana) {
        Campana campana = null;

        Cursor cursor = db.query(
                DbContract.CampanaEntry.TABLE_NAME,
                null,
                BaseColumns._ID + " = ?",
                new String[]{String.valueOf(idCampana)},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow(DbContract.CampanaEntry.COLUMN_CAMPANA));
            String descripcion = cursor.getString(cursor.getColumnIndexOrThrow(DbContract.CampanaEntry.COLUMN_DESCRIPCION));
            String imagen = cursor.getString(cursor.getColumnIndexOrThrow(DbContract.CampanaEntry.COLUMN_IMAGEN_CAMPANA));
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID));

            campana = new Campana();
            campana.setId(id);
            campana.setNombreCampanha(nombre);
            campana.setDescripcion(descripcion);
            campana.setImagenCampanha(imagen);

            cursor.close();
        }

        return campana;
    }




}
