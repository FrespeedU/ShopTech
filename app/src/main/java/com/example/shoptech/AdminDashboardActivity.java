package com.example.shoptech;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {

    private Button btnIrNuevoProducto, btnCerrarSesion;
    private RecyclerView rvProductos;

    private ProductoAdapter adaptador;
    private List<Producto> listaProductos;

    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();

        btnIrNuevoProducto = findViewById(R.id.btnIrNuevoProducto);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesionAdmin);
        rvProductos = findViewById(R.id.rvProductosAdmin);

        // Configurar la lista deslizable
        rvProductos.setLayoutManager(new LinearLayoutManager(this));
        listaProductos = new ArrayList<>();
        adaptador = new ProductoAdapter(listaProductos);
        rvProductos.setAdapter(adaptador);

        // Cargar los productos desde la nube
        cargarInventario();

        // Puente hacia la pantalla de crear producto
        btnIrNuevoProducto.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, NuevoProductoActivity.class));
        });

        // Puente para Cerrar Sesión
        btnCerrarSesion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        // Conectar el botón de Gestión de Usuarios
        Button btnIrAGestion = findViewById(R.id.btnIrAGestionUsuarios);
        if (btnIrAGestion != null) {
            btnIrAGestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Viajamos a la pantalla que me compartiste: GestionUsuariosActivity
                    Intent intent = new Intent(AdminDashboardActivity.this, GestionUsuariosActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    // Método que descarga los productos cada vez que abrimos la pantalla
    @Override
    protected void onResume() {
        super.onResume();
        cargarInventario();
    }

    private void cargarInventario() {
        // Leemos la colección "productos" de Firebase
        db.collection("productos")
                .whereEqualTo("estado", "activo") // ¡Este es el filtro clave!
                .get()

                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaProductos.clear(); // Limpiamos la lista para evitar duplicados
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Producto producto = document.toObject(Producto.class);
                        producto.setId(document.getId()); // Guardamos el ID único
                        listaProductos.add(producto);
                    }
                    adaptador.notifyDataSetChanged(); // Le avisamos al adaptador que dibuje los cambios
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar inventario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}