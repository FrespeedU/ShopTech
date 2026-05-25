package com.example.shoptech;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class GestionUsuariosActivity extends AppCompatActivity {

    private RecyclerView rvUsuarios;
    private UsuarioAdapter adaptador;
    private List<Usuario> listaUsuarios;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_usuarios);

        db = FirebaseFirestore.getInstance();
        rvUsuarios = findViewById(R.id.rvUsuariosAdmin);
        rvUsuarios.setLayoutManager(new LinearLayoutManager(this));

        listaUsuarios = new ArrayList<>();
        adaptador = new UsuarioAdapter(listaUsuarios, usuario -> {
            // Al tocar un usuario, viajamos a editarlo pasándole sus datos actuales
            Intent intent = new Intent(GestionUsuariosActivity.this, EditarUsuarioActivity.class);
            intent.putExtra("USUARIO", usuario);
            startActivity(intent);
        });
        rvUsuarios.setAdapter(adaptador);

        Button btnNuevo = findViewById(R.id.btnNuevoUsuario);
        btnNuevo.setOnClickListener(v -> {
            Intent intent = new Intent(GestionUsuariosActivity.this, EditarUsuarioActivity.class);
            startActivity(intent);
        });

        cargarUsuariosDesdeFirestore();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarUsuariosDesdeFirestore(); // Recarga la lista si regresamos de guardar uno nuevo
    }

    private void cargarUsuariosDesdeFirestore() {
        db.collection("usuarios").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaUsuarios.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            String nombre = document.getString("nombre");
                            if (nombre != null && !nombre.trim().isEmpty()) {
                                Usuario u = new Usuario();
                                u.setId(document.getId());
                                u.setNombre(nombre);
                                u.setCorreo(document.getString("correo"));
                                u.setRol(document.getString("rol"));
                                u.setEstado(document.getString("estado"));
                                listaUsuarios.add(u);
                            }
                        } catch (Exception e) {
                            // Ignora datos corruptos
                        }
                    }
                    adaptador.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al cargar usuarios", Toast.LENGTH_SHORT).show());
    }
}