package com.example.tfc.bbdd.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tfc.bbdd.definicion.SQLiteHelper;
import com.example.tfc.bbdd.entidades.Personaje;

public class InventarioDAO {

    private SQLiteDatabase db;
    public InventarioDAO(Context context) {
        SQLiteHelper dbHelper = new SQLiteHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public long insertarItem(int idPersonaje, String producto, String rareza, int cantidad, int precio, String descripcion) {
        ContentValues values = new ContentValues();
        values.put("ID_Personaje", idPersonaje);
        values.put("Producto", producto);
        values.put("Rareza", rareza);
        values.put("Cantidad", cantidad);
        values.put("Precio", precio);
        values.put("Descripcion", descripcion);

        return db.insert("Inventarios", null, values);
    }

    public int borrarItem(int idPersonaje, String producto) {
        String whereClause = "ID_Personaje = ? AND Producto = ?";
        String[] whereArgs = { String.valueOf(idPersonaje), producto };

        return db.delete("Inventarios", whereClause, whereArgs);
    }

    public boolean existeItem(int idPersonaje, String producto) {
        String query = "SELECT 1 FROM Inventarios WHERE ID_Personaje = ? AND Producto = ? LIMIT 1";
        String[] args = { String.valueOf(idPersonaje), producto };

        Cursor cursor = db.rawQuery(query, args);
        boolean existe = cursor.moveToFirst();
        cursor.close();

        return existe;
    }
}
