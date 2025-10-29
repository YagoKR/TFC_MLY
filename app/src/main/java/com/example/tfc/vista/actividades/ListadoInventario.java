package com.example.tfc.vista.actividades;

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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.tfc.R;
import com.example.tfc.bbdd.dao.CampanaDAO;
import com.example.tfc.bbdd.dao.PersonajeDAO;
import com.example.tfc.bbdd.entidades.Campana;
import com.example.tfc.bbdd.entidades.Inventario;
import com.example.tfc.bbdd.entidades.Personaje;
import com.example.tfc.vista.fragmentos.ListaInventario;
import com.example.tfc.vista.fragmentos.ListaPersonajes;

public class ListadoInventario extends AppCompatActivity implements ListaInventario.OnInventarioSelectedListener {

    public TextView txtNombrePersonaje, txtClasePersonaje;
    public ImageView imgPersonaje;
    public Button btnAnadirItem;
    private Toolbar toolbar;
    public SharedPreferences sp;
    private Personaje personaje;
    public int idPersonaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listado_inventario);


        txtNombrePersonaje = findViewById(R.id.NombrePersonaje);
        txtClasePersonaje = findViewById(R.id.NivelesPersonaje);
        imgPersonaje = findViewById(R.id.imageView4);
        btnAnadirItem = findViewById(R.id.anadirInventario);
        toolbar = findViewById(R.id.toolbarListadoInventario);
        sp = getSharedPreferences("datosUsuario", MODE_PRIVATE);
        String username = sp.getString("usuario", "Usuario");
        toolbar.setTitle("Campañas de " + username);
        setSupportActionBar(toolbar);

        idPersonaje = getIntent().getIntExtra("personajeID", -1);
        PersonajeDAO pDAO = new PersonajeDAO(getApplicationContext());
        personaje = pDAO.obtenerPersonajePorId(idPersonaje);

        if (personaje != null){
            txtNombrePersonaje.setText(personaje.getNombre()+ "(" + personaje.getRaza() + ")");
            txtClasePersonaje.setText(personaje.getClase());

            byte[] bytes = Base64.decode(personaje.getImagenPJ(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imgPersonaje.setImageBitmap(bitmap);
        }

        ListaInventario fragment = ListaInventario.newInstance(idPersonaje);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.listaInventarioFragmentContainer, fragment)
                .commit();

        btnAnadirItem.setOnClickListener(v -> {
            Intent intent = new Intent(this, CrearItem.class);
            intent.putExtra("idPersonaj", idPersonaje);
            startActivity(intent);
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item_03, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.editarPersonaje) {
            Intent intent = new Intent(ListadoInventario.this, EditarPersonajes.class);
            intent.putExtra("idPersona",idPersonaje);
            startActivity(intent);
            return true;
        }

        if (id == R.id.salirPersonaje) {
            finishAffinity();
            System.exit(0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void actualizarDatosPersonaje() {
        PersonajeDAO pDAO = new PersonajeDAO(getApplicationContext());
        Personaje personajeActualizado = pDAO.obtenerPersonajePorId(idPersonaje);

        if (personajeActualizado != null) {
            txtNombrePersonaje.setText(personajeActualizado.getNombre()+ "(" + personajeActualizado.getRaza() + ")");
            txtClasePersonaje.setText(personajeActualizado.getClase());

            try {
                if (personajeActualizado.getImagenPJ() != null && !personajeActualizado.getImagenPJ().isEmpty()) {
                    byte[] bytes = android.util.Base64.decode(personajeActualizado.getImagenPJ(), android.util.Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imgPersonaje.setImageBitmap(bitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarDatosPersonaje();
    }

    @Override
    public void OnInventarioSelectedListener(Inventario inventario) {
        Intent intent = new Intent(this, EditarItem.class);
        intent.putExtra("idItem", inventario.getId());
        startActivity(intent);
    }
}