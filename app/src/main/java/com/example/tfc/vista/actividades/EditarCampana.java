package com.example.tfc.vista.actividades;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tfc.R;
import com.example.tfc.bbdd.dao.CampanaDAO;
import com.example.tfc.bbdd.entidades.Campana;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditarCampana extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText editNombreCampana, editDescripcionCampana;
    private ImageView imageViewEditar;
    private Button btnEditarCampana;
    private CampanaDAO campanaDAO;
    private Campana campana;

    private Uri selectedImageUri;
    private String imagenBase64Actual;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_campana);


        toolbar = findViewById(R.id.toolbar2);
        editNombreCampana = findViewById(R.id.editnombreCampana);
        editDescripcionCampana = findViewById(R.id.editdescripcionCampana);
        imageViewEditar = findViewById(R.id.imageVieweditarCamp);
        btnEditarCampana = findViewById(R.id.btneditarCampana);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Editar Campaña");
        int idCampana = getIntent().getIntExtra("id", -1);

        campanaDAO = new CampanaDAO(getApplicationContext());

        campana = campanaDAO.obtenerCampanaPorId(idCampana);
        if (campana != null) {
            actualizarDatosCampana();
        }

        imageViewEditar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 123);
        });

        btnEditarCampana.setOnClickListener(v -> {
            campana.setNombreCampanha(editNombreCampana.getText().toString());
            campana.setDescripcion(editDescripcionCampana.getText().toString());

            if (selectedImageUri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    String nuevaImagenBase64 = bitmapToBase64(bitmap);
                    campana.setImagenCampanha(nuevaImagenBase64);
                } catch (IOException e) {
                    e.printStackTrace();
                    campana.setImagenCampanha(imagenBase64Actual);
                }
            } else {
                campana.setImagenCampanha(imagenBase64Actual);
            }

            campanaDAO.actualizarCampana(campana, idCampana);

            AlertDialog.Builder builder = new AlertDialog.Builder(EditarCampana.this);
            builder.setMessage("Campaña modificada con éxito")
                    .setTitle("Éxito")
                    .setPositiveButton("Ok", (dialogInterface, i) -> {
                        setResult(RESULT_OK);
                        finish();
                    });
            builder.create().show();
        });
    }


    private void actualizarDatosCampana() {
        if (campana != null) {
            editNombreCampana.setText(campana.getNombreCampanha());
            editDescripcionCampana.setText(campana.getDescripcion());
            imagenBase64Actual = campana.getImagenCampanha();

            try {
                if (imagenBase64Actual != null && !imagenBase64Actual.isEmpty()) {
                    byte[] bytes = Base64.decode(imagenBase64Actual, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Bitmap resized = resizeAndCropBitmap(bitmap, 128, 128);
                    imageViewEditar.setImageBitmap(resized);
                }
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
            imageViewEditar.setImageURI(selectedImageUri);
        }
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        return java.util.Base64.getEncoder().encodeToString(byteArray);
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