package com.example.movilarduino

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import cn.pedant.SweetAlert.SweetAlertDialog
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class CambiarContrasenia : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cambiar_contrasenia)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val newPassword = findViewById<EditText>(R.id.editNewPassword)
        val newPassword2 = findViewById<EditText>(R.id.editNewPassword2)
        val btnChange = findViewById<Button>(R.id.btnChangePassword)

        btnChange.setOnClickListener {

            val pass1 = newPassword.text.toString()
            val pass2 = newPassword2.text.toString()

            if (pass1.isEmpty() || pass2.isEmpty()) {
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Campos vacíos")
                    .setContentText("Ingrese la contraseña en ambos campos")
                    .show()
                return@setOnClickListener
            }

            // Validar contraseñas
            if (!esPasswordValida(pass1)) {
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Contraseña inválida")
                    .setContentText("Debe tener mínimo 8 caracteres, 1 mayúscula, 1 minúscula, 1 número y 1 carácter especial.")
                    .show()
                return@setOnClickListener
            }

            if (pass1 != pass2) {
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Las contraseñas no coinciden")
                    .setContentText("Intente nuevamente.")
                    .show()
                return@setOnClickListener
            }

            // Obtener ID del usuario
            val prefs = getSharedPreferences("user_data", MODE_PRIVATE)
            val idUsuario = prefs.getInt("id_usuario", -1)

            if (idUsuario == -1) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("ID de usuario no encontrado.")
                    .show()
                return@setOnClickListener
            }

            cambiarPasswordEnServidor(idUsuario, pass1)
        }
    }

    private fun esPasswordValida(password: String): Boolean {
        val regex =
            Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&.#_-])[A-Za-z\\d@\$!%*?&.#_-]{8,}$")
        return regex.matches(password)
    }

    private fun cambiarPasswordEnServidor(idUsuario: Int, nuevaPass: String) {
        val url = "http://98.95.8.72/cambiar_password.php"

        val queue = Volley.newRequestQueue(this)
        val request = object : StringRequest(
            Request.Method.POST, url,
            { response ->

                if (response.contains("correctamente", ignoreCase = true)) {

                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Contraseña cambiada")
                        .setContentText("La contraseña se actualizó correctamente.")
                        .setConfirmClickListener {
                            it.dismissWithAnimation()

                            val intent = Intent(this, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }
                        .show()

                } else {
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText(response)
                        .show()
                }
            },
            { error ->
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error del servidor")
                    .setContentText(error.message ?: "Error desconocido")
                    .show()
            }) {

            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id_usuario"] = idUsuario.toString()
                params["password"] = nuevaPass
                return params
            }
        }
        queue.add(request)
    }
}

