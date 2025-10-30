package com.example.tfc.bbdd.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import androidx.annotation.Nullable;

import com.example.tfc.bbdd.definicion.DbContract;
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
        values.put("Imagen_PJ", personaje.getImagenPJ());
        values.put("ID_Campana", personaje.getIdCampana());
        values.put("ID_Usuario", personaje.getIdUsuario());

        return db.insert("Personajes", null, values);
    }

    public int borrarPersonaje(String nombrePersonaje, String idUsuario, int idCampana) {
        return db.delete("Personajes", "Nombre = ? AND ID_Usuario = ? AND ID_CAMPANA = ?", new String[]{nombrePersonaje, idUsuario, String.valueOf(idCampana)});
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

    public Personaje obtenerPersonajePorId(int idPersonaje) {
        Personaje personaje = null;
        Cursor cursor = null;

        try {
            cursor = db.query(
                    "Personajes",
                    null,
                    BaseColumns._ID + " = ?",
                    new String[]{String.valueOf(idPersonaje)},
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow("Nombre"));
                String raza = cursor.getString(cursor.getColumnIndexOrThrow("Raza"));
                String clase = cursor.getString(cursor.getColumnIndexOrThrow("Clase"));
                String imagen = cursor.getString(cursor.getColumnIndexOrThrow("Imagen_PJ"));
                int idCampana = cursor.getInt(cursor.getColumnIndexOrThrow("ID_Campana"));
                String idUsuario = cursor.getString(cursor.getColumnIndexOrThrow("ID_Usuario"));
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID));

                personaje = new Personaje(nombre, raza, clase, imagen, idCampana, idUsuario);
                personaje.setIdPersonaje(id);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return personaje;
    }

    public int actualizarPersonaje(Personaje personaje) {
        ContentValues values = new ContentValues();
        values.put("Nombre", personaje.getNombre());
        values.put("Raza", personaje.getRaza());
        values.put("Clase", personaje.getClase());
        values.put("Imagen_PJ", personaje.getImagenPJ());
        values.put("ID_Campana", personaje.getIdCampana());
        values.put("ID_Usuario", personaje.getIdUsuario());

        return db.update(
                "Personajes",
                values,
                BaseColumns._ID + " = ?",
                new String[]{String.valueOf(personaje.getIdPersonaje())}
        );
    }

    public ArrayList<Personaje> obtenerPersonajesPorCampanaYUsuario(int idCampana, String idUsuario) {
        ArrayList<Personaje> personajes = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.query(
                    DbContract.PersonajeEntry.TABLE_NAME,
                    null,
                    DbContract.PersonajeEntry.COLUMN_ID_CAMPANA + " = ? AND " +
                            DbContract.PersonajeEntry.COLUMN_ID_USUARIO + " = ?",
                    new String[]{String.valueOf(idCampana), idUsuario},
                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst()) {
                do {
                    String nombre = cursor.getString(cursor.getColumnIndexOrThrow(DbContract.PersonajeEntry.COLUMN_NOMBRE));
                    String raza = cursor.getString(cursor.getColumnIndexOrThrow(DbContract.PersonajeEntry.COLUMN_RAZA));
                    String clase = cursor.getString(cursor.getColumnIndexOrThrow(DbContract.PersonajeEntry.COLUMN_CLASE));
                    String imagen = cursor.getString(cursor.getColumnIndexOrThrow(DbContract.PersonajeEntry.COLUMN_IMAGEN_PJ));
                    int idCamp = cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.PersonajeEntry.COLUMN_ID_CAMPANA));
                    String idUser = cursor.getString(cursor.getColumnIndexOrThrow(DbContract.PersonajeEntry.COLUMN_ID_USUARIO));
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID));

                    Personaje personaje = new Personaje(nombre, raza, clase, imagen, idCamp, idUser);
                    personaje.setIdPersonaje(id);

                    personajes.add(personaje);
                } while (cursor.moveToNext());
            }

        } finally {
            if (cursor != null) cursor.close();
        }

        return personajes;
    }

    public boolean existePersonajeConNombreEnCampana(String nombrePersonaje, int idCampana, @Nullable Integer idPersonajeExcluido) {
        boolean existe = false;
        Cursor cursor = null;

        try {
            String selection = "Nombre = ? AND ID_Campana = ?";
            ArrayList<String> argsList = new ArrayList<>();
            argsList.add(nombrePersonaje);
            argsList.add(String.valueOf(idCampana));

            if (idPersonajeExcluido != null && idPersonajeExcluido != -1) {
                selection += " AND " + BaseColumns._ID + " != ?";
                argsList.add(String.valueOf(idPersonajeExcluido));
            }

            String[] selectionArgs = argsList.toArray(new String[0]);

            cursor = db.query(
                    "Personajes",
                    new String[]{BaseColumns._ID},
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
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

}
