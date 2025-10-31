package com.example.tfc.vista.actividades.creacion;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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

        txtNombreUsuario.addTextChangedListener(new TextWatcher() {
            private static final int MAX_CHARACTERS = 15;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > MAX_CHARACTERS) {
                    txtNombreUsuario.setText(s.subSequence(0, MAX_CHARACTERS));
                    txtNombreUsuario.setSelection(MAX_CHARACTERS);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        txtNombreReal.addTextChangedListener(new TextWatcher() {
            private static final int MAX_CHARACTERS = 25;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > MAX_CHARACTERS) {
                    txtNombreReal.setText(s.subSequence(0, MAX_CHARACTERS));
                    txtNombreReal.setSelection(MAX_CHARACTERS);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        btnCrearU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtContrasenha.getText().toString().isEmpty() || txtNombreReal.getText().toString().isEmpty() || txtNombreUsuario.getText().toString().isEmpty()) {
                    dialog.show();
                    return;
                }

                String usuario = txtNombreUsuario.getText().toString();
                String nome = txtNombreReal.getText().toString();
                String email = txtEmailUsuario.getText().toString();
                String contrasena = txtContrasenha.getText().toString();

                if (!validarContrasena(contrasena)) return;

                if (!validarEmail(email)) {
                    new AlertDialog.Builder(CrearUsuario.this)
                            .setTitle("Error")
                            .setMessage("El correo electrónico no tiene un formato válido (ejemplo: usuario@correo.com)")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }

                final EditText inputConfirm = new EditText(CrearUsuario.this);
                inputConfirm.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

                new AlertDialog.Builder(CrearUsuario.this)
                        .setTitle("Confirmar contraseña")
                        .setMessage("Introduce de nuevo tu contraseña")
                        .setView(inputConfirm)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String confirmacion = inputConfirm.getText().toString();

                                if (!contrasena.equals(confirmacion)) {
                                    new AlertDialog.Builder(CrearUsuario.this)
                                            .setTitle("Error")
                                            .setMessage("Las contraseñas no coinciden")
                                            .setPositiveButton("Ok", null)
                                            .show();
                                    return;
                                }

                                String contrasenaHasheada = hashPassword(contrasena);
                                UsuarioDAO uDAO = new UsuarioDAO(getApplicationContext());

                                if (uDAO.existeUsuario(usuario, email)) {
                                    new AlertDialog.Builder(CrearUsuario.this)
                                            .setTitle("Error")
                                            .setMessage("El nombre de usuario ya existe o el correo ya está siendo utilizado")
                                            .setPositiveButton("Ok", null)
                                            .show();
                                    return;
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

                                    Usuario u = new Usuario(usuario, nome, email, contrasenaHasheada, imagenBase64);
                                    uDAO.insertarDatos(u);

                                    new AlertDialog.Builder(CrearUsuario.this)
                                            .setMessage("Usuario creado con éxito")
                                            .setTitle("Éxito")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    finish();
                                                }
                                            })
                                            .show();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
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

    private boolean validarContrasena(String contrasena) {
        if (!contrasena.matches("^[A-Za-z0-9]+$")) {
            new AlertDialog.Builder(CrearUsuario.this)
                    .setTitle("Error")
                    .setMessage("La contraseña solo puede contener letras y números")
                    .setPositiveButton("Ok", null)
                    .show();
            return false;
        }

        return contrasena.matches("^(?=.*[A-Z])(?=.*\\d)[A-Za-z0-9]{6,12}$");
    }

    private boolean validarEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    }



}