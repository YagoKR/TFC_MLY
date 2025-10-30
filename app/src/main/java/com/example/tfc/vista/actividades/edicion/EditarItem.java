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
import com.example.tfc.bbdd.dao.InventarioDAO;
import com.example.tfc.bbdd.entidades.Inventario;

import java.io.ByteArrayOutputStream;

public class EditarItem extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView imagenItem;
    private EditText nomeEditItem, cantidadEditItem, descripcionEditItem;
    private Button btnEditarItem;
    private Uri selectedImageUri;
    private String imagenBase64Actual;
    private Inventario itemActual;
    private InventarioDAO inventarioDAO;

    private int idPersonaje;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_item);

        toolbar = findViewById(R.id.toolbarEditarItem);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Editar Ítem");

        imagenItem = findViewById(R.id.imagenEditItem);
        nomeEditItem = findViewById(R.id.nomeEditItem);
        cantidadEditItem = findViewById(R.id.cantidadItem);
        descripcionEditItem = findViewById(R.id.descripcionItem);
        btnEditarItem = findViewById(R.id.btnEditarItem);

        long idItem = getIntent().getLongExtra("idItem", -1);
        inventarioDAO = new InventarioDAO(this);
        itemActual = inventarioDAO.obtenerItemPorId(idItem);

        if (itemActual != null) {
            idPersonaje = itemActual.getIdPersonaje();
            nomeEditItem.setText(itemActual.getProducto());
            cantidadEditItem.setText(String.valueOf(itemActual.getCantidad()));
            descripcionEditItem.setText(itemActual.getDescripcion());
            imagenBase64Actual = itemActual.getImagenItem();

            if (imagenBase64Actual != null && !imagenBase64Actual.isEmpty()) {
                byte[] bytes = Base64.decode(imagenBase64Actual, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imagenItem.setImageBitmap(bitmap);
            }
        }


        imagenItem.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 123);
        });

        descripcionEditItem.addTextChangedListener(new TextWatcher() {
            private static final int MAX_CHARACTERS = 30;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > MAX_CHARACTERS) {
                    descripcionEditItem.setText(s.subSequence(0, MAX_CHARACTERS));
                    descripcionEditItem.setSelection(MAX_CHARACTERS);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });


        btnEditarItem.setOnClickListener(v -> {
            String cantidadTexto = cantidadEditItem.getText().toString().trim();
            String descripcion = descripcionEditItem.getText().toString().trim();

            if (cantidadTexto.isEmpty()) {
                mostrarDialogo("Error", "La cantidad no puede estar vacía");
                return;
            }

            int cantidad;
            try {
                cantidad = Integer.parseInt(cantidadTexto);
            } catch (NumberFormatException e) {
                mostrarDialogo("Error", "Cantidad inválida");
                return;
            }

            if (cantidad > 999) {
                mostrarDialogo("Error", "La cantidad máxima permitida es 999");
                return;
            }

            itemActual.setCantidad(cantidad);
            itemActual.setDescripcion(descripcion);

            try {
                Bitmap bitmap;
                if (selectedImageUri != null) {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    Bitmap resized = resizeAndCropBitmap(bitmap, 128, 128);
                    itemActual.setImagenItem(bitmapToBase64(resized));
                } else {
                    itemActual.setImagenItem(imagenBase64Actual);
                }
            } catch (Exception e) {
                mostrarDialogo("Error", "Error al procesar la imagen");
                return;
            }

            int filas = inventarioDAO.actualizarItem(itemActual);
            if (filas > 0) {
                mostrarDialogo("Éxito", "Item actualizado correctamente", true);
            } else {
                mostrarDialogo("Error", "No se pudo actualizar el item");
            }
        });
    }

    private void mostrarDialogo(String titulo, String mensaje) {
        mostrarDialogo(titulo, mensaje, false);
    }

    private void mostrarDialogo(String titulo, String mensaje, boolean cerrarDespues) {
        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Ok", (dialog, which) -> {
                    if (cerrarDespues) finish();
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imagenItem.setImageURI(selectedImageUri);
        }
    }
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        return java.util.Base64.getEncoder().encodeToString(byteArray);
    }

    private Bitmap resizeAndCropBitmap(Bitmap original, int targetWidth, int targetHeight) {
        if (original == null) throw new IllegalArgumentException("Bitmap no puede ser nulo");
        if (targetWidth <= 0 || targetHeight <= 0)
            throw new IllegalArgumentException("Las dimensiones objetivo deben ser mayores que 0");

        int width = original.getWidth();
        int height = original.getHeight();
        float scale = Math.max((float) targetWidth / width, (float) targetHeight / height);

        int scaledWidth = Math.round(scale * width);
        int scaledHeight = Math.round(scale * height);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(original, scaledWidth, scaledHeight, true);

        int offsetX = (scaledWidth - targetWidth) / 2;
        int offsetY = (scaledHeight - targetHeight) / 2;
        return Bitmap.createBitmap(scaledBitmap, offsetX, offsetY, targetWidth, targetHeight);
    }

}
