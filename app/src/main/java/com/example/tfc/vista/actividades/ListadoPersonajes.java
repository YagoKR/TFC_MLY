package com.example.tfc.vista.actividades;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tfc.R;
import com.example.tfc.bbdd.entidades.Campana;
import com.example.tfc.bbdd.entidades.Personaje;
import com.example.tfc.vista.fragmentos.ListaPersonajes;

public class ListadoPersonajes extends AppCompatActivity implements ListaPersonajes.OnPersonajeSelectedListener {

    public TextView txtNombre, txtDescripcion;
    public ImageView imgCampana;
    public Button btnCrearPersonaje;
    public int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listado_personajes);

        txtNombre = findViewById(R.id.NombreCampaña);
        txtDescripcion = findViewById(R.id.DescriCampaña);
        imgCampana = findViewById(R.id.imageView3);
        btnCrearPersonaje = findViewById(R.id.anadirPJ);

        Campana campana = (Campana) getIntent().getSerializableExtra("campana");
        if (campana != null) {
            txtNombre.setText(campana.getNombreCampanha());
            txtDescripcion.setText(campana.getDescripcion());

            byte[] bytes = Base64.decode(campana.getImagenCampanha(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imgCampana.setImageBitmap(bitmap);
        }

        btnCrearPersonaje.setOnClickListener(v -> {
            Intent intent = new Intent(this, CrearPersonaje.class);
            intent.putExtra("nombreCampanha", campana.getNombreCampanha());
            startActivity(intent);
        });

    }

    @Override
    public void onPersonajeSelected(Personaje personaje) {

    }
}