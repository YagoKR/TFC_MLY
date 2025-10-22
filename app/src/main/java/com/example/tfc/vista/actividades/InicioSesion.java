package com.example.tfc.vista.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tfc.R;

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
                String usuario = txtUsuario.getText().toString();
                sp = getSharedPreferences("datosUsuario", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("usuario", usuario);
                editor.apply();

                Intent i = new Intent(InicioSesion.this, ListadoCampanas.class);
                startActivity(i);
            }
        });

    }
}