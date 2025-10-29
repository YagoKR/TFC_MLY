package com.example.tfc.vista.actividades;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tfc.R;
import com.example.tfc.bbdd.dao.UsuarioDAO;
import com.example.tfc.bbdd.entidades.Usuario;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EditarUsuario extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText editNombreUsuario, editNombreReal, editmail, editcontrasena;
    private ImageView imageView;
    private Button btnEditarUsuario, btncambiarContrasena;
    private UsuarioDAO usuarioDAO;
    private SharedPreferences sp;
    private Usuario usuario;

    private Uri selectedImageUri;
    private String imagenBase64Actual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_usuario);

        toolbar = findViewById(R.id.toolbar2);
        editNombreUsuario = findViewById(R.id.editnombreUsuario);
        editNombreReal = findViewById(R.id.editNombreReal);
        editmail = findViewById(R.id.editmail);
        editcontrasena = findViewById(R.id.editcontrasena);
        imageView = findViewById(R.id.imageView);
        btnEditarUsuario = findViewById(R.id.btnEditarUsuario);
        btncambiarContrasena = findViewById(R.id.btnCambiarContrasena);

        setSupportActionBar(toolbar);
        toolbar.setTitle("Editar perfil");

        usuarioDAO = new UsuarioDAO(getApplicationContext());
        sp = getSharedPreferences("datosUsuario", MODE_PRIVATE);
        String username = sp.getString("usuario", null);

        if (username != null) {
            usuario = usuarioDAO.obtenerUsuario(username);
            if (usuario != null) {
                editNombreUsuario.setText(usuario.getIdUsuario());
                editNombreReal.setText(usuario.getNombre());
                editmail.setText(usuario.getEmail());
                editcontrasena.setText(usuario.getContrasenha());
                imagenBase64Actual = usuario.getImagen();

                try {
                    byte[] bytes = Base64.decode(imagenBase64Actual, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 123);
            }
        });

        btncambiarContrasena.setOnClickListener(v -> {
            editcontrasena.setEnabled(true);
            editcontrasena.setText("");
            editcontrasena.requestFocus();
        });

        btnEditarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                usuario.setNombre(editNombreReal.getText().toString());
                usuario.setEmail(editmail.getText().toString());

                if (editcontrasena.isEnabled()) {
                    String nuevaContrasena = editcontrasena.getText().toString().trim();
                    if (!nuevaContrasena.isEmpty()) {
                        if (nuevaContrasena.length() < 6 || nuevaContrasena.length() > 12) {
                            new AlertDialog.Builder(EditarUsuario.this)
                                    .setTitle("Error")
                                    .setMessage("La contraseña debe tener entre 6 y 12 caracteres")
                                    .setPositiveButton("Ok", null)
                                    .show();
                            return;
                        }
                        usuario.setContrasenha(hashPassword(nuevaContrasena));
                    }
                }

                if (selectedImageUri != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                        Bitmap resized = resizeAndCropBitmap(bitmap, 128, 128);
                        usuario.setImagen(bitmapToBase64(resized));
                    } catch (IOException e) {
                        e.printStackTrace();
                        usuario.setImagen(imagenBase64Actual);
                    }
                } else {
                    usuario.setImagen(imagenBase64Actual);
                }

                usuarioDAO.actualizarUsuario(usuario);

                new AlertDialog.Builder(EditarUsuario.this)
                        .setMessage("Usuario modificado con éxito")
                        .setTitle("Éxito")
                        .setPositiveButton("Ok", (dialogInterface, i) -> {
                            setResult(RESULT_OK);
                            finish();
                        }).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imageView.setImageURI(selectedImageUri);
        }
    }
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        return java.util.Base64.getEncoder().encodeToString(byteArray);
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