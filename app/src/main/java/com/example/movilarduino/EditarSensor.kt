package com.example.movilarduino

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import cn.pedant.SweetAlert.SweetAlertDialog


class EditarSensor : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_sensor)

        // 1️⃣ Referencias a los elementos del layout
        val txtDueno = findViewById<TextView>(R.id.txtDueno)
        val txtCodigo = findViewById<TextView>(R.id.txtCodigo)
        val txtTipo = findViewById<TextView>(R.id.txtTipo)
        val txtFechaAlta = findViewById<TextView>(R.id.txtFechaAlta)
        val txtFechaBaja = findViewById<TextView>(R.id.txtFechaBaja)
        val spinnerEstado = findViewById<Spinner>(R.id.spinnerEstado)
        val btnGuardar = findViewById<Button>(R.id.btnGuardar)

        // 2️⃣ Recibir datos enviados desde la otra Activity
        val dueno = intent.getStringExtra("dueno")
        val codigo = intent.getStringExtra("codigo")
        val tipo = intent.getStringExtra("tipo")
        val estadoActual = intent.getStringExtra("estado")
        val fechaAlta = intent.getStringExtra("fechaAlta")
        val fechaBaja = intent.getStringExtra("fechaBaja")

        // 3️⃣ Mostrar en pantalla los valores NO editables
        txtDueno.text = "Dueño: $dueno"
        txtCodigo.text = "Código: $codigo"
        txtTipo.text = "Tipo: $tipo"
        txtFechaAlta.text = "Fecha de alta: $fechaAlta"
        txtFechaBaja.text = "Fecha de baja: ${fechaBaja ?: "—"}"

        // 4️⃣ Llenar Spinner con opciones
        val estados = listOf("ACTIVO", "INACTIVO", "PERDIDO", "BLOQUEADO")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, estados)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEstado.adapter = adapter

        // 5️⃣ Seleccionar el estado actual del sensor
        spinnerEstado.setSelection(estados.indexOf(estadoActual))

        btnGuardar.setOnClickListener {
            val nuevoEstado = spinnerEstado.selectedItem.toString()

            // Alerta de confirmación
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("¿Deseas guardar los cambios?")
                .setContentText("Se actualizará el estado del sensor a $nuevoEstado")
                .setConfirmText("Sí")
                .setCancelText("No")
                .setConfirmClickListener { sDialog ->

                    val url = "http://98.95.8.72/editar_sensor.php?codigo=$codigo&estado=$nuevoEstado"
                    val queue = Volley.newRequestQueue(this)

                    val request = StringRequest(Request.Method.GET, url, { response ->

                        // Alerta de éxito
                        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("¡Sensor modificado!")
                            .setConfirmText("Aceptar")
                            .setConfirmClickListener {
                                it.dismissWithAnimation()
                                setResult(RESULT_OK)
                                finish()
                            }
                            .show()

                    }, {
                        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText("No se pudo actualizar el sensor")
                            .setConfirmText("Aceptar")
                            .show()
                    })

                    queue.add(request)
                    sDialog.dismissWithAnimation() // cerrar confirmación
                }
                .setCancelClickListener { sDialog ->
                    sDialog.dismissWithAnimation()
                }
                .show()
        }

        val btnEliminar = findViewById<Button>(R.id.btnEliminar)

        btnEliminar.setOnClickListener {

            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("¿Deseas eliminar este sensor?")
                .setContentText("Esta acción no se puede deshacer")
                .setConfirmText("Sí, eliminar")
                .setCancelText("Cancelar")
                .setConfirmClickListener { sDialog ->

                    val codigo = intent.getStringExtra("codigo") ?: ""
                    val url = "http://98.95.8.72/eliminar_sensor.php?codigo_sensor=$codigo"
                    val queue = Volley.newRequestQueue(this)

                    val request = StringRequest(Request.Method.GET, url,
                        {
                            SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("¡Sensor eliminado!")
                                .setConfirmText("Aceptar")
                                .setConfirmClickListener {
                                    it.dismissWithAnimation()
                                    finish()
                                }
                                .show()
                        },
                        {
                            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error")
                                .setContentText("No se pudo eliminar el sensor")
                                .setConfirmText("Aceptar")
                                .show()
                        })

                    queue.add(request)
                    sDialog.dismissWithAnimation()
                }
                .setCancelClickListener { sDialog ->
                    sDialog.dismissWithAnimation()
                }
                .show()
        }
    }
}
