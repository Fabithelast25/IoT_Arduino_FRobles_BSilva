package com.example.movilarduino

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
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

private lateinit var listado: ListView
private lateinit var listaUsuario: ArrayList<Usuario>
private lateinit var listaFiltrada: ArrayList<String>
private lateinit var adapter: UsuarioAdapter
private lateinit var dato: RequestQueue


class ListarUsuarios : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_listar_usuarios)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        listado = findViewById(R.id.listaUsuarios)
        dato = Volley.newRequestQueue(this)

        listaUsuario = ArrayList()
        listaFiltrada = ArrayList()

        cargarLista()
    }

    private fun cargarLista() {
        listaUsuario.clear()

        val url = "http://98.95.8.72/consulta_usuarios.php"
        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    val json = JSONArray(response)

                    for (i in 0 until json.length()) {
                        val usuario = json.getJSONObject(i)

                        val nombre = usuario.getString("nombres")
                        val apellido = usuario.getString("apellidos")
                        val email = usuario.getString("email")
                        val estado = usuario.getString("estado")
                        val telefono = usuario.getString("telefono")
                        val rut = usuario.getString("rut")

                        val usuarioObj = Usuario(
                            nombre = nombre,
                            apellido = apellido,
                            email = email,
                            estado = estado,
                            telefono = telefono,
                            rut = rut
                        )

                        listaUsuario.add(usuarioObj)
                    }

                    adapter = UsuarioAdapter(this, listaUsuario)
                    listado.adapter = adapter

                } catch (e: Exception) {
                    Toast.makeText(this, "Error parseando JSON: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error de conexión: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )

        dato.add(request)
    }

/*
    private fun filtrarDatos(query: String) {
        if (!::adapter.isInitialized) return

        listaFiltrada.clear()

        if (query.isEmpty()) {
            // Si no hay texto → restauramos toda la lista
            listaFiltrada.addAll(listaUsuario)
        } else {
            // Si hay texto → filtramos por nombre o apellido
            for (item in listaUsuario) {
                val textoNormalizado = normalizar(item)
                val queryNormalizada = normalizar(query)
                if (textoNormalizado.contains(queryNormalizada)) {
                    listaFiltrada.add(item)
                }
            }
        }

        adapter.notifyDataSetChanged()
    }
 */

    private fun normalizar(texto: String): String {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
            .replace("[^\\p{ASCII}]".toRegex(), "")
            .lowercase()
    }

}