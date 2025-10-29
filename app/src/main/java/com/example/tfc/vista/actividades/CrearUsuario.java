package com.example.tfc.vista.actividades;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
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
import com.example.tfc.bbdd.dao.UsuarioDAO;
import com.example.tfc.bbdd.entidades.Usuario;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class CrearUsuario extends AppCompatActivity {

    public Button btnCrearU;
    public EditText txtContrasenha, txtNombreReal, txtNombreUsuario, txtEmailUsuario;
    public ImageView imgUsuario;
    private Uri selectedImageUri;

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
        txtEmailUsuario = findViewById(R.id.emailReal);
        imgUsuario = findViewById(R.id.imageView);

        AlertDialog.Builder builder = new AlertDialog.Builder(CrearUsuario.this);
        builder.setMessage("Hay campos vacíos").setTitle("Error").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();

        imgUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 123);
            }
        });


        btnCrearU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtContrasenha.getText().toString().isEmpty() || txtNombreReal.getText().toString().isEmpty() || txtNombreUsuario.getText().toString().isEmpty()) {
                    dialog.show();
                    return;
                }
                    String imagenBase64 = null;
                    String usuario, nome, email, contrasena, contrasenaHasheada;

                    usuario = txtNombreUsuario.getText().toString();
                    nome = txtNombreReal.getText().toString();
                    contrasena = txtContrasenha.getText().toString();
                    email = txtEmailUsuario.getText().toString();

                if (contrasena.length() < 6 || contrasena.length() > 12) {
                    new AlertDialog.Builder(CrearUsuario.this)
                            .setTitle("Error")
                            .setMessage("La contraseña debe tener entre 6 y 12 caracteres")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }

                contrasenaHasheada = hashPassword(contrasena);

                UsuarioDAO uDAO = new UsuarioDAO(getApplicationContext());
                if (uDAO.existeUsuario(usuario, email)) {
                    new AlertDialog.Builder(CrearUsuario.this)
                            .setTitle("Error")
                            .setMessage("El nombre de usuario ya existe o el correo ya está siendo utilizado. Elige otro.")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }
                if (selectedImageUri != null) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                            Bitmap resized = resizeAndCropBitmap(bitmap, 128, 128);
                            imagenBase64 = bitmapToBase64(resized);
                            Usuario u = new Usuario(usuario, nome, email, contrasenaHasheada, imagenBase64);
                            uDAO.insertarDatos(u);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.woman_avatar_proof);
                        imagenBase64 = bitmapToBase64(defaultBitmap);
                        Usuario u = new Usuario(usuario, nome, email, contrasenaHasheada, imagenBase64);
                        uDAO.insertarDatos(u);

                    }
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
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imgUsuario.setImageURI(selectedImageUri);
        }
    }
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        return Base64.getEncoder().encodeToString(byteArray);
    }


    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
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