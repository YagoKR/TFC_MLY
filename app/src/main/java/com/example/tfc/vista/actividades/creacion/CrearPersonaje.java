package com.example.tfc.vista.actividades.creacion;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tfc.R;
import com.example.tfc.bbdd.dao.PersonajeDAO;
import com.example.tfc.bbdd.entidades.Personaje;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class CrearPersonaje extends AppCompatActivity {

    public EditText txtNombrePJ, txtRazaPJ, txtStatsPJ;
    public ImageView imgPersonaje;
    public Button btnAnadirPJ;
    public int idCampana;
    public String username;
    private Uri selectedImageUri;
    private PersonajeDAO pDAO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crear_personaje);

        txtNombrePJ = findViewById(R.id.nombrePersonaje);
        txtRazaPJ = findViewById(R.id.razaPersonaje);
        txtStatsPJ = findViewById(R.id.statsPersonaje);
        imgPersonaje = findViewById(R.id.imageViewPersonaje);
        btnAnadirPJ = findViewById(R.id.btnCrearPersonaje);

        idCampana = getIntent().getIntExtra("idCampana", -1);

        SharedPreferences sp = getSharedPreferences("datosUsuario", MODE_PRIVATE);
        username = sp.getString("usuario", "Usuario");

        pDAO = new PersonajeDAO(getApplicationContext());

        imgPersonaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 123);
            }
        });

        btnAnadirPJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtNombrePJ.getText().toString().isEmpty() || txtRazaPJ.getText().toString().isEmpty() || txtStatsPJ.getText().toString().isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CrearPersonaje.this);
                    builder.setMessage("Hay campos vacíos").setTitle("Error").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }

                String imagenBase64 = null;
                String nombre = txtNombrePJ.getText().toString();
                String raza = txtRazaPJ.getText().toString();
                String stats = txtStatsPJ.getText().toString();


                if (pDAO.existePersonaje(nombre, idCampana, username)) {
                    new AlertDialog.Builder(CrearPersonaje.this)
                            .setTitle("Error")
                            .setMessage("Ya existe este personaje para este usuario")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }

                if (selectedImageUri != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                        Bitmap resizedBitmap = resizeAndCropBitmap(bitmap, 128, 128);
                        imagenBase64 = bitmapToBase64(resizedBitmap);
                        Personaje p = new Personaje(nombre, raza, stats, imagenBase64, idCampana , username);
                        pDAO.insertarDatos(p);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.character_generic);
                    Bitmap resizedBitmap = resizeAndCropBitmap(defaultBitmap, 128, 128);
                    imagenBase64 = bitmapToBase64(resizedBitmap);
                    Personaje p = new Personaje(nombre, raza, stats, imagenBase64, idCampana , username);
                    pDAO.insertarDatos(p);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(CrearPersonaje.this);
                builder.setMessage("Personaje creado con éxito").setTitle("Éxito").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imgPersonaje.setImageURI(selectedImageUri);
        }
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        return Base64.getEncoder().encodeToString(byteArray);
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