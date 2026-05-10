package com.example.shoptech;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // --- Flujo Principal (Botón Fucsia Ingresar) ---
        Button btnIngresar = findViewById(R.id.btnIngresar);
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Cerramos el login
            }
        });

        // --- Flujo: Recuperar Contraseña ---
        TextView tvOlvidoPass = findViewById(R.id.tvOlvidoPass);
        tvOlvidoPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RecuperacionPassActivity.class);
                startActivity(intent);
            }
        });

        // --- Enlaces temporales para pruebas ---
        TextView tvAdmin = findViewById(R.id.tvIngresarAdmin);
        tvAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                startActivity(intent);
            }
        });

        TextView tvVendedor = findViewById(R.id.tvIngresarVendedor);
        tvVendedor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, VendedorDashboardActivity.class);
                startActivity(intent);
            }
        });
    }
}