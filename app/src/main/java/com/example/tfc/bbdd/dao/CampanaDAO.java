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

    public ArrayList<Campana> obtenerTodasCampanas() {
        ArrayList<Campana> campanas = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query("Campañas", null, null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String nombreCampanha = cursor.getString(cursor.getColumnIndexOrThrow("Nombre_campaña"));
                    String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("Descripcion"));
                    String imagenCampanha = cursor.getString(cursor.getColumnIndexOrThrow("Imagen_Campaña"));

                    Campana campana = new Campana(nombreCampanha, descripcion, imagenCampanha);
                    campanas.add(campana);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return campanas;
    }
    public boolean existeCampana(String nombreCampana) {
        boolean existe = false;
        Cursor cursor = null;
        try {
            cursor = db.query(
                    "Campañas",
                    new String[]{"Nombre_campaña"},
                    "Nombre_campaña = ?",
                    new String[]{nombreCampana},
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                existe = true;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return existe;
    }

    public int obtenerIdCampana(String nombreCampana) {
        int id = -1;
        Cursor cursor = null;
        try {
            cursor = db.query(
                    "Campañas",
                    new String[]{"_id"},
                    "Nombre_campaña = ?",
                    new String[]{nombreCampana},
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return id;
    }

    public int actualizarCampana(Campana campana, int id) {
        ContentValues values = new ContentValues();
        values.put("Nombre_campaña", campana.getNombreCampanha());
        values.put("Descripcion", campana.getDescripcion());
        values.put("Imagen_Campaña", campana.getImagenCampanha());

        // Usar el ID para localizar la fila correcta
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

        if (cursor != null && cursor.moveToFirst()) {
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow(DbContract.CampanaEntry.COLUMN_CAMPANA));
            String descripcion = cursor.getString(cursor.getColumnIndexOrThrow(DbContract.CampanaEntry.COLUMN_DESCRIPCION));
            String imagen = cursor.getString(cursor.getColumnIndexOrThrow(DbContract.CampanaEntry.COLUMN_IMAGEN_CAMPANA));

            campana = new Campana();
            campana.setNombreCampanha(nombre);
            campana.setDescripcion(descripcion);
            campana.setImagenCampanha(imagen);

            cursor.close();
        }

        return campana;
    }




}
