package com.example.tfc.vista.actividades;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tfc.R;
import com.example.tfc.bbdd.dao.CampanaDAO;
import com.example.tfc.bbdd.entidades.Campana;
import com.example.tfc.bbdd.entidades.Personaje;
import com.example.tfc.vista.fragmentos.ListaPersonajes;

public class ListadoPersonajes extends AppCompatActivity implements ListaPersonajes.OnPersonajeSelectedListener {

    public TextView txtNombre, txtDescripcion;
    public ImageView imgCampana;
    public Button btnCrearPersonaje;
    public int idCampana;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listado_personajes);

        txtNombre = findViewById(R.id.NombreCampaña);
        txtDescripcion = findViewById(R.id.DescriCampaña);
        imgCampana = findViewById(R.id.imageView3);
        btnCrearPersonaje = findViewById(R.id.anadirPJ);
        toolbar = findViewById(R.id.toolbarListadoCampanas);
        setSupportActionBar(toolbar);


        Campana campana = (Campana) getIntent().getSerializableExtra("campana");
        CampanaDAO cDAO = new CampanaDAO(getApplicationContext());
        idCampana = cDAO.obtenerIdCampana(campana.getNombreCampanha());

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item_02, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.editarCampana) {
            Intent intent = new Intent(ListadoPersonajes.this, EditarCampana.class);
            intent.putExtra("campana", (Campana) getIntent().getSerializableExtra("campana"));
            intent.putExtra("id",idCampana);
            startActivity(intent);
            return true;
        }

        if (id == R.id.salirCampana) {
            finishAffinity();
            System.exit(0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPersonajeSelected(Personaje personaje) {

    }

    private void actualizarDatosCampana() {
        CampanaDAO cDAO = new CampanaDAO(getApplicationContext());
        Campana campanaActualizada = cDAO.obtenerCampanaPorId(idCampana);

        if (campanaActualizada != null) {
            txtNombre.setText(campanaActualizada.getNombreCampanha());
            txtDescripcion.setText(campanaActualizada.getDescripcion());

            try {
                if (campanaActualizada.getImagenCampanha() != null && !campanaActualizada.getImagenCampanha().isEmpty()) {
                    byte[] bytes = android.util.Base64.decode(campanaActualizada.getImagenCampanha(), android.util.Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imgCampana.setImageBitmap(bitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarDatosCampana();
    }

}