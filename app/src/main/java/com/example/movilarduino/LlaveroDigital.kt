package com.example.movilarduino

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class LlaveroDigital : AppCompatActivity() {

    private lateinit var imgEstado: ImageView
    private lateinit var btnSolicitar: Button
    private val handler = Handler(Looper.getMainLooper())
    private val intervalo = 3000L // consulta cada 3 segundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_llavero_digital)

        imgEstado = findViewById(R.id.imgEstado)
        btnSolicitar = findViewById(R.id.btnSolicitar)

        iniciarActualizacionAutomatica()

        btnSolicitar.setOnClickListener {
            enviarSolicitudAcceso()
        }
    }

    private fun enviarSolicitudAcceso() {
        val url = "http://98.95.8.72/registrar_evento.php"

        // Recupera id del usuario y sensor guardados en login
        val prefs = getSharedPreferences("user_data", MODE_PRIVATE)
        val idUsuario = prefs.getInt("id_usuario", 0).toString()
        val idSensor = prefs.getInt("id_sensor", 0).toString()

        val request = object : StringRequest(Method.POST, url,
            { Toast.makeText(this, "Solicitud enviada", Toast.LENGTH_SHORT).show() },
            { Toast.makeText(this, "Error enviando solicitud", Toast.LENGTH_SHORT).show() }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val p = HashMap<String, String>()
                p["id_usuario"] = idUsuario
                p["id_sensor"] = idSensor
                p["tipo_evento"] = "SOLICITUD_ACCESO"
                p["resultado"] = "PENDIENTE"
                return p
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun iniciarActualizacionAutomatica() {
        handler.post(object : Runnable {
            override fun run() {
                obtenerEstado()
                handler.postDelayed(this, intervalo)
            }
        })
    }

    private fun obtenerEstado() {
        val url = "http://98.95.8.72/get_estado.php"

        val request = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                val estado = response.getString("estado_barrera")

                if (estado == "ABIERTO") {
                    imgEstado.setImageResource(R.drawable.barrera_abierta)
                } else {
                    imgEstado.setImageResource(R.drawable.barrera_cerrada)
                }
            },
            {
                Toast.makeText(this, "Error obteniendo estado", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
