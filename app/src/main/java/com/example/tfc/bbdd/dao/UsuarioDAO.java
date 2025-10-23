package com.example.tfc.bbdd.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tfc.bbdd.definicion.SQLiteHelper;
import com.example.tfc.bbdd.entidades.Usuario;

public class UsuarioDAO {
    private SQLiteDatabase db;

    public UsuarioDAO(Context context) {
        SQLiteHelper dbHelper = new SQLiteHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public long insertarDatos(Usuario usuario) {
        ContentValues values = new ContentValues();
        values.put ("usuario", usuario.getIdUsuario());
        values.put ("nombre", usuario.getNombre());
        values.put ("email", usuario.getEmail());
        values.put ("contraseña", usuario.getContrasenha());
        values.put ("imagen", usuario.getImagen());

        return db.insert ("Usuarios", null, values);
    }

    public boolean existeUsuario(String idUsuario, String mail) {
        boolean existe = false;
        Cursor cursor = null;
        try {
            cursor = db.query(
                    "Usuarios",
                    new String[]{"usuario", "email"},
                    "usuario = ? OR email = ?",
                    new String[]{idUsuario},
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

    public boolean validarUsuario(String usuario, String contrasena) {
        boolean valido = false;
        Cursor cursor = null;
        try {
            cursor = db.query(
                    "Usuarios",
                    new String[]{"usuario"},
                    "usuario = ? AND contraseña = ?",
                    new String[]{usuario, contrasena},
                    null, null, null
            );
            if (cursor != null && cursor.moveToFirst()) {
                valido = true;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return valido;
    }


}
