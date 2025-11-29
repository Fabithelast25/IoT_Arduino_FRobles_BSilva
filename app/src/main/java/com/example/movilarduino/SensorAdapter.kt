package com.example.movilarduino

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class SensorAdapter(
    context: Context,
    private val sensores: ArrayList<Sensor>
) : ArrayAdapter<Sensor>(context, 0, sensores) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Infla la fila item_sensor.xml si no existe aún
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_sensor, parent, false)

        // Obtiene el sensor actual
        val sensor = sensores[position]

        // Referencias a las columnas
        val txtNombre = view.findViewById<TextView>(R.id.txtNombre)
        val txtCodigo = view.findViewById<TextView>(R.id.txtCodigo)
        val txtEstado = view.findViewById<TextView>(R.id.txtEstado)
        val txtTipo = view.findViewById<TextView>(R.id.txtTipo)
        val txtFechaAlta = view.findViewById<TextView>(R.id.txtFechaAlta)
        val txtFechaBaja = view.findViewById<TextView>(R.id.txtFechaBaja)

        // Construye Nombre + Apellido
        val nombreCompleto = "${sensor.nombre ?: ""} ${sensor.apellido ?: ""}"

        // Asigna valores a las columnas (SIN etiquetas)
        txtNombre.text = nombreCompleto.trim()
        txtCodigo.text = sensor.codigo_sensor ?: ""
        txtEstado.text = sensor.estado ?: ""
        txtTipo.text = sensor.tipo ?: ""
        txtFechaAlta.text = sensor.fecha_alta ?: ""

        // Si fecha_baja es null o vacía, no muestra nada
        txtFechaBaja.text =
            if (sensor.fecha_baja.isNullOrBlank() || sensor.fecha_baja == "null") ""
            else sensor.fecha_baja

        return view
    }
}
