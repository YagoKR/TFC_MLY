package com.example.tfc.vista.actividades;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tfc.R;
import com.example.tfc.bbdd.dao.PersonajeDAO;
import com.example.tfc.bbdd.entidades.Personaje;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditarPersonajes extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView imageViewEditarPersonaje;
    private EditText nombreEditarPersonaje, razaEditarPersonaje, statsEditarPersonaje;
    private Button btnEditarPersonaje;
    private PersonajeDAO personajeDAO;
    private Personaje personaje;

    private Uri selectedImageUri;
    private String imagenBase64Actual;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_personajes);
        toolbar = findViewById(R.id.toolbarEditarPersonaje);
        imageViewEditarPersonaje = findViewById(R.id.imageViewEditarPersonaje);
        nombreEditarPersonaje = findViewById(R.id.nombreEditarPersonaje);
        razaEditarPersonaje = findViewById(R.id.razaEditarPersonaje);
        statsEditarPersonaje = findViewById(R.id.statsEditarPersonaje);
        btnEditarPersonaje = findViewById(R.id.btnEditarPersonaje);

        setSupportActionBar(toolbar);
        toolbar.setTitle("Editar Personaje");

        int idPersonaje = getIntent().getIntExtra("idPersona", -1);

        personajeDAO = new PersonajeDAO(getApplicationContext());

        personaje = personajeDAO.obtenerPersonajePorId(idPersonaje);
        if (personaje != null) {
            actualizarDatosPersonaje();
        }

        imageViewEditarPersonaje.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 123);
        });

        btnEditarPersonaje.setOnClickListener(v -> {
            personaje.setNombre(nombreEditarPersonaje.getText().toString());
            personaje.setRaza(razaEditarPersonaje.getText().toString());
            personaje.setClase(statsEditarPersonaje.getText().toString());

            if (selectedImageUri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    String nuevaImagenBase64 = bitmapToBase64(bitmap);
                    personaje.setImagenPJ(nuevaImagenBase64);
                } catch (IOException e) {
                    e.printStackTrace();
                    personaje.setImagenPJ(imagenBase64Actual);
                }
            } else {
                personaje.setImagenPJ(imagenBase64Actual);
            }

            personajeDAO.actualizarPersonaje(personaje);

            AlertDialog.Builder builder = new AlertDialog.Builder(EditarPersonajes.this);
            builder.setMessage("Personaje modificado con éxito")
                    .setTitle("Éxito")
                    .setPositiveButton("Ok", (dialogInterface, i) -> {
                        setResult(RESULT_OK);
                        finish();
                    });
            builder.create().show();
        });
    }

    private void actualizarDatosPersonaje() {
        nombreEditarPersonaje.setText(personaje.getNombre());
        razaEditarPersonaje.setText(personaje.getRaza());
        statsEditarPersonaje.setText(personaje.getClase());
        imagenBase64Actual = personaje.getImagenPJ();

        if (imagenBase64Actual != null && !imagenBase64Actual.isEmpty()) {
            try {
                byte[] bytes = Base64.decode(imagenBase64Actual, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageViewEditarPersonaje.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imageViewEditarPersonaje.setImageURI(selectedImageUri);
        }
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        return java.util.Base64.getEncoder().encodeToString(byteArray);
    }
}