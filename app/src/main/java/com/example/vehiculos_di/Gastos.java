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
import java.net.HttpURLConnection;
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
    static String Comprobar_Matricula = "/web/listadoCSV.php";
    static String SERVIDOR = "http://192.168.100.19:8080";
    //static String SERVIDOR = "http://192.168.0.111:8080";//Clase
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

        DescargarJSON descargarJSON = new DescargarJSON();
        descargarJSON.execute(direccion);

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
            }
        });
        buttonInsertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (matricula.getText().toString().equals("")) {
                    Toast.makeText(Gastos.this, "Debe introducir al menos el campo matrícula", Toast.LENGTH_SHORT).show();
                } else {

                    Comprobar comprobar = new Comprobar("insertar");
                    comprobar.execute(direccion);
                }

            }
        });
        buttonModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id.getText().toString().equals("")) {
                    Toast.makeText(Gastos.this, "Debe introducir el campo ID", Toast.LENGTH_SHORT).show();
                } else {
                    Comprobar comprobar = new Comprobar("modificar");
                    comprobar.execute(direccion);

                }
            }
        });
        buttonBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id.getText().toString().equals("")) {
                    Toast.makeText(Gastos.this, "Debe introducir el campo ID", Toast.LENGTH_SHORT).show();
                } else {

                    Comprobar comprobar = new Comprobar("eliminar");
                    comprobar.execute(direccion);
                }
            }
        });

    }

    private boolean isNumeric(String cadena) {
        try {
            Integer.parseInt(cadena);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
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

    private void Modificar(String id, String Matricula, String concepto, String valor, String dir) {
        String script = SERVIDOR + dir + "?id=" + id + "&Matricula=" + Matricula + "&concepto=" + concepto + "&valor=" + valor;
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

    private class Comprobar extends AsyncTask<String, Void, Void> {

        String opcion;
        String contenido = "";


        public Comprobar(String opcion) {
            this.opcion = opcion;
        }

        @Override
        protected Void doInBackground(String... strings) {
            String script = strings[0];
            String url = SERVIDOR + script;


            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

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


            return null;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            boolean existe = false;
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
                        if (id.getText().toString().equals(entry.getValue().getAsString())) {
                            existe = true;
                        }
                    }
                    contador++;

                }


            }

            if (!existe) {
                switch (opcion) {
                    case "insertar":

                        CompMat compMat = new CompMat("insertar");
                        compMat.execute(Comprobar_Matricula);

                        break;
                    case "modificar":
                        Toast.makeText(Gastos.this, "No existe el id del gasto que desea modificar", Toast.LENGTH_SHORT).show();
                        break;
                    case "eliminar":
                        Toast.makeText(Gastos.this, "No existe el id del gasto que desea eliminar", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }

            } else {
                switch (opcion) {
                    case "insertar":
                        CompMat compMat1 = new CompMat("insertar");
                        compMat1.execute(Comprobar_Matricula);
                        break;
                    case "modificar":

                        CompMat compMat = new CompMat("modificar");
                        compMat.execute(Comprobar_Matricula);

                        break;
                    case "eliminar":

                        CompMat compMat2 = new CompMat("eliminar");
                        compMat2.execute(Comprobar_Matricula);

                        break;
                    default:
                        break;

                }
            }

        }


    }

    private class CompMat extends AsyncTask<String, Void, Void> {
        String total = "";
        boolean existe;
        String opcion;

        public CompMat(String opcion) {
            this.opcion = opcion;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            existe = false;
            String[] lineas = total.split("\n");

            for (String lin : lineas) {
                String[] campos = lin.split(",");
                String dato = " MATRÍCULA: " + campos[0];
                dato += " MARCA: " + campos[1];
                dato += " COLOR: " + campos[2];


                if (matricula.getText().toString().equals(campos[0])) {


                    existe = true;

                }
            }
            if (existe) {
                switch (opcion) {
                    case "insertar":
                        String dir = "/web/insertarPOSTGastos.php";
                        Insertar(matricula.getText().toString(), concepto.getText().toString(), valor.getText().toString(), dir);
                        Toast.makeText(Gastos.this, "Insertando...", Toast.LENGTH_SHORT).show();

                        break;
                    case "modificar":
                        String dir1 = "/web/updateGETGastos.php";
                        Modificar(id.getText().toString(), matricula.getText().toString(), concepto.getText().toString(), valor.getText().toString(), dir1);
                        Toast.makeText(Gastos.this, "Modificando...", Toast.LENGTH_SHORT).show();

                        break;
                    case "eliminar":
                        String dir2 = "/web/deleteGETGastos.php";
                        Eliminar(id.getText().toString(), dir2);
                        Toast.makeText(Gastos.this, "Eliminando...", Toast.LENGTH_SHORT).show();
                        break;

                }

            } else {
                Toast.makeText(Gastos.this, "La matrícula introducida no existe", Toast.LENGTH_SHORT).show();

            }


        }

        @Override
        protected Void doInBackground(String... strings) {
            String script = strings[0];

            URL url = null;
            HttpURLConnection httpURLConnection = null;


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


}
