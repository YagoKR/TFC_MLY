package com.example.tfc.vista.actividades.listados;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.tfc.R;
import com.example.tfc.bbdd.dao.CampanaDAO;
import com.example.tfc.bbdd.entidades.Campana;
import com.example.tfc.bbdd.entidades.Personaje;
import com.example.tfc.vista.actividades.creacion.CrearPersonaje;
import com.example.tfc.vista.actividades.edicion.EditarCampana;
import com.example.tfc.vista.fragmentos.ListaPersonajes;

public class ListadoPersonajes extends AppCompatActivity implements ListaPersonajes.OnPersonajeSelectedListener {

    public TextView txtNombre, txtDescripcion;
    public ImageView imgCampana;
    public Button btnCrearPersonaje;
    public int idCampana;
    private Toolbar toolbar;
    public SharedPreferences sp;
    private Campana campana;

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
        sp = getSharedPreferences("datosUsuario", MODE_PRIVATE);
        String username = sp.getString("usuario", "Usuario");
        toolbar.setTitle("Campañas de " + username);
        setSupportActionBar(toolbar);

        idCampana = getIntent().getIntExtra("idCampana", -1);

        CampanaDAO cDAO = new CampanaDAO(getApplicationContext());
        campana = cDAO.obtenerCampanaPorId(idCampana);

        if (campana != null) {
            txtNombre.setText(campana.getNombreCampanha());
            txtDescripcion.setText(campana.getDescripcion());

            byte[] bytes = Base64.decode(campana.getImagenCampanha(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imgCampana.setImageBitmap(bitmap);
        }

        ListaPersonajes fragment = ListaPersonajes.newInstance(username, idCampana);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.listaPersonajeFragmentContainer, fragment)
                .commit();

        btnCrearPersonaje.setOnClickListener(v -> {
            Intent intent = new Intent(this, CrearPersonaje.class);
            intent.putExtra("idCampana", campana.getId());
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
        Intent intent = new Intent(this, ListadoInventario.class);
        intent.putExtra("personajeID", personaje.getIdPersonaje());
        startActivity(intent);
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