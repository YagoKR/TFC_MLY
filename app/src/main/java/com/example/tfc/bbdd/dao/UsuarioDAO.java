package com.example.tfc.bbdd.dao;

import android.content.ContentValues;
import android.content.Context;
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
}
