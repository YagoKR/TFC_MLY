package com.example.tfc.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tfc.R;
import com.example.tfc.bbdd.definicion.SQLiteHelper;

public class MainActivity extends AppCompatActivity {
    public Button btninicioSesion,btnCrearUsuario;
    public SQLiteHelper sqHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btninicioSesion = (Button) findViewById(R.id.btnIniciar);
        btnCrearUsuario = (Button) findViewById(R.id.btnCrear);
        sqHelper = new SQLiteHelper(getApplicationContext());

        sqHelper.getWritableDatabase();

        btnCrearUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, CrearUsuario.class);
                startActivity(i);
            }
        });

        btninicioSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, InicioSesion.class);
                startActivity(i);
            }
        });
    }
}