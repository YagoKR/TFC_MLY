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
import com.example.tfc.bbdd.entidades.Personaje;

import java.util.ArrayList;

public class PersonajeAdapter extends ArrayAdapter<Personaje> {

    public PersonajeAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull ArrayList<Personaje> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Personaje personaje = getItem(position);

        if (convertView==null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.personajes_element, parent, false);
        }

        TextView nombrePersonaje = convertView.findViewById(R.id.textViewNombrePersonaje);
        TextView stats = convertView.findViewById(R.id.textViewStats);
        ImageView imagenPersonaje = convertView.findViewById(R.id.imageViewPj);
        nombrePersonaje.setText(personaje.getNombre() +" ("+ personaje.getRaza() +")");
        stats.setText(personaje.getClase());


        String imagenBase64 = personaje.getImagen();

        if (imagenBase64 != null && !imagenBase64.isEmpty()) {
            try {
                byte[] bytes = android.util.Base64.decode(imagenBase64, android.util.Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imagenPersonaje.setImageBitmap(bitmap);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                imagenPersonaje.setImageResource(R.drawable.ic_launcher_background);
            }
        } else {
            imagenPersonaje.setImageResource(R.drawable.woman_avatar_proof);
        }
        return convertView;
    }
}