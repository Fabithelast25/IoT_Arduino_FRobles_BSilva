package com.example.movilarduino

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley

class ListarUsuarios : AppCompatActivity() {

    private lateinit var listView: ListView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_usuarios)

        listView = findViewById(R.id.listaUsuarios)


        listView.setOnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position) as HashMap<String, String>

            val intent = Intent(this, EditarUsuario::class.java)

            intent.putExtra("id_usuario", item["id_usuario"])
            intent.putExtra("nombres", item["raw_nombres"])
            intent.putExtra("apellidos", item["raw_apellidos"])
            intent.putExtra("rut", item["raw_rut"])
            intent.putExtra("email", item["raw_email"])
            intent.putExtra("telefono", item["raw_telefono"])
            intent.putExtra("estado", item["raw_estado"])


            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        cargarUsuarios()
    }

    private fun cargarUsuarios() {
        val prefs = getSharedPreferences("user_data", MODE_PRIVATE)
        val idDepartamento = prefs.getInt("id_departamento", 0)
        val url = "http://98.95.8.72/consulta_usuarios.php?id_departamento=$idDepartamento"

        val queue = Volley.newRequestQueue(this)

        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->

                val lista = ArrayList<HashMap<String, String>>()

                for (i in 0 until response.length()) {
                    val item = response.getJSONObject(i)

                    val map = HashMap<String, String>()
                    map["id_usuario"] = item.getString("id_usuario")
                    map["nombres"] = "Nombre(s): " + item.getString("nombres")
                    map["apellidos"] = "Apellido(s): " + item.getString("apellidos")
                    map["rut"] = "RUT: " + item.getString("rut")
                    map["email"] = "Email: " + item.getString("email")
                    map["telefono"] = "Teléfono: " + item.getString("telefono")
                    map["estado"] = "Estado: " + item.getString("estado")

                    // Para pasar datos reales sin prefijos
                    map["raw_nombres"] = item.getString("nombres")
                    map["raw_apellidos"] = item.getString("apellidos")
                    map["raw_rut"] = item.getString("rut")
                    map["raw_email"] = item.getString("email")
                    map["raw_telefono"] = item.getString("telefono")
                    map["raw_estado"] = item.getString("estado")

                    lista.add(map)
                }

                val adapter = SimpleAdapter(
                    this,
                    lista,
                    R.layout.item_usuario,
                    arrayOf("nombres", "apellidos", "rut", "email", "telefono", "estado"),
                    intArrayOf(
                        R.id.txtNombreUsuario,
                        R.id.txtApellidoUsuario,
                        R.id.txtRutUsuario,
                        R.id.txtEmailUsuario,
                        R.id.txtTelefonoUsuario,
                        R.id.txtEstadoUsuario
                    )
                )

                listView.adapter = adapter
            },
            {
                println("Error cargando usuarios")
            }
        )

        queue.add(request)
    }
}
