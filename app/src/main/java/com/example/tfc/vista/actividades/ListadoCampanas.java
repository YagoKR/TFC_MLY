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
import com.example.tfc.bbdd.entidades.Usuario;

public class ListadoCampanas extends AppCompatActivity {

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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.salir) {
            finishAffinity();
            System.exit(0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}