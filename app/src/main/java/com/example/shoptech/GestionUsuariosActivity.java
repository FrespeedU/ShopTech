package com.example.shoptech;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class GestionUsuariosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_usuarios);

        // 1. Buscamos el botón de Nuevo Usuario por su ID
        Button btnNuevo = findViewById(R.id.btnNuevoUsuario);

        // 2. Programamos el salto a la pantalla de Edición
        btnNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GestionUsuariosActivity.this, EditarUsuarioActivity.class);
                startActivity(intent);
            }
        });
    }
}