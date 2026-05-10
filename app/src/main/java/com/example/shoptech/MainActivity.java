package com.example.shoptech;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigationView = findViewById(R.id.menu_inferior);

        // 1. Mostrar el catálogo por defecto cuando se abre esta pantalla
        if (savedInstanceState == null) {
            cargarFragmento(new InicioFragment());
        }

        // 2. Configurar los clics del menú inferior
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_inicio) {
                    cargarFragmento(new InicioFragment());
                    return true;
                } else if (id == R.id.nav_carrito) {
                    cargarFragmento(new CarritoFragment()); // <-- Línea nueva
                    return true;
                } else if (id == R.id.nav_perfil) {
                    cargarFragmento(new PerfilFragment()); // <-- Línea nueva
                    return true;
                }
                return false;
            }
        });
    }

    // 3. Método auxiliar que hace el intercambio de los Fragmentos visuales
    private void cargarFragmento(Fragment fragmento) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenedor_fragmentos, fragmento)
                .commit();
    }
}