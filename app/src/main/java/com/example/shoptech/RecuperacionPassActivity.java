package com.example.shoptech;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class RecuperacionPassActivity extends AppCompatActivity {

    private EditText etCorreo;
    private Button btnEnviar;
    private TextView tvVolverLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperacion_pass);

        mAuth = FirebaseAuth.getInstance();

        etCorreo = findViewById(R.id.etCorreoRecuperar);
        btnEnviar = findViewById(R.id.btnEnviarEnlace);
        tvVolverLogin = findViewById(R.id.tvVolverLoginRecuperar);

        // Volver a la pantalla de Login
        tvVolverLogin.setOnClickListener(v -> {
            startActivity(new Intent(RecuperacionPassActivity.this, LoginActivity.class));
            finish();
        });

        // Lógica de recuperación
        btnEnviar.setOnClickListener(v -> {
            String correo = etCorreo.getText().toString().trim();

            if (correo.isEmpty()) {
                Toast.makeText(RecuperacionPassActivity.this, "Por favor, ingresa tu correo electrónico", Toast.LENGTH_SHORT).show();
                return;
            }

            // Método nativo de Firebase para restablecer contraseña
            mAuth.sendPasswordResetEmail(correo)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(RecuperacionPassActivity.this, "Enlace de recuperación enviado a tu correo", Toast.LENGTH_LONG).show();
                            // Regresar al Login automáticamente tras enviar el correo
                            startActivity(new Intent(RecuperacionPassActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(RecuperacionPassActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}