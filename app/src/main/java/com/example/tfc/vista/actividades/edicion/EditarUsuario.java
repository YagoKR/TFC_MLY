package com.example.tfc.vista.actividades.edicion;

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
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tfc.R;
import com.example.tfc.bbdd.dao.UsuarioDAO;
import com.example.tfc.bbdd.entidades.Usuario;
import com.example.tfc.vista.actividades.creacion.CrearUsuario;

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

        editNombreReal.addTextChangedListener(new TextWatcher() {
            private static final int MAX_CHARACTERS = 25;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > MAX_CHARACTERS) {
                    editNombreReal.setText(s.subSequence(0, MAX_CHARACTERS));
                    editNombreReal.setSelection(MAX_CHARACTERS);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
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

                if (!validarEmail(editmail.getText().toString())) return;

                String nuevoEmail = editmail.getText().toString().trim();
                Usuario usuarioExistente = usuarioDAO.obtenerUsuarioPorEmail(nuevoEmail);
                if (usuarioExistente != null && !usuarioExistente.getIdUsuario().equals(usuario.getIdUsuario())) {
                    new AlertDialog.Builder(EditarUsuario.this)
                            .setTitle("Error")
                            .setMessage("El correo ya está registrado en otra cuenta")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }

                usuario.setEmail(nuevoEmail);

                if (editcontrasena.isEnabled()) {
                    String nuevaContrasena = editcontrasena.getText().toString().trim();
                    if (!nuevaContrasena.isEmpty()) {
                        if (!validarContrasena(nuevaContrasena)) return;

                        final EditText inputConfirm = new EditText(EditarUsuario.this);
                        inputConfirm.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

                        new AlertDialog.Builder(EditarUsuario.this)
                                .setTitle("Confirmar contraseña")
                                .setMessage("Introduce de nuevo tu nueva contraseña")
                                .setView(inputConfirm)
                                .setPositiveButton("Aceptar", (dialog, which) -> {
                                    String confirmacion = inputConfirm.getText().toString();
                                    if (!nuevaContrasena.equals(confirmacion)) {
                                        new AlertDialog.Builder(EditarUsuario.this)
                                                .setTitle("Error")
                                                .setMessage("Las contraseñas no coinciden.")
                                                .setPositiveButton("Ok", null)
                                                .show();
                                        return;
                                    }

                                    usuario.setContrasenha(hashPassword(nuevaContrasena));

                                    if (selectedImageUri != null) {
                                        try {
                                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                                            usuario.setImagen(bitmapToBase64(bitmap));
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
                                            .setPositiveButton("Ok", (dialogInterface, i1) -> {
                                                setResult(RESULT_OK);
                                                finish();
                                            }).show();

                                })
                                .setNegativeButton("Cancelar", null)
                                .show();

                    }
                } else {
                    if (selectedImageUri != null) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                            usuario.setImagen(bitmapToBase64(bitmap));
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
                            .setPositiveButton("Ok", (dialogInterface, i1) -> {
                                setResult(RESULT_OK);
                                finish();
                            }).show();
                }
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
        if (bitmap == null) return null;

        Bitmap resized = resizeAndCropBitmap(bitmap, 256, 256);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        resized.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

        byte[] byteArray = outputStream.toByteArray();

        return android.util.Base64.encodeToString(byteArray, Base64.DEFAULT);
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
            new AlertDialog.Builder(EditarUsuario.this)
                    .setTitle("Error")
                    .setMessage("La contraseña solo puede contener letras y números.")
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