package com.example.tfc.bbdd.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tfc.bbdd.definicion.SQLiteHelper;
import com.example.tfc.bbdd.entidades.Inventario;

import java.util.ArrayList;

public class InventarioDAO {

    private SQLiteDatabase db;
    public InventarioDAO(Context context) {
        SQLiteHelper dbHelper = new SQLiteHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public long insertarItem(Inventario item) {
        ContentValues values = new ContentValues();
        values.put("ID_Personaje", item.getIdPersonaje());
        values.put("Producto", item.getProducto());
        values.put("Categoria", item.getCategoria());
        values.put("Cantidad", item.getCantidad());
        values.put("Precio", item.getPrecio());
        values.put("Valor", item.getValor());
        values.put("Descripcion", item.getDescripcion());
        values.put("Imagen_Item", item.getImagenItem());

        return db.insert("Inventarios", null, values);
    }

    public int borrarItem(int idPersonaje, String producto) {
        String whereClause = "ID_Personaje = ? AND Producto = ?";
        String[] whereArgs = { String.valueOf(idPersonaje), producto };

        return db.delete("Inventarios", whereClause, whereArgs);
    }

    public int actualizarItem(Inventario item) {
        ContentValues values = new ContentValues();
        values.put("Producto", item.getProducto());
        values.put("Categoria", item.getCategoria());
        values.put("Cantidad", item.getCantidad());
        values.put("Precio", item.getPrecio());
        values.put("Valor", item.getValor());
        values.put("Descripcion", item.getDescripcion());
        values.put("Imagen_Item", item.getImagenItem());

        String whereClause = "ID_Personaje = ? AND Producto = ?";
        String[] whereArgs = { String.valueOf(item.getIdPersonaje()), item.getProducto() };

        return db.update("Inventarios", values, whereClause, whereArgs);
    }


    public boolean existeItem(int idPersonaje, String producto) {
        String query = "SELECT 1 FROM Inventarios WHERE ID_Personaje = ? AND Producto = ? LIMIT 1";
        String[] args = { String.valueOf(idPersonaje), producto };

        Cursor cursor = db.rawQuery(query, args);
        boolean existe = cursor.moveToFirst();
        cursor.close();

        return existe;
    }

    public ArrayList<Inventario> obtenerItemsPorPersonaje(int idPersonaje) {
        ArrayList<Inventario> items = new ArrayList<>();
        Cursor cursor = null;

        try {
            String selection = "ID_Personaje = ?";
            String[] selectionArgs = { String.valueOf(idPersonaje) };
            cursor = db.query("Inventarios", null, selection, selectionArgs, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
                    String producto = cursor.getString(cursor.getColumnIndexOrThrow("Producto"));
                    String rareza = cursor.getString(cursor.getColumnIndexOrThrow("Categoria"));
                    int cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("Cantidad"));
                    int precio = cursor.getInt(cursor.getColumnIndexOrThrow("Precio"));
                    String valor = cursor.getString(cursor.getColumnIndexOrThrow("Valor"));
                    String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("Descripcion"));
                    String imagen = cursor.getString(cursor.getColumnIndexOrThrow("Imagen_Item"));

                    Inventario item = new Inventario(id, idPersonaje, producto, rareza, cantidad, precio, valor, descripcion, imagen);
                    items.add(item);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return items;
    }

    public Inventario obtenerItemPorId(long idItem) {
        Inventario item = null;
        Cursor cursor = null;

        try {
            String selection = "_id = ?";
            String[] selectionArgs = { String.valueOf(idItem) };

            cursor = db.query("Inventarios", null, selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
                int idPersonaje = cursor.getInt(cursor.getColumnIndexOrThrow("ID_Personaje"));
                String producto = cursor.getString(cursor.getColumnIndexOrThrow("Producto"));
                String categoria = cursor.getString(cursor.getColumnIndexOrThrow("Categoria"));
                int cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("Cantidad"));
                int precio = cursor.getInt(cursor.getColumnIndexOrThrow("Precio"));
                String valor = cursor.getString(cursor.getColumnIndexOrThrow("Valor"));
                String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("Descripcion"));
                String imagen = cursor.getString(cursor.getColumnIndexOrThrow("Imagen_Item"));

                item = new Inventario(id, idPersonaje, producto, categoria, cantidad, precio, valor, descripcion, imagen);
            }
        } finally {
            if (cursor != null) cursor.close();
        }

        return item;
    }

}
