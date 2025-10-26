package com.example.tfc.bbdd.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tfc.bbdd.definicion.SQLiteHelper;
import com.example.tfc.bbdd.entidades.UsuariosCampanas;

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

    public boolean existeCampanaParaUsuario(String idUsuario, String nombreCampana) {
        boolean existe = false;
        Cursor cursor = null;
        try {
            cursor = db.query(
                    "Campañas c JOIN Usuarios_Campañas uc ON c._ID = uc.ID_Campaña",
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


}
