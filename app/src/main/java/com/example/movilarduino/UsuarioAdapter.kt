package com.example.movilarduino

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

// Si no tienes la data class Usuario, créala (ver más abajo)
class UsuarioAdapter(
    private val activity: Activity,
    private val usuarios: List<Usuario>
) : ArrayAdapter<Usuario>(activity, 0, usuarios) {

    // ViewHolder para evitar llamadas repetidas a findViewById
    private class ViewHolder(
        val txtNombre: TextView,
        val txtApellido: TextView,
        val txtEmail: TextView,
        val txtEstado: TextView,
        val txtTelefono: TextView,
        val txtRut: TextView
    )

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val usuario = usuarios[position]
        var rowView = convertView
        val holder: ViewHolder

        if (rowView == null) {
            val inflater = LayoutInflater.from(activity)
            rowView = inflater.inflate(R.layout.item_usuario, parent, false)

            holder = ViewHolder(
                rowView.findViewById(R.id.txtNombre),
                rowView.findViewById(R.id.txtApellido),
                rowView.findViewById(R.id.txtEmail),
                rowView.findViewById(R.id.txtEstado),
                rowView.findViewById(R.id.txtTelefono),
                rowView.findViewById(R.id.txtRut)
            )

            rowView.tag = holder
        } else {
            holder = rowView.tag as ViewHolder
        }

        // Asignar valores a los TextViews
        holder.txtNombre.text = usuario.nombre ?: ""
        holder.txtApellido.text = usuario.apellido ?: ""
        holder.txtEmail.text = usuario.email ?: ""
        holder.txtEstado.text = usuario.estado ?: ""
        holder.txtTelefono.text = usuario.telefono ?: ""
        holder.txtRut.text = usuario.rut ?: ""

        return rowView!!
    }
}