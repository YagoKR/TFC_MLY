package com.example.tfc.vista.actividades;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tfc.R;
import com.example.tfc.bbdd.dao.InventarioDAO;
import com.example.tfc.bbdd.entidades.Inventario;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class CrearItem extends AppCompatActivity {

    private static final String API_URL = "https://www.dnd5eapi.co/api/equipment/";
    private RequestQueue requestQueue;
    private EditText nomeItem, cantiItem, descripcionItem;
    private ImageView imagenItem;
    private Button btnCrearItem;
    private Uri selectedImageUri;
    private int idPersonaje;

    private String descripcionAPI = "Sin descripción";
    private String categoryName = "Común";
    private int precioCantidad = 0;
    private String precioUnidad = "gp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crear_item);

        idPersonaje = getIntent().getIntExtra("idPersonaj", -1);

        nomeItem = findViewById(R.id.nomeItem);
        cantiItem = findViewById(R.id.cantidadItem);
        descripcionItem = findViewById(R.id.descripcionItem);

        imagenItem = findViewById(R.id.imagenItem);

        btnCrearItem = findViewById(R.id.btnCrearItem);

        requestQueue = Volley.newRequestQueue(this);

        imagenItem.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 123);
        });

        nomeItem.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String item = nomeItem.getText().toString().trim().toLowerCase().replace(" ", "-");
                if (!item.isEmpty()) checkApiConnection(item);
            }
        });

        btnCrearItem.setOnClickListener(v -> guardarItem());
    }

    private void checkApiConnection(String texto) {
        String nuevaURL = API_URL + texto;
        StringRequest request = new StringRequest(Request.Method.GET, nuevaURL,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);

                        JSONObject equipmentCategory = jsonResponse.getJSONObject("equipment_category");
                        categoryName = equipmentCategory.getString("name");

                        JSONObject cost = jsonResponse.getJSONObject("cost");
                        precioCantidad = cost.getInt("quantity");
                        precioUnidad = cost.getString("unit");

                        Log.d("API Response", response);

                    } catch (JSONException e) {
                        Log.e("JSON Error", "Error al parsear la respuesta: " + e.getMessage());
                    }
                },
                error -> Log.e("API Error", "Error en la conexión: " + error.toString())
        );

        requestQueue.add(request);
    }

    private void guardarItem() {
        String nombre = nomeItem.getText().toString().trim();
        String cantidadTexto = cantiItem.getText().toString().trim();
        String descripcionUsuario = descripcionItem.getText().toString().trim();

        if (nombre.isEmpty() || cantidadTexto.isEmpty()) {
            mostrarDialogo("Error", "Hay campos vacíos");
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadTexto);
        } catch (NumberFormatException e) {
            mostrarDialogo("Error", "La cantidad debe ser un número válido");
            return;
        }

        if (cantidad > 999) {
            mostrarDialogo("Error", "La cantidad máxima permitida es 999");
            return;
        }
        InventarioDAO invDAO = new InventarioDAO(getApplicationContext());
        if (invDAO.existeItem(idPersonaje, nombre)) {
            mostrarDialogo("Error", "Ya existe un ítem con ese nombre para este personaje");
            return;
        }

        if (!descripcionUsuario.isEmpty()) {
            descripcionAPI = descripcionUsuario;
        }

        String imagenBase64;
        try {
            Bitmap bitmap;
            if (selectedImageUri != null) {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
            } else {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.woman_avatar_proof);
            }
            Bitmap resized = resizeAndCropBitmap(bitmap, 128, 128);
            imagenBase64 = bitmapToBase64(resized);
        } catch (Exception e) {
            mostrarDialogo("Error", "Error al procesar la imagen");
            return;
        }

        invDAO = new InventarioDAO(getApplicationContext());
        Inventario item = new Inventario(
                idPersonaje,
                nombre,
                categoryName,
                cantidad,
                precioCantidad,
                precioUnidad,
                descripcionAPI,
                imagenBase64
        );

        long id = invDAO.insertarItem(item);
        if (id > 0) {
            mostrarDialogo("Éxito", "Ítem creado con éxito", true);
        } else {
            mostrarDialogo("Error", "No se pudo crear el ítem");
        }
    }

    private void mostrarDialogo(String titulo, String mensaje) {
        mostrarDialogo(titulo, mensaje, false);
    }

    private void mostrarDialogo(String titulo, String mensaje, boolean cerrarDespues) {
        new AlertDialog.Builder(CrearItem.this)
                .setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Ok", (dialog, which) -> {
                    if (cerrarDespues) finish();
                })
                .show();
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
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
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
