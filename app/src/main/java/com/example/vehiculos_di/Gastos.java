package com.example.vehiculos_di;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


import androidx.appcompat.app.AppCompatActivity;

public class Gastos extends AppCompatActivity {
    Button buttonVolver, buttonModificar, buttonInsertar, buttonBorrar, buttonMostrar;
    EditText matricula, concepto, valor, id;
    ListView listaGastos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gastos);
        buttonVolver = findViewById(R.id.buttonVolver2);
        buttonModificar = findViewById(R.id.buttonModGastos);
        buttonInsertar = findViewById(R.id.buttonInsGastos);
        buttonBorrar = findViewById(R.id.buttonBorGastos);
        buttonMostrar = findViewById(R.id.buttonMostGastos);
        matricula = findViewById(R.id.editTextMatriculaGastos);
        concepto = findViewById(R.id.editTextConcepto);
        valor = findViewById(R.id.editTextValor);
        id = findViewById(R.id.editTextID);
        listaGastos = findViewById(R.id.ListaGastos);

        /*buttonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(MainActivity.NOMBRE, "Vuelvo de Gastos");
                setResult(RESULT_OK, intent);
                finish();
            }
        });*/
    }

}
