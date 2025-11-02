package com.example.tfc.vista.adaptadores;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.tfc.R;
import com.example.tfc.bbdd.entidades.Inventario;

import java.util.ArrayList;

public class InventarioAdapter extends ArrayAdapter<Inventario> {
    public InventarioAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull ArrayList<Inventario> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Inventario inventario = getItem(position);

        if (convertView==null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.inventario_element, parent, false);
        }

        TextView nombreItem = convertView.findViewById(R.id.textViewNombreItem);
        TextView infoItem = convertView.findViewById(R.id.textViewInfoItem);
        ImageView imagenPersonaje = convertView.findViewById(R.id.imageViewItem);

        nombreItem.setText(inventario.getProducto() + " ("+inventario.getCategoria()+")");
        infoItem.setText(inventario.getDescripcion() +"\n" + inventario.getCantidad() + "\n" + inventario.getPrecio() + " " + inventario.getValor());

        String imagenBase64 = inventario.getImagenItem();

        if (imagenBase64 != null && !imagenBase64.isEmpty()) {
            try {
                byte[] bytes = android.util.Base64.decode(imagenBase64, android.util.Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imagenPersonaje.setImageBitmap(bitmap);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                imagenPersonaje.setImageResource(R.drawable.ic_launcher_background);
            }
        }
        return convertView;
    }
}
