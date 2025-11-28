package com.example.movilarduino

import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class AgregarUsuario : AppCompatActivity() {
    private var idUsuarioActual: Int = 0
    private var idDepartamentoActual: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_agregar_usuario)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // === Recuperar datos del usuario desde SharedPreferences ===
        val prefs = getSharedPreferences("user_data", MODE_PRIVATE)
        idUsuarioActual = prefs.getInt("id_usuario", 0)
        idDepartamentoActual = prefs.getInt("id_departamento", 0)
        Toast.makeText(this, "id usuario actual: $idUsuarioActual", Toast.LENGTH_LONG).show()
        Toast.makeText(this, "id dpto actual: $idDepartamentoActual", Toast.LENGTH_LONG).show()

        // === Configurar Spinner ===
        val spinner: Spinner = findViewById(R.id.spinnerTipoSensor)
        val opciones = arrayOf("LLAVERO", "TARJETA")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // === Botón Registrar ===
        val btnRegistrar = findViewById<Button>(R.id.button6)
        btnRegistrar.setOnClickListener {
            registrarUsuarioYSensor()
        }
    }

    private fun registrarUsuarioYSensor() {
        // Leer datos de los EditText y Spinner
        val nombres = findViewById<EditText>(R.id.editTextText).text.toString().trim()
        val apellidos = findViewById<EditText>(R.id.editTextText2).text.toString().trim()
        val email = findViewById<EditText>(R.id.editTextText3).text.toString().trim()
        val telefono = findViewById<EditText>(R.id.editTextPhone).text.toString().trim()
        val rut = findViewById<EditText>(R.id.editTextText5).text.toString().trim()
        val codigoSensor = findViewById<EditText>(R.id.editTextText6).text.toString().trim()
        val tipoSensor = findViewById<Spinner>(R.id.spinnerTipoSensor).selectedItem.toString()

        // Validar campos vacíos
        if (nombres.isEmpty() || apellidos.isEmpty() || email.isEmpty() ||
            telefono.isEmpty() || rut.isEmpty() || codigoSensor.isEmpty()
        ) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val password = generarPassword(8) // contraseña aleatoria
        val url = "http://98.95.8.72/registrar_usuario_sensor.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                try {
                    val json = JSONObject(response)
                    Toast.makeText(
                        this,
                        json.getString("message") + "\nContraseña: ${json.getString("password_generada")}",
                        Toast.LENGTH_LONG
                    ).show()
                } catch (e: Exception) {
                    Toast.makeText(this, "Error de formato JSON: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error de conexión: ${error.message}", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()

                // Datos del usuario
                params["nombres"] = nombres
                params["apellidos"] = apellidos
                params["email"] = email
                params["telefono"] = telefono
                params["rut"] = rut
                params["password"] = password
                params["estado_usuario"] = "ACTIVO"
                params["rol"] = "Residente"
                params["id_departamento_usuario"] = idDepartamentoActual.toString()

                // Datos del sensor
                params["codigo_sensor"] = codigoSensor
                params["tipo_sensor"] = tipoSensor
                params["estado_sensor"] = "ACTIVO"
                params["id_usuario_sensor"] = idUsuarioActual.toString()

                return params
            }
        }

        Volley.newRequestQueue(this).add(stringRequest)
    }

    // Función para generar contraseña aleatoria
    private fun generarPassword(longitud: Int): String {
        val caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..longitud)
            .map { caracteres.random() }
            .joinToString("")
    }

}
