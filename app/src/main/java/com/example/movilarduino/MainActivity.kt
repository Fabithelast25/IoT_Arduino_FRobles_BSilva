package com.example.movilarduino

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

private lateinit var btnIngresar: Button
private lateinit var editEmail: EditText
private lateinit var editPassword: EditText

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Vincular elementos del XML
        btnIngresar = findViewById(R.id.buttonLogin)
        editEmail = findViewById(R.id.editTextEmailAddress)
        editPassword = findViewById(R.id.editTextPassword)

        btnIngresar.setOnClickListener {
            iniciarSesion()
        }
    }

    private fun iniciarSesion() {

        val email = editEmail.text.toString().trim()
        val password = editPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Ingrese email y contraseña", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "http://98.95.8.72/login.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->

                try {
                    val json = JSONObject(response)

                    if (json.getBoolean("success")) {

                        Toast.makeText(this, "Bienvenido!", Toast.LENGTH_LONG).show()

                        // Guardar los IDs en SharedPreferences
                        val prefs = getSharedPreferences("user_data", MODE_PRIVATE)
                        prefs.edit().apply {
                            putInt("id_usuario", json.getInt("id_usuario"))
                            putInt("id_departamento", json.getInt("id_departamento"))
                            apply()
                        }

                        // Ir al menú
                        val intent = Intent(this, MenuAdministrador::class.java)
                        startActivity(intent)

                    } else {
                        Toast.makeText(this, json.getString("message"), Toast.LENGTH_LONG).show()
                    }

                } catch (e: Exception) {
                    Toast.makeText(this, "Error de formato JSON: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error de conexión: ${error.message}", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["email"] = email
                params["password"] = password
                return params
            }
        }

        Volley.newRequestQueue(this).add(stringRequest)
    }
}
