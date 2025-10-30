package com.example.tfc.bbdd.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import androidx.annotation.Nullable;

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

    public boolean existeCampanaConNombre(String idUsuario, String nombreCampana, @Nullable Integer idCampanaExcluida) {
        boolean existe = false;
        Cursor cursor = null;

        try {
            String tables = DbContract.CampanaEntry.TABLE_NAME + " c INNER JOIN " +
                    DbContract.UsuariosCampanasEntry.TABLE_NAME + " uc ON " +
                    "c." + BaseColumns._ID + " = uc." + DbContract.UsuariosCampanasEntry.COLUMN_ID_CAMPANA;

            String selection = "uc." + DbContract.UsuariosCampanasEntry.COLUMN_ID_USUARIO + " = ? AND " +
                    "c." + DbContract.CampanaEntry.COLUMN_CAMPANA + " = ?";

            ArrayList<String> selectionArgsList = new ArrayList<>();
            selectionArgsList.add(idUsuario);
            selectionArgsList.add(nombreCampana);

            if (idCampanaExcluida != null && idCampanaExcluida != -1) {
                selection += " AND c." + BaseColumns._ID + " != ?";
                selectionArgsList.add(String.valueOf(idCampanaExcluida));
            }

            String[] selectionArgs = selectionArgsList.toArray(new String[0]);

            cursor = db.query(
                    tables,
                    new String[]{"c." + BaseColumns._ID},
                    selection,
                    selectionArgs,
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                existe = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }

        return existe;
    }




}
