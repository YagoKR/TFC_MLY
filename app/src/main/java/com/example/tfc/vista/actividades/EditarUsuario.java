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

public class EditarUsuario extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText editNombreUsuario, editNombreReal, editmail, editcontrasena;
    private ImageView imageView;
    private Button btnEditarUsuario;
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
        btnEditarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                usuario.setNombre(editNombreReal.getText().toString());
                usuario.setEmail(editmail.getText().toString());
                usuario.setContrasenha(editcontrasena.getText().toString());

                if (selectedImageUri != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                        String nuevaImagenBase64 = bitmapToBase64(bitmap);
                        usuario.setImagen(nuevaImagenBase64);
                    } catch (IOException e) {
                        e.printStackTrace();
                        usuario.setImagen(imagenBase64Actual);
                    }
                } else {
                    usuario.setImagen(imagenBase64Actual);
                }

                usuarioDAO.actualizarUsuario(usuario);

                AlertDialog.Builder builder = new AlertDialog.Builder(EditarUsuario.this);
                builder.setMessage("Usuario modificado con éxito")
                        .setTitle("Éxito")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
                builder.create().show();
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
}