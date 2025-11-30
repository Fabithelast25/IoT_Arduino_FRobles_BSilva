package com.example.movilarduino

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley

class ListarSensores : AppCompatActivity() {

    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_sensores)

        // Guardamos referencia al ListView para usarlo después
        listView = findViewById(R.id.listaSensores)

        // Cuando se toque un item, abrimos pantalla de edición
        listView.setOnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position) as HashMap<String, String>

            val intent = Intent(this, EditarSensor::class.java)
            intent.putExtra("dueno", item["nombre"])
            intent.putExtra("codigo", item["codigo"]!!.removePrefix("Código: "))
            intent.putExtra("tipo", item["tipo"]!!.removePrefix("Tipo: "))
            intent.putExtra("estado", item["estado"]!!.removePrefix("Estado: "))
            intent.putExtra("fechaAlta", item["fechaAlta"]!!.removePrefix("Alta: "))
            intent.putExtra("fechaBaja", item["fechaBaja"]!!.removePrefix("Baja: "))
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        cargarSensores() // Aquí refrescamos la lista SIEMPRE
    }

    private fun cargarSensores() {
        // Obtenemos el ListView y el id del departamento
        val prefs = getSharedPreferences("user_data", MODE_PRIVATE)
        val idDepartamento = prefs.getInt("id_departamento", 0)

        // URL con el id del departamento
        val url = "http://98.95.8.72/consulta_sensores.php?id_departamento=$idDepartamento"

        // Cola de solicitudes para Volley
        val queue = Volley.newRequestQueue(this)

        // Solicitud JSON al servidor
        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                // Lista donde guardaremos los sensores
                val lista = ArrayList<HashMap<String, String>>()

                // Recorrer respuesta
                for (i in 0 until response.length()) {
                    val item = response.getJSONObject(i)

                    val map = HashMap<String, String>()
                    map["nombre"] = item.getString("nombres") + " " + item.getString("apellidos")
                    map["codigo"] = "Código: " + item.getString("codigo_sensor")
                    map["tipo"] = "Tipo: " + item.getString("tipo")
                    map["estado"] = "Estado: " + item.getString("estado")
                    map["fechaAlta"] = "Alta: " + item.getString("fecha_alta")
                    map["fechaBaja"] = if (item.isNull("fecha_baja")) "Baja: " else "Baja: " + item.getString("fecha_baja")

                    lista.add(map)
                }

                // Adaptador para mostrar los datos
                val adapter = SimpleAdapter(
                    this,
                    lista,
                    R.layout.item_sensor,
                    arrayOf("nombre", "codigo", "tipo", "estado", "fechaAlta", "fechaBaja"),
                    intArrayOf(R.id.txtNombre, R.id.txtCodigo, R.id.txtTipo, R.id.txtEstado, R.id.txtFechaAlta, R.id.txtFechaBaja)
                )

                listView.adapter = adapter
            },
            {
                println("Error cargando sensores")
            }
        )

        queue.add(request)
    }
}

