package com.example.vehiculos_di;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.service.autofill.CharSequenceTransformation;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Coches extends AppCompatActivity {
    Button buttonVolver, buttonModificar, buttonInsertar, buttonBorrar, buttonCSV, buttonJSON, buttonXML;
    EditText matricula, marca, color;
    ListView lista;
    boolean existe = false;

    static String direccion = "/web/listadoCSV.php";
    // static String SERVIDOR = "http://192.168.100.19:8080";//casa
    static String SERVIDOR = "http://192.168.0.111:8080";//clase
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coches);
        buttonVolver = findViewById(R.id.buttonVolver);

        buttonModificar = findViewById(R.id.buttonModCoches);
        buttonInsertar = findViewById(R.id.buttonInsCoches);
        buttonBorrar = findViewById(R.id.buttonBorCoches);
        buttonCSV = findViewById(R.id.buttonMosCoches);
        buttonJSON = findViewById(R.id.buttonMosCoches2);
        buttonXML = findViewById(R.id.buttonMosCoches3);

        matricula = findViewById(R.id.editTextMatrCoches);
        marca = findViewById(R.id.editTextMarca);
        color = findViewById(R.id.editTextColor);
        lista = findViewById(R.id.ListaCoches);

       /* DescargarCSV descargarCSV = new DescargarCSV();
        descargarCSV.execute(direccion);*/

        buttonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(MainActivity.NOMBRE, "Vuelvo de coches");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        buttonCSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DescargarCSV descargarCSV = new DescargarCSV();
                descargarCSV.execute(direccion);

            }
        });
        buttonInsertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (matricula.getText().toString().equals("")) {
                    Toast.makeText(Coches.this, "Debe introducir al menos el campo matrícula", Toast.LENGTH_SHORT).show();
                } else {
                    Comprobar comprobar = new Comprobar("insertar");
                    comprobar.execute(direccion);


                }

            }
        });
        buttonModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (matricula.getText().toString().equals("")) {
                    Toast.makeText(Coches.this, "Debe introducir al menos el campo matrícula", Toast.LENGTH_SHORT).show();
                } else {
                    Comprobar comprobar = new Comprobar("modificar");
                    comprobar.execute(direccion);


                }

            }
        });
        buttonBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (matricula.getText().toString().equals("")) {
                    Toast.makeText(Coches.this, "Debe introducir el campo matrícula", Toast.LENGTH_SHORT).show();
                } else {
                    Comprobar comprobar = new Comprobar("eliminar");
                    comprobar.execute(direccion);

                }

            }
        });
        buttonXML.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DescargarXML descargarXML = new DescargarXML();
                descargarXML.execute("/web/listadoXMLCoches.php");
            }
        });
        buttonJSON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DescargarJSON descargarJSON = new DescargarJSON();
                descargarJSON.execute("/web/listadoJSONCoches.php");
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

        URL url = null;
        HttpURLConnection httpURLConnection = null;


        try {
            url = new URL(script);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

                String linea = "";

                while ((linea = br.readLine()) != null) {
                    contenido += linea + "\n";
                }

                br.close();
                inputStream.close();


            }
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    private class Comprobar extends AsyncTask<String, Void, Void> {
        String total = "";
        String opcion;

        public Comprobar(String opcion) {
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
                    Log.i("EXISTE", campos[0] + " " + existe);
                } else {


                    Log.i("NO EXISTE", campos[0] + " " + existe);

                }

            }
            if (!existe) {
                switch (opcion) {
                    case "insertar":
                        String dir = "/web/insertarPOSTCoches.php";
                        Insertar(matricula.getText().toString(), marca.getText().toString(), color.getText().toString(), dir);
                        Toast.makeText(Coches.this, "Insertando...", Toast.LENGTH_SHORT).show();
                        break;
                    case "modificar":
                        Toast.makeText(Coches.this, "No existe la matrícula que desea modificar", Toast.LENGTH_SHORT).show();
                        break;
                    case "eliminar":
                        Toast.makeText(Coches.this, "No existe la matrícula que desea eliminar", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }


            } else {
                switch (opcion) {
                    case "insertar":
                        Toast.makeText(Coches.this, "No se puede insertar una matrícula ya existente", Toast.LENGTH_SHORT).show();
                        break;
                    case "modificar":
                        String dir = "/web/updateGETCoches.php";
                        Modificar(matricula.getText().toString(), marca.getText().toString(), color.getText().toString(), dir);
                        Toast.makeText(Coches.this, "Modificando...", Toast.LENGTH_SHORT).show();
                        break;
                    case "eliminar":
                        String dire = "/web/deleteGETCoches.php";
                        Eliminar(matricula.getText().toString(), dire);
                        Toast.makeText(Coches.this, "Eliminando...", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;

                }
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

    private class DescargarXML extends AsyncTask<String, Void, Void> {
        List<String> list = new ArrayList<String>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Coches.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle("Descargando la información de la red en xml.");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ArrayAdapter<String> adapter;

            adapter = new ArrayAdapter<String>(getApplicationContext(),
                    R.layout.support_simple_spinner_dropdown_item, list);
            lista.setAdapter(adapter);

            progressDialog.dismiss();
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

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document document = db.parse(new URL(url).openStream());

                Element raiz = document.getDocumentElement();
                NodeList hijos = raiz.getChildNodes();

                for (int i = 0; i < hijos.getLength(); i++) {

                    Node nodo = hijos.item(i);
                    if (nodo instanceof Element) {
                        NodeList nietos = nodo.getChildNodes();

                        String registro = "";
                        for (int j = 0; j < nietos.getLength(); j++) {
                            if (nietos.item(j) instanceof Element) {
                                registro += " " + nietos.item(j).getNodeName() + " " + nietos.item(j).getTextContent();
                            }
                        }


                        list.add(registro);
                    }

                }

            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
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
            String fila1 = "Matrícula\t\t\tMarca\t\t\t\t\tColor";
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


                }

                list.add(fila);


            }


            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Coches.this);
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

}
