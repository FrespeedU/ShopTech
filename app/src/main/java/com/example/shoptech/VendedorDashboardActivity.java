package com.example.shoptech;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class VendedorDashboardActivity extends AppCompatActivity {

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
}