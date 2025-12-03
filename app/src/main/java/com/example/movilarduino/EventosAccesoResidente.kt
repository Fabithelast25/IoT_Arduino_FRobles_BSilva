package com.example.movilarduino

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import cn.pedant.SweetAlert.SweetAlertDialog


class EventosAccesoResidente : AppCompatActivity() {

    private lateinit var listView: ListView
    private val handler = Handler(Looper.getMainLooper())
    private val intervalo = 3000L // 3 segundos para actualizar
    private val listaEventos = ArrayList<MutableMap<String, String>>()
    private lateinit var adapter: BaseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventos_acceso)

        listView = findViewById(R.id.listViewEventos)

        // Crear el adapter global
        adapter = object : BaseAdapter() {
            override fun getCount() = listaEventos.size
            override fun getItem(position: Int) = listaEventos[position]
            override fun getItemId(position: Int) = position.toLong()

            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val rowView = convertView ?: LayoutInflater.from(this@EventosAccesoResidente)
                    .inflate(R.layout.item_evento_residente, parent, false)

                val usuario = rowView.findViewById<TextView>(R.id.txtUsuario)
                val sensor = rowView.findViewById<TextView>(R.id.txtSensor)
                val evento = rowView.findViewById<TextView>(R.id.txtEvento)
                val fecha = rowView.findViewById<TextView>(R.id.txtFecha)
                val resultado = rowView.findViewById<TextView>(R.id.txtResultado)

                val item = listaEventos[position]

                usuario.text = "Usuario: ${item["usuario"]}"
                sensor.text = "Sensor: ${item["sensor"]}"
                evento.text = "Evento: ${item["evento"]}"
                fecha.text = "Fecha: ${item["fecha"]}"
                resultado.text = "Resultado: ${item["resultado"]}"

                return rowView
            }
        }

        listView.adapter = adapter
        iniciarActualizacionAutomatica()
    }

    private fun iniciarActualizacionAutomatica() {
        handler.post(object : Runnable {
            override fun run() {
                obtenerHistorial()
                handler.postDelayed(this, intervalo)
            }
        })
    }


    private fun obtenerHistorial() {
        val prefs = getSharedPreferences("user_data", MODE_PRIVATE)
        val idDepartamento = prefs.getInt("id_departamento", 0)

        val url = "http://98.95.8.72/obtener_solicitudes.php?id_departamento=$idDepartamento"


        val request = StringRequest(Request.Method.GET, url,
            { response ->
                try {
                    val jsonArray = JSONArray(response)
                    listaEventos.clear()

                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        val item = mutableMapOf<String, String>()
                        item["id_evento"] = obj.getString("id_evento")
                        item["usuario"] = obj.getString("usuario")
                        item["sensor"] = obj.getString("sensor")
                        item["evento"] = obj.getString("evento")
                        item["fecha"] = obj.getString("fecha")
                        item["resultado"] = obj.getString("resultado")
                        listaEventos.add(item)
                    }

                    adapter.notifyDataSetChanged()
                } catch (e: Exception) {
                    SweetAlertDialog(this@EventosAccesoResidente, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText("Error parseando JSON: ${e.message}")
                        .setConfirmText("Aceptar")
                        .show()
                }
            },
            {
                SweetAlertDialog(this@EventosAccesoResidente, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("Error al obtener eventos")
                    .setConfirmText("Aceptar")
                    .show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }


    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
