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
import cn.pedant.SweetAlert.SweetAlertDialog


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

        val prefs = getSharedPreferences("user_data", MODE_PRIVATE)
        val idSensor = prefs.getInt("id_sensor", 0).toString()

        if (idSensor == "0") {
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Error")
                .setContentText("No hay sensor asignado")
                .setConfirmText("Aceptar")
                .show()
            return
        }

        val request = object : StringRequest(Method.POST, url,
            { response ->
                if(response.trim() == "OK"){
                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("¡Solicitud enviada!")
                        .setConfirmText("Aceptar")
                        .show()
                } else {
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error servidor")
                        .setContentText(response)
                        .setConfirmText("Aceptar")
                        .show()
                }
            },
            { error ->
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("No se pudo enviar la solicitud: ${error.message}")
                    .setConfirmText("Aceptar")
                    .show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val p = HashMap<String, String>()
                p["id_sensor"] = idSensor
                return p
            }
        }

        SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Confirmar")
            .setContentText("¿Deseas enviar la solicitud de acceso?")
            .setConfirmText("Sí")
            .setCancelText("No")
            .setConfirmClickListener { sDialog ->
                sDialog.dismissWithAnimation()
                Volley.newRequestQueue(this).add(request)
            }
            .setCancelClickListener { sDialog ->
                sDialog.dismissWithAnimation()
            }
            .show()
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
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("No se pudo obtener el estado de la barrera")
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
