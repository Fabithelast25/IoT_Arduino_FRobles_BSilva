package com.example.movilarduino

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import java.text.Normalizer

// Declara el ListView que mostrará los sensores
private lateinit var listado: ListView

// Lista donde se almacenarán objetos Sensor
private lateinit var listaSensor: ArrayList<Sensor>

// Lista para filtrar sensores (más adelante si quieres buscar)
private lateinit var listaFiltrada: ArrayList<Sensor>

// Adaptador encargado de inflar cada ítem de la lista
private lateinit var adapter: SensorAdapter

// Cola de peticiones Volley
private lateinit var dato: RequestQueue

class ListarSensores : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge() // Ajusta el layout para ocupar toda la pantalla
        setContentView(R.layout.activity_listar_sensores)

        // Ajusta margenes según la barra del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        listado = findViewById(R.id.listaSensores) // Vincula el ListView del layout
        dato = Volley.newRequestQueue(this)        // Inicializa la cola de Volley

        listaSensor = ArrayList()   // Crea la lista vacía de sensores
        listaFiltrada = ArrayList() // Lista auxiliar para filtros

        cargarLista() // Llama al metodo que consulta la API
    }

    private fun cargarLista() {
        listaSensor.clear() // Limpia la lista antes de cargar nuevos datos

        val url = "http://98.95.8.72/consulta_sensores.php" // URL del backend

        // Crea la petición GET a la URL
        val request = StringRequest(
            Request.Method.GET, url,
            { response ->               // Si la conexión es exitosa
                try {
                    val json = JSONArray(response) // Convierte la respuesta en JSON

                    for (i in 0 until json.length()) { // Recorre el JSON
                        val sensor = json.getJSONObject(i) // Obtiene un objeto por vez

                        // Extrae cada campo del sensor desde el JSON
                        val nombre = sensor.getString("nombres")
                        val apellido = sensor.getString("apellidos")
                        val codigo = sensor.getString("codigo_sensor")
                        val estado = sensor.getString("estado")
                        val tipo = sensor.getString("tipo")
                        val fechaAlta = sensor.getString("fecha_alta")
                        val fechaBaja = sensor.optString("fecha_baja", null)

                        // Crea un objeto Sensor con los datos extraídos
                        val sensorObj = Sensor(
                            nombre = nombre,
                            apellido = apellido,
                            codigo_sensor = codigo,
                            estado = estado,
                            tipo = tipo,
                            fecha_alta = fechaAlta,
                            fecha_baja = fechaBaja
                        )

                        listaSensor.add(sensorObj) // Lo agrega a la lista
                    }

                    adapter = SensorAdapter(this, listaSensor) // Crea el adaptador
                    listado.adapter = adapter                 // Asigna adaptador a la vista

                } catch (e: Exception) {
                    Toast.makeText(this, "Error parseando JSON: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error de conexión: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )

        dato.add(request) // Agrega la petición a la cola de Volley
    }

    private fun normalizar(texto: String): String {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
            .replace("[^\\p{ASCII}]".toRegex(), "")
            .lowercase()
    }
}