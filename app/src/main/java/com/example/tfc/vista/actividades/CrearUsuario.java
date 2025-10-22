package com.example.tfc.vista.actividades;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tfc.R;

public class CrearUsuario extends AppCompatActivity {

    public Button btnCrearU;
    public EditText txtContrasenha, txtNombreReal, txtNombreUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crear_usuario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnCrearU = findViewById(R.id.btnCrearUsuario);
        txtContrasenha = findViewById(R.id.contrasena);
        txtNombreReal = findViewById(R.id.nomeReal);
        txtNombreUsuario = findViewById(R.id.nomeUsuario);

        AlertDialog.Builder builder = new AlertDialog.Builder(CrearUsuario.this);
        builder.setMessage("Hay campos vacíos").setTitle("Error").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();

        btnCrearU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtContrasenha.getText().toString().isEmpty() || txtNombreReal.getText().toString().isEmpty() || txtNombreUsuario.getText().toString().isEmpty()) {
                    dialog.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CrearUsuario.this);
                    builder.setMessage("Usuario creado con éxito").setTitle("Éxito").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }
}