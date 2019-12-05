package com.example.vehiculos_di;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class Coches extends AppCompatActivity {
    Button buttonVolver, buttonModificar, buttonInsertar, buttonBorrar, buttonMostrar;
    EditText matricula, marca, color;
    ListView listaCoche;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coches);
        buttonVolver = findViewById(R.id.buttonVolver);
        buttonModificar = findViewById(R.id.buttonModCoches);
        buttonInsertar = findViewById(R.id.buttonInsCoches);
        buttonBorrar = findViewById(R.id.buttonBorCoches);
        buttonMostrar = findViewById(R.id.buttonMosCoches);
        matricula = findViewById(R.id.editTextMatrCoches);
        marca = findViewById(R.id.editTextMarca);
        color = findViewById(R.id.editTextColor);
        listaCoche = findViewById(R.id.ListaCoches);
        buttonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(MainActivity.NOMBRE, "Vuelvo de coches");
                setResult(RESULT_OK, intent);
                finish();
            }
        });


    }
}
