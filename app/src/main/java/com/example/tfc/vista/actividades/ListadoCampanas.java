package com.example.tfc.vista.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tfc.R;
import com.example.tfc.bbdd.dao.UsuarioDAO;
import com.example.tfc.bbdd.entidades.Campana;
import com.example.tfc.bbdd.entidades.Usuario;
import com.example.tfc.vista.fragmentos.ListaCampana;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ListadoCampanas extends AppCompatActivity implements ListaCampana.OnCampanaSelectedListener{

    public Toolbar toolbar;
    public SharedPreferences sp;
    public UsuarioDAO usuarioDAO;
    public TextView txtNombre, txtEmail;
    public ImageView imgUsuario;
    public Button btnanadirCampana;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listado_campanas);

        toolbar = findViewById(R.id.toolbarListadoCampanas);
        txtNombre = findViewById(R.id.NombreUsuario);
        txtEmail = findViewById(R.id.correoUsuario);
        imgUsuario = findViewById(R.id.imageView2);
        btnanadirCampana = findViewById(R.id.anadirCampana);

        usuarioDAO = new UsuarioDAO(getApplicationContext());

        sp = getSharedPreferences("datosUsuario", MODE_PRIVATE);
        String username = sp.getString("usuario", "Usuario");
        toolbar.setTitle("Campañas de " + username);
        setSupportActionBar(toolbar);

        if (username != null) {
            UsuarioDAO uDAO = new UsuarioDAO(getApplicationContext());
            Usuario usuario = uDAO.obtenerUsuario(username);

            if (usuario != null) {
                txtNombre.setText(usuario.getIdUsuario());
                txtEmail.setText(usuario.getEmail());

                byte[] bytes = android.util.Base64.decode(usuario.getImagen(), android.util.Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imgUsuario.setImageBitmap(bitmap);
            }
        }

        btnanadirCampana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ListadoCampanas.this, CrearCampana.class);
                startActivity(i);
            }
        });

    }
    private void cerrarSesion() {
        SharedPreferences sp = getSharedPreferences("datosUsuario", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(ListadoCampanas.this, InicioSesion.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.editarPerfil) {
            Intent intent = new Intent(ListadoCampanas.this, EditarUsuario.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.cerrarSesion) {
            cerrarSesion();
            return true;
        }
        if (id == R.id.salir) {
            finishAffinity();
            System.exit(0);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCampanaSelected(Campana campana) {
        Intent intent = new Intent(this, ListadoPersonajes.class);
        intent.putExtra("idCampana", campana.getId());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarDatosUsuario();
    }

    private void actualizarDatosUsuario() {
        sp = getSharedPreferences("datosUsuario", MODE_PRIVATE);
        String username = sp.getString("usuario", null);

        if (username != null) {
            Usuario usuario = usuarioDAO.obtenerUsuario(username);

            if (usuario != null) {
                txtNombre.setText(usuario.getIdUsuario());
                txtEmail.setText(usuario.getEmail());

                try {
                    byte[] bytes = android.util.Base64.decode(usuario.getImagen(), android.util.Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imgUsuario.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}