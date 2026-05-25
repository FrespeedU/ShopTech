package com.example.shoptech;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PagoActivity extends AppCompatActivity {

    private TextView tvMontoPagar;
    private EditText etNumeroTarjeta, etVencimiento, etCVV, etTitular;
    private Button btnProcesarPago;

    private List<Producto> carritoRecibido;
    private String direccionEnvio;
    private double totalVenta = 0;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        tvMontoPagar = findViewById(R.id.tvMontoPagar);
        etNumeroTarjeta = findViewById(R.id.etNumeroTarjeta);
        etVencimiento = findViewById(R.id.etVencimiento);
        etCVV = findViewById(R.id.etCVV);
        etTitular = findViewById(R.id.etTitular);
        btnProcesarPago = findViewById(R.id.btnProcesarPago);

        // Recibir los datos del Checkout
        carritoRecibido = (List<Producto>) getIntent().getSerializableExtra("CARRITO");
        direccionEnvio = getIntent().getStringExtra("DIRECCION");

        calcularTotal();

        btnProcesarPago.setOnClickListener(v -> validarYProcesarPago());
    }

    private void calcularTotal() {
        if (carritoRecibido != null) {
            for (Producto p : carritoRecibido) {
                totalVenta += p.getPrecio();
            }
        }
        tvMontoPagar.setText("Total a pagar: $ " + totalVenta + " COP");
    }

    private void validarYProcesarPago() {
        if (etNumeroTarjeta.getText().toString().isEmpty() || etCVV.getText().toString().isEmpty()) {
            Toast.makeText(this, "Llene los datos de la tarjeta", Toast.LENGTH_SHORT).show();
            return;
        }

        btnProcesarPago.setEnabled(false);
        btnProcesarPago.setText("Conectando con el Banco...");

        // Simulamos un retraso de 2 segundos para dar realismo a la transacción
        new Handler().postDelayed(this::generarOrdenEnFirebase, 2000);
    }

    private void generarOrdenEnFirebase() {
        // En una app real, aquí guardaríamos una orden por cada producto o un arreglo.
        // Para simplificar tu entrega, tomaremos el vendedor del primer producto del carrito.
        String vendedorUid = carritoRecibido.get(0).getVendedor_uid();
        String compradorUid = mAuth.getCurrentUser().getUid();

        Map<String, Object> orden = new HashMap<>();
        orden.put("comprador_uid", compradorUid);
        orden.put("vendedor_uid", vendedorUid);
        orden.put("total", totalVenta);
        orden.put("direccion", direccionEnvio);
        orden.put("estado", "Pagado");

        db.collection("ordenes").add(orden)
                .addOnSuccessListener(documentReference -> {
                    // NUEVO: Descontar el stock de la base de datos
                    for (Producto p : carritoRecibido) {
                        if (p.getId() != null) {
                            db.collection("productos").document(p.getId())
                                    .update("stock", com.google.firebase.firestore.FieldValue.increment(-1));
                        }
                    }
                    Toast.makeText(this, "¡Pago Exitoso! Tu orden fue generada", Toast.LENGTH_LONG).show();
                    // Enviamos al cliente de vuelta al inicio, limpiando el historial
                    Intent intent = new Intent(PagoActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error en el pago: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnProcesarPago.setEnabled(true);
                    btnProcesarPago.setText("Intentar de nuevo");
                });
    }
}