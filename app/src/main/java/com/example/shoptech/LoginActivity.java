package com.example.shoptech;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {

    private EditText etCorreo, etPass;
    private Button btnIngresar, btnHuella;
    private TextView tvCrearCuenta, tvOlvidoPass;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etCorreo = findViewById(R.id.etCorreo);
        etPass = findViewById(R.id.etPass);
        btnIngresar = findViewById(R.id.btnIngresar);
        btnHuella = findViewById(R.id.btnHuella);
        tvCrearCuenta = findViewById(R.id.tvCrearCuenta);
        tvOlvidoPass = findViewById(R.id.tvOlvidoPass);

        tvCrearCuenta.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
            startActivity(intent);
        });

        tvOlvidoPass.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RecuperacionPassActivity.class);
            startActivity(intent);
        });

        btnIngresar.setOnClickListener(v -> {
            String correo = etCorreo.getText().toString().trim();
            String password = etPass.getText().toString().trim();

            if (correo.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Por favor, ingresa correo y contraseña", Toast.LENGTH_SHORT).show();
                return;
            }
            iniciarSesionConFirebase(correo, password);
        });

        btnHuella.setOnClickListener(v -> mostrarDialogoBiometrico());

    }
    @Override
    protected void onStart() {
        super.onStart();
        // Revisamos si ya hay una sesión activa en Firebase
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Si el usuario ya había iniciado sesión antes, lo redirigimos automáticamente
            verificarRolYRedirigir(currentUser.getUid());
        }
    }

    private void mostrarDialogoBiometrico() {
        Executor executor = ContextCompat.getMainExecutor(this);

        BiometricPrompt biometricPrompt = new BiometricPrompt(LoginActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(), "Error: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                // NUEVO: En lugar de buscar si hay un usuario activo, buscamos en la memoria interna
                SharedPreferences prefs = getSharedPreferences("MisCredenciales", Context.MODE_PRIVATE);
                String correoGuardado = prefs.getString("correo", null);
                String passGuardada = prefs.getString("password", null);

                if (correoGuardado != null && passGuardada != null) {
                    Toast.makeText(getApplicationContext(), "Huella reconocida. Iniciando sesión...", Toast.LENGTH_SHORT).show();
                    // Ejecutamos el inicio de sesión automático usando los datos guardados
                    iniciarSesionConFirebase(correoGuardado, passGuardada);
                } else {
                    Toast.makeText(getApplicationContext(), "Inicia sesión con correo primero para vincular tu huella", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Huella no reconocida. Intenta de nuevo.", Toast.LENGTH_SHORT).show();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Inicio de sesión biométrico")
                .setSubtitle("Usa tu huella dactilar para ingresar a ShopTech")
                .setNegativeButtonText("Cancelar")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void iniciarSesionConFirebase(String correo, String password) {
        mAuth.signInWithEmailAndPassword(correo, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {

                            // NUEVO: Si el login es exitoso, guardamos las credenciales en la memoria del teléfono
                            SharedPreferences prefs = getSharedPreferences("MisCredenciales", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("correo", correo);
                            editor.putString("password", password);
                            editor.apply(); // Guarda los cambios de forma asíncrona

                            verificarRolYRedirigir(user.getUid());
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void verificarRolYRedirigir(String uid) {
        db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String rol = documentSnapshot.getString("rol");
                        Intent intent;

                        if ("Administrador".equals(rol)) {
                            intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                        } else if ("Vendedor".equals(rol)) {
                            intent = new Intent(LoginActivity.this, VendedorDashboardActivity.class);
                        } else {
                            intent = new Intent(LoginActivity.this, MainActivity.class);
                        }

                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Error: El usuario no tiene un perfil configurado.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(LoginActivity.this, "Error al obtener datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}