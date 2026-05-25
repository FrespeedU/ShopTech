package com.example.shoptech;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvProductos;
    private Button btnVerCarrito;
    private Button btnSalir;

    private ProductoCompradorAdapter adaptador;
    private List<Producto> listaProductos;
    private List<Producto> carritoCompras;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        rvProductos = findViewById(R.id.rvProductosComprador);
        btnVerCarrito = findViewById(R.id.btnVerCarrito);
        btnSalir = findViewById(R.id.btnSalirComprador); // Conectamos el nuevo botón de salir

        carritoCompras = new ArrayList<>();
        listaProductos = new ArrayList<>();

        rvProductos.setLayoutManager(new LinearLayoutManager(this));

        // Inicializamos el adaptador. Ahora, en lugar de agregar directo, abrimos el diálogo.
        adaptador = new ProductoCompradorAdapter(listaProductos, producto -> {
            mostrarDialogoDetalles(producto);
        });

        rvProductos.setAdapter(adaptador);

        cargarCatalogoPublico();

        // Acción: Cerrar Sesión
        btnSalir.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        });

        // Acción: Ir al Checkout
        btnVerCarrito.setOnClickListener(v -> {
            if (carritoCompras.isEmpty()) {
                Toast.makeText(this, "Tu carrito está vacío", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(MainActivity.this, CheckoutActivity.class);
                intent.putExtra("CARRITO", (Serializable) carritoCompras);
                startActivity(intent);
            }
        });
    }

    // NUEVO MÉTODO: Construye y muestra la ventana emergente
    private void mostrarDialogoDetalles(Producto producto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflamos el diseño que acabas de crear en el paso 1
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_detalle_producto, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        // Esto hace que el fondo cuadrado por defecto de Android se vuelva transparente
        // para que se vean las esquinas redondeadas de tu CardView oscuro
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Conectar las vistas de adentro de la ventana emergente
        ImageView ivImagen = dialogView.findViewById(R.id.ivDialogImagen);
        TextView tvNombre = dialogView.findViewById(R.id.tvDialogNombre);
        TextView tvDescripcion = dialogView.findViewById(R.id.tvDialogDescripcion);
        TextView tvPrecio = dialogView.findViewById(R.id.tvDialogPrecio);
        Button btnAgregar = dialogView.findViewById(R.id.btnDialogAgregar);
        Button btnCerrar = dialogView.findViewById(R.id.btnDialogCerrar);

        // Llenar los textos con la información del producto
        tvNombre.setText(producto.getNombre());
        tvDescripcion.setText(producto.getDescripcion());
        tvPrecio.setText("$ " + producto.getPrecio() + " COP");

        // Cargar la imagen con Glide
        Glide.with(this)
                .load(producto.getImagen_url())
                .placeholder(android.R.drawable.ic_menu_camera)
                .into(ivImagen);

        // Lógica: Si presiona Añadir al Carrito
        btnAgregar.setOnClickListener(v -> {
            carritoCompras.add(producto);
            btnVerCarrito.setText("Ver Carrito (" + carritoCompras.size() + ")");
            Toast.makeText(this, producto.getNombre() + " agregado al carrito", Toast.LENGTH_SHORT).show();
            dialog.dismiss(); // Cierra la ventana emergente
        });

        // Lógica: Si presiona Cancelar
        btnCerrar.setOnClickListener(v -> dialog.dismiss());

        // Mostrar la ventana en pantalla
        dialog.show();
    }

    private void cargarCatalogoPublico() {
        db.collection("productos")
                .whereEqualTo("estado", "activo")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaProductos.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            String nombreReal = document.getString("nombre");

                            // FILTRO: Solo armamos el producto si tiene un nombre válido
                            if (nombreReal != null && !nombreReal.trim().isEmpty()) {
                                Producto p = new Producto();
                                p.setId(document.getId());
                                p.setNombre(nombreReal);

                                // Extraemos el resto de datos con red de seguridad
                                String desc = document.getString("descripcion");
                                p.setDescripcion(desc != null ? desc : "Sin descripción");

                                String imgUrl = document.getString("imagen_url");
                                p.setImagen_url(imgUrl != null ? imgUrl : "");

                                String vendUid = document.getString("vendedor_uid");
                                p.setVendedor_uid(vendUid != null ? vendUid : "");

                                Double precioReal = document.getDouble("precio");
                                p.setPrecio(precioReal != null ? precioReal : 0.0);

                                Long stockReal = document.getLong("stock");
                                p.setStock(stockReal != null ? stockReal.intValue() : 0);

                                listaProductos.add(p);
                            }
                        } catch (Exception e) {
                            // Si el documento viejo está corrupto, lo ignoramos y no se cierra la app
                        }
                    }
                    adaptador.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al cargar catálogo", Toast.LENGTH_SHORT).show());
    }
}