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

        // Validación del RUT
        if (!esRutValido(rut)) {
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("RUT inválido")
                .setContentText("El RUT ingresado no es válido. Verifique el formato.")
                .show()
            return
        }

        // Validación del teléfono
        if (!esTelefonoValido(telefono)) {
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Teléfono inválido")
                .setContentText("El número de teléfono debe comenzar con 9 y tener 9 dígitos.")
                .show()
            return
        }

        // Validación de nombres
        if (!esNombreValido(nombres)) {
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Nombres inválidos")
                .setContentText("Los nombres solo pueden contener letras y espacios.")
                .show()
            return
        }

        // Validación de apellidos
        if (!esNombreValido(apellidos)) {
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Apellidos inválidos")
                .setContentText("Los apellidos solo pueden contener letras y espacios.")
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
                    // Verificar el estado de la respuesta para el error de RUT o correo duplicado
                    if (json.getString("success") == "false") {
                        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText(json.getString("message"))
                            .show()
                    } else {
                        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Usuario creado")
                            .setContentText(json.getString("message"))
                            .show()
                    }

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

    private fun esRutValido(rut: String): Boolean {
        // Eliminar guion
        val rutLimpio = rut.replace("-", "")

        // Verificar que el rut tenga el tamaño correcto (mínimo 2 caracteres)
        if (rutLimpio.length < 2) return false

        // Dividir el RUT y el dígito verificador
        val cuerpoRut = rutLimpio.substring(0, rutLimpio.length - 1)
        val dv = rutLimpio.last().toUpperCase()

        // Verificar que el cuerpo del RUT solo contenga números
        if (!cuerpoRut.all { it.isDigit() }) return false

        // Algoritmo para validar el dígito verificador
        var suma = 0
        var multiplicador = 2

        for (i in cuerpoRut.length - 1 downTo 0) {
            suma += (cuerpoRut[i].toString().toInt()) * multiplicador
            multiplicador = if (multiplicador == 7) 2 else multiplicador + 1
        }

        val dvCalculado = 11 - (suma % 11)
        val dvCorrecto = when (dvCalculado) {
            11 -> '0'
            10 -> 'K'
            else -> dvCalculado.toString()[0]
        }

        // Verificar si el dígito verificador calculado coincide con el ingresado
        return dv == dvCorrecto.toUpperCase()
    }

    private fun esTelefonoValido(telefono: String): Boolean {
        // Verificar que el teléfono no esté vacío y tenga 9 dígitos (sin contar el código de área)
        return telefono.length == 9 && telefono.startsWith("9") && telefono.all { it.isDigit() }
    }

    private fun esNombreValido(nombre: String): Boolean {
        // La expresión regular permite letras y espacios
        val regex = "^[a-zA-ZáéíóúÁÉÍÓÚÑñ ]+\$".toRegex()
        return nombre.matches(regex)
    }
}

