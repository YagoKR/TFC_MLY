package com.example.tfc.bbdd.definicion;

import android.provider.BaseColumns;

public class DbContract {
    public static final class UsuarioEntry implements BaseColumns {
        public static final String TABLE_NAME = "Usuarios";
        public static final String COLUMN_USUARIO = "Usuario";
        public static final String COLUMN_NOMBRE = "Nombre";
        public static final String COLUMN_EMAIL = "Email";
        public static final String COLUMN_PASSWORD = "Contraseña";
    }

    public static final class CampanaEntry implements BaseColumns {
        public static final String TABLE_NAME_02 = "Campañas";
        public static final String COLUMN_CAMPANA = "Nombre campaña";
        public static final String COLUMN_DESCRIPCION = "Descripcion";
        public static final String COLUMN_ID_USUARIO= "ID_Usuario";
    }

    public static final class PersonajeEntry implements BaseColumns {
        public static final String TABLE_NAME_03 = "Personajes";
        public static final String COLUMN_ID_CAMPANA = "ID_PJ";
        public static final String COLUMN_NOMBRE = "Nombre";
        public static final String COLUMN_EDAD = "Edad";
        public static final String COLUMN_STATS ="Stats";
    }
    public static final class InventarioEntry implements BaseColumns {
        public static final String TABLE_NAME_03 = "Inventarios";
        public static final String COLUMN_ID_CAMPANA = "ID_Campaña";
        public static final String COLUMN_PRODUCTO = "Producto";
        public static final String COLUMN_RAREZA ="Rareza";
        public static final String COLUMN_CANTIDAD = "Cantidad";
        public static final String COLUMN_DESCRIPCION ="Descripcion";
    }
    public static final String SQLITE_CREATE_USUARIO =
            "CREATE TABLE " + UsuarioEntry.TABLE_NAME + " (" +
                    UsuarioEntry.COLUMN_USUARIO + " TEXT PRIMARY KEY, " +
                    UsuarioEntry.COLUMN_NOMBRE + " TEXT NOT NULL, " +
                    UsuarioEntry.COLUMN_EMAIL + " TEXT NOT NULL UNIQUE, " +
                    UsuarioEntry.COLUMN_PASSWORD + " TEXT NOT NULL" +
                    ");";
    public static final String SQLITE_DELETE_USUARIO =
            "DROP TABLE IF EXISTS " + UsuarioEntry.TABLE_NAME;

    public static final String SQLITE_CREATE_CAMPANA =
            "CREATE TABLE " + CampanaEntry.TABLE_NAME_02 + " (" +
                    CampanaEntry.COLUMN_CAMPANA + " TEXT PRIMARY KEY, " +
                    CampanaEntry.COLUMN_DESCRIPCION + " TEXT," +
                    "FOREIGN KEY ("+ CampanaEntry.COLUMN_ID_USUARIO +" ) REFERENCES" +
                    UsuarioEntry.TABLE_NAME + "("+ UsuarioEntry.COLUMN_USUARIO +") ON DELETE CASCADE" +
                    ");";

    public static final String SQLITE_DELETE_CAMPANA =
            "DROP TABLE IF EXISTS " + CampanaEntry.TABLE_NAME_02;
}
