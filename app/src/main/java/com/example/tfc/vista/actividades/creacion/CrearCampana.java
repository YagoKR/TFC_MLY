package com.example.tfc.vista.actividades.creacion;

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
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tfc.R;
import com.example.tfc.bbdd.dao.CampanaDAO;
import com.example.tfc.bbdd.dao.UsuarioCampanasDAO;
import com.example.tfc.bbdd.entidades.Campana;
import com.example.tfc.bbdd.entidades.UsuariosCampanas;

import java.io.ByteArrayOutputStream;

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

        txtNombreCampana.addTextChangedListener(new TextWatcher() {
            private static final int MAX_CHARACTERS = 30;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > MAX_CHARACTERS) {
                    txtNombreCampana.setText(s.subSequence(0, MAX_CHARACTERS));
                    txtNombreCampana.setSelection(MAX_CHARACTERS);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });


        txtDescripcionCampana.addTextChangedListener(new TextWatcher() {
            private static final int MAX_CHARACTERS = 100;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > MAX_CHARACTERS) {
                    txtDescripcionCampana.setText(s.subSequence(0, MAX_CHARACTERS));
                    txtDescripcionCampana.setSelection(MAX_CHARACTERS);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
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
                        imagenBase64 = bitmapToBase64(bitmap);
                        Campana c = new Campana(campana, descripcion, imagenBase64);
                        long idcampana = cDAO.insertarDatos(c);
                        c.setId((int) idcampana);
                        UsuariosCampanas uc = new UsuariosCampanas(username, (int) idcampana);
                        ucDAO.insertarDatos(uc);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dragon_generic);
                    imagenBase64 = bitmapToBase64(defaultBitmap);
                    Campana c = new Campana(campana, descripcion, imagenBase64);
                    long idcampana = cDAO.insertarDatos(c);
                    c.setId((int) idcampana);
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