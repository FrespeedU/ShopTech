package com.example.shoptech;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NuevoProductoActivity extends AppCompatActivity {

    private ImageView ivPreviewProducto;
    private Button btnSeleccionarImagen, btnGuardarProducto;
    private EditText etNombre, etDescripcion, etMarca, etModelo, etSku, etCategoria, etPrecio, etStock;

    private Uri imageUri; // Guardará la ruta de la foto (sea de cámara o galería)

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageRef;

    // Lanzador para la Galería
    private final ActivityResultLauncher<String> abrirGaleria = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    imageUri = uri;
                    ivPreviewProducto.setImageURI(uri);
                }
            }
    );

    // Lanzador para la Cámara
    private final ActivityResultLauncher<Uri> tomarFoto = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            exito -> {
                if (exito && imageUri != null) {
                    ivPreviewProducto.setImageURI(null); // Refrescamos la vista
                    ivPreviewProducto.setImageURI(imageUri); // Mostramos la foto recién tomada
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_producto);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        // Conectar Vistas
        ivPreviewProducto = findViewById(R.id.ivPreviewProducto);
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        btnGuardarProducto = findViewById(R.id.btnGuardarProducto);

        etNombre = findViewById(R.id.etNombreProducto);
        etDescripcion = findViewById(R.id.etDescripcionProducto);
        etMarca = findViewById(R.id.etMarcaProducto);
        etModelo = findViewById(R.id.etModeloProducto);
        etSku = findViewById(R.id.etSkuProducto);
        etCategoria = findViewById(R.id.etCategoriaProducto);
        etPrecio = findViewById(R.id.etPrecioProducto);
        etStock = findViewById(R.id.etStockProducto);

        // Mostrar menú de opciones al tocar el botón de imagen
        btnSeleccionarImagen.setOnClickListener(v -> mostrarOpcionesImagen());

        // Acción: Guardar todo en Firebase
        btnGuardarProducto.setOnClickListener(v -> validarYSubirProducto());
    }

    private void mostrarOpcionesImagen() {
        String[] opciones = {"Tomar Foto con la Cámara", "Elegir de la Galería", "Cancelar"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona el origen de la imagen");
        builder.setItems(opciones, (dialog, which) -> {
            if (which == 0) {
                activarCamara();
            } else if (which == 1) {
                abrirGaleria.launch("image/*");
            } else {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void activarCamara() {
        // Creamos un archivo temporal vacío en la memoria del celular
        File archivoFoto = new File(getCacheDir(), "foto_producto_temp.jpg");
        // Le damos el pasaporte seguro de FileProvider para que la cámara pueda escribir ahí
        imageUri = FileProvider.getUriForFile(this, "com.example.shoptech.fileprovider", archivoFoto);

        // Disparamos la cámara
        tomarFoto.launch(imageUri);
    }

    private void validarYSubirProducto() {
        String nombre = etNombre.getText().toString().trim();
        String precioStr = etPrecio.getText().toString().trim();
        String stockStr = etStock.getText().toString().trim();

        if (nombre.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty()) {
            Toast.makeText(this, "Nombre, Precio y Stock son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri == null) {
            Toast.makeText(this, "Por favor selecciona o toma una imagen del producto", Toast.LENGTH_SHORT).show();
            return;
        }

        btnGuardarProducto.setEnabled(false);
        btnGuardarProducto.setText("Subiendo a la nube...");

        String nombreArchivo = UUID.randomUUID().toString() + ".jpg";
        StorageReference imagenRef = storageRef.child("productos/" + mAuth.getCurrentUser().getUid() + "/" + nombreArchivo);

        imagenRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imagenRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        guardarDatosEnFirestore(uri.toString());
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al subir imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnGuardarProducto.setEnabled(true);
                    btnGuardarProducto.setText("Subir al Inventario");
                });
    }

    private void guardarDatosEnFirestore(String imageUrl) {
        Map<String, Object> producto = new HashMap<>();
        producto.put("nombre", etNombre.getText().toString().trim());
        producto.put("descripcion", etDescripcion.getText().toString().trim());
        producto.put("marca", etMarca.getText().toString().trim());
        producto.put("modelo", etModelo.getText().toString().trim());
        producto.put("sku", etSku.getText().toString().trim());
        producto.put("categoria", etCategoria.getText().toString().trim());
        producto.put("precio", Double.parseDouble(etPrecio.getText().toString().trim()));
        producto.put("stock", Integer.parseInt(etStock.getText().toString().trim()));
        producto.put("imagen_url", imageUrl);
        producto.put("vendedor_uid", mAuth.getCurrentUser().getUid());
        producto.put("estado", "activo");

        db.collection("productos").add(producto)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "¡Producto registrado con éxito!", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar producto: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnGuardarProducto.setEnabled(true);
                    btnGuardarProducto.setText("Subir al Inventario");
                });
    }
}