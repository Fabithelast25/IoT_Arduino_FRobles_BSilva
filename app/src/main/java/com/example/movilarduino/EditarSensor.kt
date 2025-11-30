package com.example.movilarduino

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

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
            val url = "http://98.95.8.72/editar_sensor.php?codigo=$codigo&estado=$nuevoEstado"
            val queue = Volley.newRequestQueue(this)

            val request = StringRequest(Request.Method.GET, url, { response ->

                Toast.makeText(this, "Actualizado correctamente", Toast.LENGTH_SHORT).show()

                // Devuelve resultado a la Activity anterior
                val intent = Intent()
                setResult(RESULT_OK, intent)
                finish()

            }, {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
            })

            queue.add(request)
        }

        val btnEliminar = findViewById<Button>(R.id.btnEliminar)

        // 1) Cuando se pulse el botón eliminar
        btnEliminar.setOnClickListener {

            // 2) Obtiene el código del sensor
            val codigo = intent.getStringExtra("codigo") ?: ""

            // 3) URL hacia el PHP de eliminación
            val url = "http://98.95.8.72/eliminar_sensor.php?codigo_sensor=$codigo"

            // 4) Creamos la cola de peticiones
            val queue = Volley.newRequestQueue(this)

            // 5) Petición GET hacia el PHP
            val request = StringRequest(
                Request.Method.GET, url,
                {
                    // 6) Mensaje de éxito
                    Toast.makeText(this, "Sensor eliminado correctamente", Toast.LENGTH_LONG).show()

                    // 7) Cerramos esta pantalla y volvemos a la lista
                    finish()
                },
                {
                    // 8) Mensaje de error
                    Toast.makeText(this, "Error al eliminar", Toast.LENGTH_LONG).show()
                }
            )

            // 9) Ejecutamos la petición
            queue.add(request)
        }
    }
}
