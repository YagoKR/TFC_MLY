package com.example.tfc.vista.actividades;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tfc.R;
import com.example.tfc.bbdd.dao.CampanaDAO;
import com.example.tfc.bbdd.dao.UsuarioCampanasDAO;
import com.example.tfc.bbdd.dao.UsuarioDAO;
import com.example.tfc.bbdd.entidades.Campana;
import com.example.tfc.bbdd.entidades.Usuario;
import com.example.tfc.bbdd.entidades.UsuariosCampanas;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class CrearCampana extends AppCompatActivity {
    public SharedPreferences sp;

    public EditText txtNombreCampana, txtDescripcionCampana;
    public ImageView imgCampana;
    public Button btncrearCampana;
    private Uri selectedImageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crear_campana);
        sp = getSharedPreferences("datosUsuario", MODE_PRIVATE);
        String username = sp.getString("usuario", "Usuario");

        txtNombreCampana = findViewById(R.id.nomeCampana);
        txtDescripcionCampana = findViewById(R.id.descripcionCampana);
        imgCampana = findViewById(R.id.imageView3);
        btncrearCampana = findViewById(R.id.btnCrearCampana);

        imgCampana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 123);
            }
        });


        btncrearCampana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtNombreCampana.getText().toString().isEmpty() || txtDescripcionCampana.getText().toString().isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CrearCampana.this);
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
                String campana, descripcion;

                campana = txtNombreCampana.getText().toString();
                descripcion = txtDescripcionCampana.getText().toString();

                UsuarioCampanasDAO ucDAO = new UsuarioCampanasDAO(getApplicationContext());
                CampanaDAO cDAO = new CampanaDAO(getApplicationContext());
                if (ucDAO.existeCampanaParaUsuario(username, campana)) {
                    new AlertDialog.Builder(CrearCampana.this)
                            .setTitle("Error")
                            .setMessage("Ya existe esta campaña para este usuario.")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }
                if (selectedImageUri != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                        Bitmap resizedBitmap = resizeAndCropBitmap(bitmap, 64, 64);
                        imagenBase64 = bitmapToBase64(resizedBitmap);
                        Campana u = new Campana(campana, descripcion, imagenBase64);
                        long idcampana = cDAO.insertarDatos(u);
                        UsuariosCampanas uc = new UsuariosCampanas(username, (int) idcampana);
                        ucDAO.insertarDatos(uc);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.woman_avatar_proof);
                    imagenBase64 = bitmapToBase64(defaultBitmap);
                    Campana u = new Campana(campana, descripcion, imagenBase64);
                    long idcampana = cDAO.insertarDatos(u);
                    UsuariosCampanas uc = new UsuariosCampanas(username, (int) idcampana);
                    ucDAO.insertarDatos(uc);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(CrearCampana.this);
                builder.setMessage("Campaña creada con éxito").setTitle("Éxito").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
            imgCampana.setImageURI(selectedImageUri);
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