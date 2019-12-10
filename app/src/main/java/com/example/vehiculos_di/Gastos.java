package com.example.vehiculos_di;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Gastos extends AppCompatActivity {
    Button buttonVolver, buttonModificar, buttonInsertar, buttonBorrar, buttonMostrar;
    EditText matricula, concepto, valor, id;
    ListView lista;
    static String direccion = "/web/listadoJSON.php";
    static String direccion2 = "/web/listadoCSV.php";
    //static String SERVIDOR = "http:// 192.168.100.19";
    static String SERVIDOR = "http://192.168.0.111:8080";//Clase
    ProgressDialog progressDialog;

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
        lista = findViewById(R.id.ListaGastos);

        buttonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(MainActivity.NOMBRE, "Vuelvo de Gastos");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        buttonMostrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DescargarJSON descargarJSON = new DescargarJSON();
                descargarJSON.execute(direccion);
                //descargarJSON.execute("/pract/listadoJSON.php");
            }
        });
        buttonInsertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (matricula.getText().toString() == " ") {
                    Toast.makeText(Gastos.this, "Debe introducir al menos el campo matrícula", Toast.LENGTH_SHORT).show();
                } else {
                    if (comprobarMatricula(matricula.getText().toString(), direccion2)) {

                        String dir = "/web/insertarPOSTGastos.php";
                        Insertar(matricula.getText().toString(), concepto.getText().toString(), valor.getText().toString(), dir);
                    } else {
                        Toast.makeText(Gastos.this, "No existe esa matrícula en la tabla coches", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });
        buttonModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (matricula.getText().toString() == " ") {
                    Toast.makeText(Gastos.this, "Debe introducir el campo ID", Toast.LENGTH_SHORT).show();
                } else {

                    if (comprobar(id.getText().toString(), direccion)) {
                        if (comprobarMatricula(matricula.getText().toString(), direccion2)) {
                            String dir = "/web/updateGETGastos.php";
                            Modificar(matricula.getText().toString(), concepto.getText().toString(), valor.getText().toString(), dir);
                        } else {
                            Toast.makeText(Gastos.this, "No existe esa matrícula en la tabla coches", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(Gastos.this, "No existe ese id", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        buttonBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (matricula.getText().toString() == " ") {
                    Toast.makeText(Gastos.this, "Debe introducir el campo ID", Toast.LENGTH_SHORT).show();
                } else {


                    if (comprobar(id.getText().toString(), direccion)) {
                        String dir = "/web/deleteGETGastos.php";
                        Eliminar(matricula.getText().toString(), dir);
                    } else {
                        Toast.makeText(Gastos.this, "No existe ese id", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private class DescargarJSON extends AsyncTask<String, Void, Void> {
        List<String> list = new ArrayList<String>();

        @Override
        protected Void doInBackground(String... strings) {
            String script = strings[0];
            String url = SERVIDOR + script;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String contenido = "";
            try {

                URLConnection conexion = null;

                conexion = new URL(url).openConnection();
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
            JsonParser parser = new JsonParser();
            JsonArray jsonArray = parser.parse(contenido).getAsJsonArray();
            String fila1 = "ID\t\t\tMatrícula\t\t\tConcepto\t\t\t\t\tValor";
            list.add(fila1);
            for (JsonElement elemento : jsonArray) {
                String fila = "";
                JsonObject objeto = elemento.getAsJsonObject();


                Set<Map.Entry<String, JsonElement>> entrySet = objeto.entrySet();
                int contador = 0;

                for (Map.Entry<String, JsonElement> entry : entrySet) {
                    if (contador % 2 != 0) {
                        fila += "  " + entry.getValue().getAsString();
                    }
                    contador++;
                    // entry.getValue();

                }

                list.add(fila);


            }


            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Gastos.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle("Descargando la información de la red en JSON.");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ArrayAdapter<String> adapter;

            adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, list);
            lista.setAdapter(adapter);

            progressDialog.dismiss();
        }
    }

    private void Insertar(String Matricula, String concepto, String valor, String dir) {
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
            ps.print("&concepto=" + concepto);
            ps.print("&valor=" + valor);

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

    private void Modificar(String Matricula, String concepto, String valor, String dir) {
        String script = SERVIDOR + dir + "?Matricula=" + Matricula + "&concepto=" + concepto + "&valor=" + valor;
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

    private void Eliminar(String id, String dir) {
        String script = SERVIDOR + dir + "?id=" + id;
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

        String scriptConsulta = SERVIDOR + dir;

        String contenido = "";
        try {

            URLConnection conexion = null;

            conexion = new URL(scriptConsulta).openConnection();
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
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(contenido).getAsJsonArray();

        for (JsonElement elemento : jsonArray) {
            String fila = "";
            JsonObject objeto = elemento.getAsJsonObject();


            Set<Map.Entry<String, JsonElement>> entrySet = objeto.entrySet();
            int contador = 0;

            for (Map.Entry<String, JsonElement> entry : entrySet) {
                if (contador % 2 != 0) {
                    fila += "  " + entry.getValue().getAsString();
                    if (cadena.equals(entry.getValue().getAsString())) {
                        existe = true;
                    }
                }
                contador++;
                // entry.getValue();

            }
        }


        return existe;
    }

    private boolean comprobarMatricula(String cadena, String dir) {
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
