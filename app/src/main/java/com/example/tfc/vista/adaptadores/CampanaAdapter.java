package com.example.tfc.vista.adaptadores;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;

import com.example.tfc.R;
import com.example.tfc.bbdd.entidades.Campana;

public class CampanaAdapter extends ArrayAdapter<Campana> {
    public CampanaAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull Campana[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Campana campana = getItem(position);

        if (convertView==null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.campana_element, parent, false);
        }

        TextView nombreCampana = convertView.findViewById(R.id.textViewNombre);
        TextView descripcionCampana = convertView.findViewById(R.id.textViewDescripcion);
        ImageView imagenCampana = convertView.findViewById(R.id.imageView);
        nombreCampana.setText(campana.getNombreCampanha());
        descripcionCampana.setText(campana.getDescripcion());

        String imagenBase64 = campana.getImagenCampanha();

        if (imagenBase64 != null && !imagenBase64.isEmpty()) {
            try {
                byte[] bytes = android.util.Base64.decode(imagenBase64, android.util.Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imagenCampana.setImageBitmap(bitmap);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                imagenCampana.setImageResource(R.drawable.ic_launcher_background);
            }
        } else {
            imagenCampana.setImageResource(R.drawable.woman_avatar_proof);
        }
        return convertView;
    }
}
