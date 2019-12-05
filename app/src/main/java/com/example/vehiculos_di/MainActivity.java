package com.example.vehiculos_di;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button buttonCoche, buttonGastos;
    static String NOMBRE = "NOMBRE";
    private int COCHE = 1;
    private int GASTO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonCoche = findViewById(R.id.buttonTablaCoche);
        buttonGastos = findViewById(R.id.buttonTablaGastos);

        buttonCoche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intento = new Intent(MainActivity.this, Coches.class);

                startActivityForResult(intento, COCHE);

            }
        });
        buttonGastos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intento = new Intent(MainActivity.this, Gastos.class);

                startActivityForResult(intento, GASTO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == COCHE && resultCode == RESULT_OK) {
            String nombre = data.getStringExtra(NOMBRE);
            Toast.makeText(this, nombre, Toast.LENGTH_SHORT).show();
        }
        if (requestCode == GASTO && resultCode == RESULT_OK) {
            String nombre = data.getStringExtra(NOMBRE);
            Toast.makeText(this, nombre, Toast.LENGTH_SHORT).show();
        }
    }
}
