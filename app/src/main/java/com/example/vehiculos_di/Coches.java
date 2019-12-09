package com.example.vehiculos_di;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Coches extends AppCompatActivity {
    Button buttonVolver, buttonModificar, buttonInsertar, buttonBorrar, buttonMostrar;
    EditText matricula, marca, color;
    ListView lista;
    static String direccion="/web/listadoCSV.php";
    static String SERVIDOR = "http://192.168.100.19";//casa
    //static String SERVIDOR = "http://192.168.0.111:8080";//clase
    ProgressDialog progressDialog;

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
        lista = findViewById(R.id.ListaCoches);

        buttonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(MainActivity.NOMBRE, "Vuelvo de coches");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        buttonMostrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DescargarCSV descargarCSV = new DescargarCSV();
                descargarCSV.execute(direccion);
                //descargarCSV.execute("/pract/listadoCSV.php");
            }
        });
        buttonInsertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (matricula.getText().toString() == " ") {
                    Toast.makeText(Coches.this, "Debe introducir al menos el campo matrícula", Toast.LENGTH_SHORT).show();
                } else {
                    if (!comprobar(matricula.getText().toString(), direccion)) {
                        String dir = "/web/insertarPOSTCoches.php";
                        Insertar(matricula.getText().toString(), marca.getText().toString(), color.getText().toString(), dir);
                    } else {
                        Toast.makeText(Coches.this, "Ya se ha introducido esa matrícula", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        buttonModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (matricula.getText().toString() == " ") {
                    Toast.makeText(Coches.this, "Debe introducir al menos el campo matrícula", Toast.LENGTH_SHORT).show();
                } else {

                    if (comprobar(matricula.getText().toString(), direccion)) {
                        String dir = "/web/updateGETCoches.php";
                        Modificar(matricula.getText().toString(), marca.getText().toString(), color.getText().toString(), dir);
                    } else {
                        Toast.makeText(Coches.this, "No existe esa matrícula", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        buttonBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (matricula.getText().toString() == " ") {
                    Toast.makeText(Coches.this, "Debe introducir el campo matrícula", Toast.LENGTH_SHORT).show();
                } else {


                    if (comprobar(matricula.getText().toString(), direccion)) {
                        String dir = "/web/deleteGETCoches.php";
                        Eliminar(matricula.getText().toString(), dir);
                    } else {
                        Toast.makeText(Coches.this, "No existe esa matrícula", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    private class DescargarCSV extends AsyncTask<String, Void, Void> {
        String total = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Coches.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle("Descargando la información de la red en csv.");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ArrayAdapter<String> adapter;
            List<String> list = new ArrayList<String>();

            String[] lineas = total.split("\n");

            for (String lin : lineas) {
                String[] campos = lin.split(",");
                String dato = " MATRÍCULA: " + campos[0];
                dato += " MARCA: " + campos[1];
                dato += " COLOR: " + campos[2];
                list.add(dato);
            }
            adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, list);
            lista.setAdapter(adapter);

            progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(String... strings) {
            String script = strings[0];

            URL url = null;
            HttpURLConnection httpURLConnection = null;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                url = new URL(SERVIDOR + script);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

                    String linea = "";

                    while ((linea = br.readLine()) != null) {
                        total += linea + "\n";
                    }

                    br.close();
                    inputStream.close();


                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.i("CONEXION", total);

            return null;
        }
    }

    private void Insertar(String Matricula, String Marca, String color, String dir) {
        String script = null;

        script = SERVIDOR + dir;

        String contenido = "";
        try {
            System.out.println(script);
            URLConnection conexion = null;

            conexion = new URL(script).openConnection();
            //conexion.connect();
            conexion.setDoOutput(true);

            PrintStream ps = new PrintStream(conexion.getOutputStream());

            ps.print("Matricula=" + Matricula);
            ps.print("&Marca=" + Marca);
            ps.print("&color=" + color);

            InputStream inputStream = conexion.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String linea = "";

            while ((linea = br.readLine()) != null) {
                contenido += linea;
            }

            br.close();

        } catch (MalformedURLException ex) {
        } catch (IOException e) {
        }

    }

    private void Modificar(String Matricula, String marca, String color, String dir) {
        String script = SERVIDOR + dir + "?Matricula=" + Matricula + "&Marca=" + marca + "&Color=" + color;
        String contenido = "";

        System.out.println(script);
        try {

            URLConnection conexion = null;

            conexion = new URL(script).openConnection();
            conexion.connect();
            InputStream inputStream = conexion.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

            String linea = "";

            while ((linea = br.readLine()) != null) {
                contenido += linea;

            }
            br.close();

        } catch (MalformedURLException ex) {

        } catch (UnsupportedEncodingException ex) {
        } catch (IOException ex) {

        }
    }

    private void Eliminar(String matricula, String dir) {
        String script = SERVIDOR + dir + "?matricula=" + matricula;
        String contenido = "";
        try {

            URLConnection conexion = null;

            conexion = new URL(script).openConnection();
            conexion.connect();
            InputStream inputStream = conexion.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

            String linea = "";

            while ((linea = br.readLine()) != null) {
                contenido += linea;

            }
            br.close();

        } catch (MalformedURLException ex) {
        } catch (UnsupportedEncodingException ex) {
        } catch (IOException ex) {
        }
    }

    private boolean comprobar(String cadena, String dir) {
        boolean existe = false;
        String csv = "";
        String scriptConsulta = SERVIDOR + dir;

        URLConnection conexion = null;
        try {
            conexion = new URL(scriptConsulta).openConnection();

            InputStream inputStream = conexion.getInputStream();

            BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

            String linea = "";

            while ((linea = bf.readLine()) != null) {
                csv += linea + "\n";
            }
            bf.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        String contenido = csv;

        String lineas[] = contenido.split("\n");

        for (String lin : lineas) {
            String[] campos = lin.split(",");
            if (cadena.equals(campos[0])) {
                existe = true;
            }
        }
        return existe;

    }

}
