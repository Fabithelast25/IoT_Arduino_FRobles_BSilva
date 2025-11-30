package com.example.movilarduino

import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cn.pedant.SweetAlert.SweetAlertDialog
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

        val prefs = getSharedPreferences("user_data", MODE_PRIVATE)
        idUsuarioActual = prefs.getInt("id_usuario", 0)
        idDepartamentoActual = prefs.getInt("id_departamento", 0)

        val spinner: Spinner = findViewById(R.id.spinnerTipoSensor)
        val opciones = arrayOf("LLAVERO", "TARJETA")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        findViewById<Button>(R.id.button6).setOnClickListener {
            registrarUsuarioYSensor()
        }
    }

    private fun registrarUsuarioYSensor() {
        val nombres = findViewById<EditText>(R.id.editTextText).text.toString().trim()
        val apellidos = findViewById<EditText>(R.id.editTextText2).text.toString().trim()
        val email = findViewById<EditText>(R.id.editTextText3).text.toString().trim()
        val telefono = findViewById<EditText>(R.id.editTextPhone).text.toString().trim()
        val rut = findViewById<EditText>(R.id.editTextText5).text.toString().trim()
        val codigoSensor = findViewById<EditText>(R.id.editTextText6).text.toString().trim()
        val tipoSensor = findViewById<Spinner>(R.id.spinnerTipoSensor).selectedItem.toString()

        // Validación
        if (nombres.isEmpty() || apellidos.isEmpty() || email.isEmpty() ||
            telefono.isEmpty() || rut.isEmpty() || codigoSensor.isEmpty()
        ) {
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Campos vacíos")
                .setContentText("Complete todos los campos antes de continuar.")
                .show()
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Email inválido")
                .setContentText("Ingrese un email con un formato válido")
                .show()
            return
        }

        val password = generarPassword(8)
        val url = "http://98.95.8.72/registrar_usuario_sensor.php"

        val request = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                try {
                    val json = JSONObject(response)

                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Usuario creado")
                        .setContentText(
                            json.getString("message")
                        )
                        .show()

                } catch (e: Exception) {
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error JSON")
                        .setContentText(e.message)
                        .show()
                }
            },
            { error ->
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error de conexión")
                    .setContentText(error.message ?: "Error desconocido")
                    .show()
            }) {

            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()

                params["nombres"] = nombres
                params["apellidos"] = apellidos
                params["email"] = email
                params["telefono"] = telefono
                params["rut"] = rut
                params["password"] = password
                params["estado_usuario"] = "ACTIVO"
                params["rol"] = "Residente"
                params["id_departamento_usuario"] = idDepartamentoActual.toString()

                params["codigo_sensor"] = codigoSensor
                params["tipo_sensor"] = tipoSensor
                params["estado_sensor"] = "ACTIVO"

                return params
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun generarPassword(longitud: Int): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..longitud).map { chars.random() }.joinToString("")
    }
}

