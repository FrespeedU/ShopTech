package com.example.shoptech;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.firestore.FirebaseFirestore;

public class VendedorDashboardActivity extends AppCompatActivity {
    private TextView tvContadorOrdenes, tvContadorProductos;
    private FirebaseFirestore db; // Por si no lo tenías declarado en este archivo
    private androidx.recyclerview.widget.RecyclerView rvProductos;
    private ProductoVendedorAdapter adaptador;
    private java.util.List<Producto> listaProductos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendedor_dashboard);

        // Conectar botón Nuevo Producto
        Button btnNuevo = findViewById(R.id.btnNuevoProducto);
        btnNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VendedorDashboardActivity.this, NuevoProductoActivity.class);
                startActivity(intent);
            }
        });
        // Inicializamos la base de datos
        db = FirebaseFirestore.getInstance();
        // Conectar y programar el botón de Cerrar Sesión
        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesionVendedor);
        btnCerrarSesion.setOnClickListener(v -> {
            // 1. Le decimos a Firebase que destruya la sesión actual
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut();

            // 2. Lo mandamos de regreso al Login
            Intent intent = new Intent(VendedorDashboardActivity.this, LoginActivity.class);
            // 3. Borramos el historial para que no pueda regresar pulsando "Atrás" en el celular
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

// Conectamos los textos con el diseño XML del vendedor
        tvContadorOrdenes = findViewById(R.id.tvContadorOrdenes);
        tvContadorProductos = findViewById(R.id.tvContadorProductos);
        rvProductos = findViewById(R.id.rvProductosVendedor);
        rvProductos.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        listaProductos = new java.util.ArrayList<>();
        adaptador = new ProductoVendedorAdapter(listaProductos);
        rvProductos.setAdapter(adaptador);


// Llamamos al método para que cuente apenas se abra la pantalla
        cargarEstadisticasReales();

        // Conectar tarjeta de Órdenes
        CardView cvOrdenes = findViewById(R.id.cvMisOrdenes);
        if (cvOrdenes != null) {
            cvOrdenes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(VendedorDashboardActivity.this, MisPedidosActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarEstadisticasReales(); // Forzamos a que Firebase cuente de nuevo
    }
    private void cargarEstadisticasReales() {
        String miUid = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 1. CONSULTA DE PRODUCTOS: Contamos Y llenamos la lista aquí
        db.collection("productos")
                .whereEqualTo("vendedor_uid", miUid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // A. Actualizar el número gigante
                    int totalProductos = queryDocumentSnapshots.size();
                    if (tvContadorProductos != null) {
                        tvContadorProductos.setText(String.valueOf(totalProductos));
                    }

                    // B. Llenar la lista visual con los productos encontrados
                    listaProductos.clear();
                    for (com.google.firebase.firestore.QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            String nombreReal = document.getString("nombre");

                            if (nombreReal != null && !nombreReal.trim().isEmpty()) {
                                Producto p = new Producto();
                                p.setId(document.getId());
                                p.setNombre(nombreReal);

                                Long stockReal = document.getLong("stock");
                                p.setStock(stockReal != null ? stockReal.intValue() : 0);

                                listaProductos.add(p);
                            }
                        } catch (Exception e) {
                            // Ignoramos la basura vieja
                        }
                    }

                    if (adaptador != null) {
                        adaptador.notifyDataSetChanged(); // Avisamos que dibuje la lista
                    }
                })
                .addOnFailureListener(e -> {
                    if (tvContadorProductos != null) tvContadorProductos.setText("0");
                });


        // 2. CONSULTA DE ÓRDENES: Solo contamos, no llenamos listas aquí
        db.collection("ordenes")
                .whereEqualTo("vendedor_uid", miUid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int totalOrdenes = queryDocumentSnapshots.size();
                    if (tvContadorOrdenes != null) {
                        tvContadorOrdenes.setText(String.valueOf(totalOrdenes));
                    }
                })
                .addOnFailureListener(e -> {
                    if (tvContadorOrdenes != null) tvContadorOrdenes.setText("0");
                });
    }
}