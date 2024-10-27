package com.example.lab5_20180951;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddFoodActivity extends AppCompatActivity {

    private EditText editNombreComida, editCaloriasComida;
    private Button btnGuardarComida;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        editNombreComida = findViewById(R.id.editNombreComida);
        editCaloriasComida = findViewById(R.id.editCaloriasComida);
        btnGuardarComida = findViewById(R.id.btnGuardarComida);
        databaseHelper = new DatabaseHelper(this);

        btnGuardarComida.setOnClickListener(view -> guardarComida());
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
}

