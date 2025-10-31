package com.example.tfc.bbdd.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tfc.bbdd.definicion.SQLiteHelper;
import com.example.tfc.bbdd.entidades.Campana;
import com.example.tfc.bbdd.entidades.UsuariosCampanas;

import java.util.ArrayList;

public class UsuarioCampanasDAO {
    private SQLiteDatabase db;

    public UsuarioCampanasDAO(Context context) {
        SQLiteHelper dbHelper = new SQLiteHelper(context);
        db = dbHelper.getWritableDatabase();
    }
    public long insertarDatos(UsuariosCampanas ucamp) {
        ContentValues values = new ContentValues();
        values.put("ID_Usuario", ucamp.getIdUsuario());
        values.put("ID_Campaña", ucamp.getIdCampana());

        return db.insert("Usuarios_Campañas", null, values);
    }

    public int borrarUsuarioCampana(String idUsuario, int idCampana) {
        return db.delete("Usuarios_Campañas", "ID_Usuario = ? AND ID_Campaña = ?", new String[]{idUsuario, String.valueOf(idCampana)});
    }

    public boolean existeCampanaParaUsuario(String idUsuario, String nombreCampana) {
        boolean existe = false;
        Cursor cursor = null;
        try {
            cursor = db.query(
                    "Campañas c JOIN Usuarios_Campañas uc ON c._id = uc.ID_Campaña",
                    new String[]{"c._ID"},
                    "uc.ID_Usuario = ? AND c.Nombre_campaña = ?",
                    new String[]{idUsuario, nombreCampana},
                    null, null, null
            );
            if (cursor != null && cursor.moveToFirst()) {
                existe = true;
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return existe;
    }
    public ArrayList<Campana> obtenerCampanasDeUsuario(String idUsuario) {
        ArrayList<Campana> campanas = new ArrayList<>();
        Cursor cursor = null;

        try {
            String query = "SELECT c._id, c.Nombre_campaña, c.Descripcion, c.Imagen_Campaña " +
                    "FROM Campañas c " +
                    "INNER JOIN Usuarios_Campañas uc ON c._ID = uc.ID_Campaña " +
                    "WHERE uc.ID_Usuario = ?";

            cursor = db.rawQuery(query, new String[]{idUsuario});

            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                    String nombre = cursor.getString(cursor.getColumnIndexOrThrow("Nombre_campaña"));
                    String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("Descripcion"));
                    String imagen = cursor.getString(cursor.getColumnIndexOrThrow("Imagen_Campaña"));

                    Campana campana = new Campana(nombre, descripcion, imagen);
                    campana.setId(id);
                    campanas.add(campana);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }

        return campanas;
    }

}
