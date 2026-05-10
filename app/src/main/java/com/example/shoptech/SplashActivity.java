package com.example.shoptech;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Temporizador para saltar a la pantalla de Login después de 3 segundos
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Creamos la intención (Intent) de viajar de SplashActivity a LoginActivity
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);

                // Cerramos el Splash para que si el usuario presiona "Atrás", no vuelva a esta pantalla
                finish();
            }
        }, 3000); // 3000 milisegundos = 3 segundos
    }
}