package com.example.shoptech;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class InicioFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflamos el diseño y lo guardamos en una variable llamada "vista"
        View vista = inflater.inflate(R.layout.fragment_inicio, container, false);

        // 1. Buscamos la tarjeta del producto usando el ID que le acabamos de poner
        CardView cvProducto = vista.findViewById(R.id.cvProductoDestacado);

        // 2. Configuramos el salto a la pantalla de Detalle
        if (cvProducto != null) {
            cvProducto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Viajamos desde el fragmento actual hacia el Detalle del Producto
                    Intent intent = new Intent(getActivity(), DetalleProductoActivity.class);
                    startActivity(intent);
                }
            });
        }

        return vista;
    }
}