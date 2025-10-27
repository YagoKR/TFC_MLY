package com.example.tfc.bbdd.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tfc.bbdd.definicion.SQLiteHelper;
import com.example.tfc.bbdd.entidades.Personaje;

import java.util.ArrayList;

public class PersonajeDAO {

    private SQLiteDatabase db;
    public PersonajeDAO(Context context) {
        SQLiteHelper dbHelper = new SQLiteHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public long insertarDatos(Personaje personaje) {
        ContentValues values = new ContentValues();
        values.put("Nombre", personaje.getNombre());
        values.put("Raza", personaje.getRaza());
        values.put("Clase", personaje.getClase());
        values.put("Imagen_PJ", personaje.getImagen());
        values.put("ID_Campana", personaje.getIdCampana());
        values.put("ID_Usuario", personaje.getIdUsuario());

        return db.insert("Personajes", null, values);
    }

    public int borrarPersonaje(String nombrePersonaje) {
        return db.delete("Personajes", "Nombre = ?", new String[]{nombrePersonaje});
    }
    public boolean existePersonaje(String nombrePersonaje, int idCampana, String idUsuario) {
        boolean existe = false;
        Cursor cursor = null;
        try {
            cursor = db.query(
                    "Personajes",
                    new String[]{"Nombre"},
                    "Nombre = ? AND id_campana = ? AND id_usuario = ?",
                    new String[]{nombrePersonaje, String.valueOf(idCampana), idUsuario},
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
    public ArrayList<Personaje> obtenerTodosPersonajes() {
        ArrayList<Personaje> personajes = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query("Personajes", null, null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String nombrePersonaje = cursor.getString(cursor.getColumnIndexOrThrow("Nombre"));
                    String raza = cursor.getString(cursor.getColumnIndexOrThrow("Raza"));
                    String clase = cursor.getString(cursor.getColumnIndexOrThrow("Clase"));
                    String imagenPersonaje = cursor.getString(cursor.getColumnIndexOrThrow("Imagen_PJ"));
                    int idCampana = cursor.getInt(cursor.getColumnIndexOrThrow("ID_Campana"));
                    String idUsuario = cursor.getString(cursor.getColumnIndexOrThrow("ID_Usuario"));

                    Personaje pesonaje = new Personaje(nombrePersonaje, raza, clase, imagenPersonaje, idCampana, idUsuario);
                    personajes.add(pesonaje);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return personajes;
    }

}
