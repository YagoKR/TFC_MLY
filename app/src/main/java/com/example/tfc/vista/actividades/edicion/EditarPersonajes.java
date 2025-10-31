package com.example.tfc.vista.actividades.edicion;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
        int idCampana = getIntent().getIntExtra("idCampana", -1);
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

        nombreEditarPersonaje.addTextChangedListener(new TextWatcher() {
            private static final int MAX_CHARACTERS = 15;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > MAX_CHARACTERS) {
                    nombreEditarPersonaje.setText(s.subSequence(0, MAX_CHARACTERS));
                    nombreEditarPersonaje.setSelection(MAX_CHARACTERS);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        statsEditarPersonaje.addTextChangedListener(new TextWatcher() {
            private static final int MAX_CHARACTERS = 60;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > MAX_CHARACTERS) {
                    statsEditarPersonaje.setText(s.subSequence(0, MAX_CHARACTERS));
                    statsEditarPersonaje.setSelection(MAX_CHARACTERS);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        razaEditarPersonaje.addTextChangedListener(new TextWatcher() {
            private static final int MAX_CHARACTERS = 20;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > MAX_CHARACTERS) {
                    razaEditarPersonaje.setText(s.subSequence(0, MAX_CHARACTERS));
                    razaEditarPersonaje.setSelection(MAX_CHARACTERS);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        btnEditarPersonaje.setOnClickListener(v -> {

            if (personajeDAO.existePersonajeConNombreEnCampana(
                    nombreEditarPersonaje.getText().toString(),
                    idCampana,
                    idPersonaje)) {

                new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Ya existe un personaje con ese nombre en esta campaña")
                        .setPositiveButton("Ok", null)
                        .show();
                return;
            }

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
        if (bitmap == null) return null;

        Bitmap resized = resizeAndCropBitmap(bitmap, 256, 256);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        resized.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

        byte[] byteArray = outputStream.toByteArray();

        return android.util.Base64.encodeToString(byteArray, Base64.DEFAULT);
    }


    private Bitmap resizeAndCropBitmap(Bitmap original, int targetWidth, int targetHeight) {
        if (original == null) {
            throw new IllegalArgumentException("Bitmap no puede ser nulo");
        }

        if (targetWidth <= 0 || targetHeight <= 0) {
            throw new IllegalArgumentException("Las dimensiones objetivo deben ser mayores que 0");
        }

        int width = original.getWidth();
        int height = original.getHeight();

        float scale = Math.max((float) targetWidth / width, (float) targetHeight / height);

        int scaledWidth = Math.round(scale * width);
        int scaledHeight = Math.round(scale * height);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(original, scaledWidth, scaledHeight, true);

        int offsetX = (scaledWidth - targetWidth) / 2;
        int offsetY = (scaledHeight - targetHeight) / 2;

        Bitmap output = Bitmap.createBitmap(scaledBitmap, offsetX, offsetY, targetWidth, targetHeight);

        return output;
    }
}