package com.example.shoptech;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CarritoFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflamos el diseño y lo guardamos en una variable llamada "vista"
        View vista = inflater.inflate(R.layout.fragment_carrito, container, false);

        // Buscamos el botón de pagar dentro de esa vista
        Button btnPagar = vista.findViewById(R.id.btnPagar);

        // Configuramos el salto a la pantalla de Pago
        btnPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Como estamos en un fragmento, usamos getActivity() como punto de partida
                Intent intent = new Intent(getActivity(), PagoActivity.class);
                startActivity(intent);
            }
        });

        return vista;
    }
}