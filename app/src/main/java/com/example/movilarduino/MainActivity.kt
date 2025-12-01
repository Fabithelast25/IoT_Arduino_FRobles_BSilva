package com.example.movilarduino

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import cn.pedant.SweetAlert.SweetAlertDialog
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
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Campos vacíos")
                .setContentText("Ingrese email y contraseña")
                .show()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Email inválido")
                .setContentText("Ingrese un email válido")
                .show()
            return
        }

        val url = "http://98.95.8.72/login.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->

                try {
                    val json = JSONObject(response)

                    if (json.getBoolean("success")) {

                        val primeraVez = json.getInt("primera_vez")
                        val idUsuario = json.getInt("id_usuario")
                        val idDepartamento = json.getInt("id_departamento")
                        val rol = json.getString("rol")  // 👈 AQUI RECIBIMOS EL ROL

                        // Guardar IDs en SharedPreferences
                        val prefs = getSharedPreferences("user_data", MODE_PRIVATE)
                        prefs.edit().apply {
                            putInt("id_usuario", idUsuario)
                            putInt("id_departamento", idDepartamento)
                            putString("rol", rol)
                            apply()
                        }

                        if (primeraVez == 1) {

                            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Nueva contraseña requerida")
                                .setContentText("Debe crear una nueva contraseña")
                                .setConfirmText("Continuar")
                                .setConfirmClickListener {
                                    val intent = Intent(this, CambiarContrasenia::class.java)
                                    intent.putExtra("id_usuario", idUsuario)
                                    startActivity(intent)
                                    it.dismissWithAnimation()
                                }
                                .show()

                        } else {

                            SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Bienvenido!")
                                .setContentText("Inicio de sesión exitoso")
                                .setConfirmText("Continuar")
                                .setConfirmClickListener {

                                    // 💥 VALIDACIÓN SEGÚN ROL
                                    val nextScreen = when (rol) {
                                        "Residente" -> MenuResidente::class.java
                                        "Administrador" -> MenuAdministrador::class.java
                                        else -> MenuAdministrador::class.java // fallback
                                    }

                                    val intent = Intent(this, nextScreen)
                                    startActivity(intent)

                                    it.dismissWithAnimation()
                                }
                                .show()
                        }

                    } else {
                        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText(json.getString("message"))
                            .show()
                    }

                } catch (e: Exception) {
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error JSON")
                        .setContentText(e.message.toString())
                        .show()
                }
            },
            { error ->
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error de conexión")
                    .setContentText(error.message ?: "Error desconocido")
                    .show()
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


