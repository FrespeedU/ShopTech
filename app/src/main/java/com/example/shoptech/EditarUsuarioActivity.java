package com.example.shoptech;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class EditarUsuarioActivity extends AppCompatActivity {

    private EditText etNombre, etCorreo, etRol, etEstado;
    private Button btnGuardar, btnEliminar;
    private FirebaseFirestore db;
    private Usuario usuarioAEditar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_usuario);

        db = FirebaseFirestore.getInstance();

        etNombre = findViewById(R.id.etNombreCompleto);
        etCorreo = findViewById(R.id.etCorreoEdit);
        etRol = findViewById(R.id.etRolUsuario);
        etEstado = findViewById(R.id.etEstadoCuenta);
        btnGuardar = findViewById(R.id.btnGuardarUsuario);
        btnEliminar = findViewById(R.id.btnEliminarUsuario); // Conectamos el botón nuevo

        usuarioAEditar = (Usuario) getIntent().getSerializableExtra("USUARIO");

        if (usuarioAEditar != null) {
            // MODO EDICIÓN
            etNombre.setText(usuarioAEditar.getNombre());
            etCorreo.setText(usuarioAEditar.getCorreo());
            etRol.setText(usuarioAEditar.getRol());
            etEstado.setText(usuarioAEditar.getEstado());
            btnGuardar.setText("Actualizar Usuario");

            // Hacemos visible el botón de eliminar
            btnEliminar.setVisibility(View.VISIBLE);
            btnEliminar.setOnClickListener(v -> confirmarEliminacion());
        }

        btnGuardar.setOnClickListener(v -> procesarGuardado());
    }

    private void confirmarEliminacion() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Usuario")
                .setMessage("¿Estás seguro de que deseas eliminar a " + usuarioAEditar.getNombre() + " del sistema? Esta acción no se puede deshacer.")
                .setPositiveButton("Sí, eliminar", (dialog, which) -> ejecutarEliminacion())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void ejecutarEliminacion() {
        btnEliminar.setEnabled(false);
        btnEliminar.setText("Eliminando...");

        db.collection("usuarios").document(usuarioAEditar.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show();
                    finish(); // Regresa a la lista
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al eliminar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnEliminar.setEnabled(true);
                    btnEliminar.setText("Eliminar Usuario");
                });
    }

    private void procesarGuardado() {
        String nombre = etNombre.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String rol = etRol.getText().toString().trim();
        String estado = etEstado.getText().toString().trim();

        if (nombre.isEmpty() || correo.isEmpty() || rol.isEmpty()) {
            Toast.makeText(this, "Campos obligatorios vacíos", Toast.LENGTH_SHORT).show();
            return;
        }

        btnGuardar.setEnabled(false);

        Map<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("nombre", nombre);
        usuarioMap.put("correo", correo);
        usuarioMap.put("rol", rol);
        usuarioMap.put("estado", estado);

        if (usuarioAEditar != null) {
            db.collection("usuarios").document(usuarioAEditar.getId())
                    .set(usuarioMap)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "¡Usuario actualizado!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> btnGuardar.setEnabled(true));
        } else {
            db.collection("usuarios").add(usuarioMap)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "¡Usuario creado!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> btnGuardar.setEnabled(true));
        }
    }
}