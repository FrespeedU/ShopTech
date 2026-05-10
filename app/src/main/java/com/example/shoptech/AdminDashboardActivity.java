package com.example.shoptech;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // --- Navegación hacia Gestión de Usuarios ---
        CardView cvUsuarios = findViewById(R.id.cvGestionUsuarios);
        cvUsuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, GestionUsuariosActivity.class);
                startActivity(intent);
            }
        });

        // --- Navegación hacia Gestión de Productos (Inventario Global) ---
        CardView cvProductos = findViewById(R.id.cvGestionProductos);
        cvProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, GestionProductosActivity.class);
                startActivity(intent);
            }
        });

        // --- Navegación hacia Reporte de Ventas ---
        TextView tvVentas = findViewById(R.id.tvVentasMonto);
        tvVentas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, ReporteVentasActivity.class);
                startActivity(intent);
            }
        });
    }
}