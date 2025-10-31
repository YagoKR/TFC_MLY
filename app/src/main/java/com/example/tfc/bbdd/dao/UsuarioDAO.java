package com.example.tfc.bbdd.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tfc.bbdd.definicion.SQLiteHelper;
import com.example.tfc.bbdd.entidades.Campana;
import com.example.tfc.bbdd.entidades.Usuario;

import java.util.ArrayList;

public class UsuarioDAO {
    private SQLiteDatabase db;
    private Context context;

    public UsuarioDAO(Context context) {
        this.context = context;
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
                    new String[]{idUsuario,mail},
                    null, null, null
            );
            if (cursor.moveToFirst()) {
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
            if (cursor.moveToFirst()) {
                valido = true;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return valido;
    }
    public Usuario obtenerUsuario(String idUsuario) {
        Usuario usuario = null;

        Cursor cursor = db.query(
                "Usuarios",
                null,
                "usuario = ?",
                new String[]{idUsuario},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow("Nombre"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("Email"));
            String contrasena = cursor.getString(cursor.getColumnIndexOrThrow("Contraseña"));
            String imagen = cursor.getString(cursor.getColumnIndexOrThrow("Imagen"));

            usuario = new Usuario(idUsuario, nombre, email, contrasena, imagen);
            cursor.close();
        }

        return usuario;
    }

    public Usuario obtenerUsuarioPorEmail(String email) {
        Usuario usuario = null;
        Cursor cursor = null;

        try {
            cursor = db.query(
                    "Usuarios",
                    null,
                    "email = ?",
                    new String[]{email},
                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst()) {
                String idUsuario = cursor.getString(cursor.getColumnIndexOrThrow("Usuario"));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow("Nombre"));
                String contrasena = cursor.getString(cursor.getColumnIndexOrThrow("Contraseña"));
                String imagen = cursor.getString(cursor.getColumnIndexOrThrow("Imagen"));

                usuario = new Usuario(idUsuario, nombre, email, contrasena, imagen);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return usuario;
    }

    public int actualizarUsuario(Usuario usuario) {
        ContentValues values = new ContentValues();
        values.put("nombre", usuario.getNombre());
        values.put("email", usuario.getEmail());
        values.put("contraseña", usuario.getContrasenha());
        values.put("imagen", usuario.getImagen());

        return db.update(
                "Usuarios",
                values,
                "usuario = ?",
                new String[]{usuario.getIdUsuario()}
        );
    }

    public int borrarUsuario(String idUsuario) {
        UsuarioCampanasDAO ucDAO = new UsuarioCampanasDAO(context);
        CampanaDAO campanaDAO = new CampanaDAO(context);

        ArrayList<Campana> campanas = ucDAO.obtenerCampanasDeUsuario(idUsuario);
        for (Campana c : campanas) {
            campanaDAO.borrarCampana(c.getId());
            ucDAO.borrarUsuarioCampana(idUsuario, c.getId());
        }

        return db.delete("Usuarios", "usuario = ?", new String[]{idUsuario});
    }



}
