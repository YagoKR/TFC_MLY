package com.example.tfc.vista.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tfc.R;
import com.example.tfc.bbdd.dao.UsuarioDAO;

public class InicioSesion extends AppCompatActivity {

    public Button btnInicio;
    public EditText txtUsuario;
    public EditText txtContrasena;
    public SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio_sesion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnInicio = findViewById(R.id.btnIniciaUsuario);
        txtUsuario = findViewById(R.id.editUsuarioInicio);
        txtContrasena = findViewById(R.id.editContra);

        btnInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usuario = txtUsuario.getText().toString().trim();
                String contrasena = txtContrasena.getText().toString().trim();

                if (usuario.isEmpty() || contrasena.isEmpty()) {
                    new AlertDialog.Builder(InicioSesion.this)
                            .setTitle("Error")
                            .setMessage("Completa todos los campos")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }

                UsuarioDAO uDAO = new UsuarioDAO(getApplicationContext());

                if (uDAO.validarUsuario(usuario, contrasena)) {
                    SharedPreferences sp = getSharedPreferences("datosUsuario", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("usuario", usuario);
                    editor.apply();

                    Intent i = new Intent(InicioSesion.this, ListadoCampanas.class);
                    startActivity(i);
                    finish();

                } else {
                    new AlertDialog.Builder(InicioSesion.this)
                            .setTitle("Error")
                            .setMessage("Usuario o contraseña incorrectos")
                            .setPositiveButton("Ok", null)
                            .show();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        txtUsuario.setText("");
        txtContrasena.setText("");
    }


}