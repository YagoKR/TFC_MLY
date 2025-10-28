package com.example.tfc.bbdd.definicion;

import android.provider.BaseColumns;

public class DbContract {
    public static final class UsuarioEntry implements BaseColumns {
        public static final String TABLE_NAME = "Usuarios";
        public static final String COLUMN_USUARIO = "Usuario";
        public static final String COLUMN_NOMBRE = "Nombre";
        public static final String COLUMN_EMAIL = "Email";
        public static final String COLUMN_PASSWORD = "Contraseña";
        public static final String COLUMN_IMAGEN = "Imagen";
    }

    public static final String SQLITE_CREATE_USUARIO =
            "CREATE TABLE " + UsuarioEntry.TABLE_NAME + " (" +
                    UsuarioEntry.COLUMN_USUARIO + " TEXT PRIMARY KEY, " +
                    UsuarioEntry.COLUMN_NOMBRE + " TEXT NOT NULL, " +
                    UsuarioEntry.COLUMN_IMAGEN + " TEXT NOT NULL, " +
                    UsuarioEntry.COLUMN_EMAIL + " TEXT NOT NULL UNIQUE, " +
                    UsuarioEntry.COLUMN_PASSWORD + " TEXT NOT NULL);";

    public static final String SQLITE_DELETE_USUARIO =
            "DROP TABLE IF EXISTS " + UsuarioEntry.TABLE_NAME;

    public static final class CampanaEntry implements BaseColumns {
        public static final String TABLE_NAME = "Campañas";
        public static final String COLUMN_CAMPANA = "Nombre_campaña";
        public static final String COLUMN_DESCRIPCION = "Descripcion";
        public static final String COLUMN_IMAGEN_CAMPANA = "Imagen_Campaña";
    }

    public static final String SQLITE_CREATE_CAMPANA =
            "CREATE TABLE " + CampanaEntry.TABLE_NAME + " (" +
                    BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    CampanaEntry.COLUMN_CAMPANA + " TEXT NOT NULL, " +
                    CampanaEntry.COLUMN_IMAGEN_CAMPANA + " TEXT, " +
                    CampanaEntry.COLUMN_DESCRIPCION + " TEXT);";

    public static final String SQLITE_DELETE_CAMPANA =
            "DROP TABLE IF EXISTS " + CampanaEntry.TABLE_NAME;

    public static final class UsuariosCampanasEntry {
        public static final String TABLE_NAME = "Usuarios_Campañas";
        public static final String COLUMN_ID_USUARIO = "ID_Usuario";
        public static final String COLUMN_ID_CAMPANA = "ID_Campaña";
    }

    public static final String SQLITE_CREATE_USUARIOS_CAMPANAS =
            "CREATE TABLE " + UsuariosCampanasEntry.TABLE_NAME + " (" +
                    UsuariosCampanasEntry.COLUMN_ID_USUARIO + " TEXT NOT NULL, " +
                    UsuariosCampanasEntry.COLUMN_ID_CAMPANA + " INTEGER NOT NULL, " +
                    "FOREIGN KEY (" + UsuariosCampanasEntry.COLUMN_ID_USUARIO + ") REFERENCES " +
                    UsuarioEntry.TABLE_NAME + "(" + UsuarioEntry.COLUMN_USUARIO + ") ON DELETE CASCADE, " +
                    "FOREIGN KEY (" + UsuariosCampanasEntry.COLUMN_ID_CAMPANA + ") REFERENCES " +
                    CampanaEntry.TABLE_NAME + "(" + BaseColumns._ID + ") ON DELETE CASCADE, " +
                    "PRIMARY KEY (" + UsuariosCampanasEntry.COLUMN_ID_USUARIO + ", " + UsuariosCampanasEntry.COLUMN_ID_CAMPANA + "));";

    public static final String SQLITE_DELETE_USUARIOS_CAMPANAS =
            "DROP TABLE IF EXISTS " + UsuariosCampanasEntry.TABLE_NAME;

    public static final class PersonajeEntry implements BaseColumns {
        public static final String TABLE_NAME = "Personajes";
        public static final String COLUMN_ID_CAMPANA = "ID_Campana";
        public static final String COLUMN_ID_USUARIO = "ID_Usuario";
        public static final String COLUMN_NOMBRE = "Nombre";
        public static final String COLUMN_RAZA = "Raza";
        public static final String COLUMN_CLASE = "Clase";
        public static final String COLUMN_IMAGEN_PJ = "Imagen_PJ";
    }

    public static final String SQLITE_CREATE_PERSONAJE =
            "CREATE TABLE " + PersonajeEntry.TABLE_NAME + " (" +
                    BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PersonajeEntry.COLUMN_NOMBRE + " TEXT NOT NULL, " +
                    PersonajeEntry.COLUMN_RAZA + " TEXT NOT NULL, " +
                    PersonajeEntry.COLUMN_CLASE + " TEXT NOT NULL, " +
                    PersonajeEntry.COLUMN_IMAGEN_PJ + " TEXT, " +
                    PersonajeEntry.COLUMN_ID_CAMPANA + " INTEGER NOT NULL, " +
                    PersonajeEntry.COLUMN_ID_USUARIO + " TEXT NOT NULL, " +
                    "FOREIGN KEY (" + PersonajeEntry.COLUMN_ID_CAMPANA + ") REFERENCES " +
                    CampanaEntry.TABLE_NAME + "(" + BaseColumns._ID + ") ON DELETE CASCADE, " +
                    "FOREIGN KEY (" + PersonajeEntry.COLUMN_ID_USUARIO + ") REFERENCES " +
                    UsuarioEntry.TABLE_NAME + "(" + UsuarioEntry.COLUMN_USUARIO + ") ON DELETE CASCADE);";

    public static final String SQLITE_DELETE_PERSONAJE =
            "DROP TABLE IF EXISTS " + PersonajeEntry.TABLE_NAME;

    public static final class InventarioEntry implements BaseColumns {
        public static final String TABLE_NAME = "Inventarios";
        public static final String COLUMN_ID_PERSONAJE = "ID_Personaje";
        public static final String COLUMN_PRODUCTO = "Producto";
        public static final String COLUMN_CATEGORIA = "Categoria";
        public static final String COLUMN_CANTIDAD = "Cantidad";
        public static final String COLUMN_PRECIO = "Precio";
        public static final String COLUMN_VALOR = "Valor";
        public static final String COLUMN_IMAGEN_ITEM = "Imagen_Item";
        public static final String COLUMN_DESCRIPCION = "Descripcion";
    }

    public static final String SQLITE_CREATE_INVENTARIO =
            "CREATE TABLE " + InventarioEntry.TABLE_NAME + " (" +
                    BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    InventarioEntry.COLUMN_ID_PERSONAJE + " INTEGER NOT NULL, " +
                    InventarioEntry.COLUMN_PRODUCTO + " TEXT NOT NULL, " +
                    InventarioEntry.COLUMN_CATEGORIA + " TEXT, " +
                    InventarioEntry.COLUMN_CANTIDAD + " INTEGER, " +
                    InventarioEntry.COLUMN_PRECIO + " INTEGER, " +
                    InventarioEntry.COLUMN_VALOR + " TEXT, " +
                    InventarioEntry.COLUMN_DESCRIPCION + " TEXT, " +
                    InventarioEntry.COLUMN_IMAGEN_ITEM + " TEXT, " +
                    "FOREIGN KEY (" + InventarioEntry.COLUMN_ID_PERSONAJE + ") REFERENCES " +
                    PersonajeEntry.TABLE_NAME + "(" + BaseColumns._ID + ") ON DELETE CASCADE);";

    public static final String SQLITE_DELETE_INVENTARIO =
            "DROP TABLE IF EXISTS " + InventarioEntry.TABLE_NAME;

}
