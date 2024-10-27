package com.example.lab5_20180951;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddFoodActivity extends AppCompatActivity {

    private EditText editNombreComida, editCaloriasComida;
    private Button btnGuardarComida;
    private DatabaseHelper databaseHelper;
    private Spinner spinnerAlimentos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        editNombreComida = findViewById(R.id.editNombreComida);
        editCaloriasComida = findViewById(R.id.editCaloriasComida);
        btnGuardarComida = findViewById(R.id.btnGuardarComida);
        databaseHelper = new DatabaseHelper(this);
        spinnerAlimentos = findViewById(R.id.spinnerAlimentos);


        btnGuardarComida.setOnClickListener(view -> guardarComida());

        findViewById(R.id.btnCargarCaloriasAlimento).setOnClickListener(view -> cargarCaloriasAlimento());

    }

    private void guardarComida() {
        String nombre = editNombreComida.getText().toString();
        int calorias = Integer.parseInt(editCaloriasComida.getText().toString());

        boolean insertado = databaseHelper.insertData(nombre, calorias);
        if (insertado) {
            Toast.makeText(this, "Comida guardada", Toast.LENGTH_SHORT).show();
            finish(); // Cerrar la actividad y regresar a la principal
        } else {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
        }
    }


    private void cargarCaloriasAlimento() {
        String alimentoSeleccionado = spinnerAlimentos.getSelectedItem().toString();

        int calorias = 0;
        switch (alimentoSeleccionado) {
            case "Manzana - 52 cal":
                calorias = 52;
                break;
            case "Banana - 89 cal":
                calorias = 89;
                break;
            case "Arroz - 130 cal":
                calorias = 130;
                break;
            case "Pechuga de pollo - 165 cal":
                calorias = 165;
                break;
            case "Pan - 79 cal":
                calorias = 79;
                break;
        }
        editCaloriasComida.setText(String.valueOf(calorias));
    }
}

