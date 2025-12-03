package com.example.movilarduino

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.net.URLEncoder

class EditarUsuario : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_usuario)

        // Referencias UI
        val idUsuario = intent.getStringExtra("id_usuario")
        val txtNombres = findViewById<TextView>(R.id.txtNombres)
        val txtApellidos = findViewById<TextView>(R.id.txtApellidos)
        val txtRut = findViewById<TextView>(R.id.txtRut)
        val editEmail = findViewById<EditText>(R.id.editEmail)
        val editTelefono = findViewById<EditText>(R.id.editTelefono)
        val spinnerEstado = findViewById<Spinner>(R.id.spinnerEstadoUsuario)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarUsuario)
        val btnEliminar = findViewById<Button>(R.id.btnEliminarUsuario)

        // Recibir datos
        val nombres = intent.getStringExtra("nombres") ?: ""
        val apellidos = intent.getStringExtra("apellidos") ?: ""
        val rut = intent.getStringExtra("rut") ?: ""
        val email = intent.getStringExtra("email") ?: ""
        val telefono = intent.getStringExtra("telefono") ?: ""
        val estado = intent.getStringExtra("estado") ?: "ACTIVO"

        // Mostrar datos
        txtNombres.text = "Nombre(s): $nombres"
        txtApellidos.text = "Apellido(s): $apellidos"
        txtRut.text = "RUT: $rut"
        editEmail.setText(email)
        editTelefono.setText(telefono)

        val estados = listOf("ACTIVO", "INACTIVO", "BLOQUEADO")
        spinnerEstado.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, estados)
        spinnerEstado.setSelection(estados.indexOf(estado))

        // ------------------------------
        //     GUARDAR CON CONFIRMACIÓN
        // ------------------------------
        btnGuardar.setOnClickListener {

            val nuevoEmail = editEmail.text.toString().trim()
            val nuevoTelefono = editTelefono.text.toString().trim()
            val nuevoEstado = spinnerEstado.selectedItem.toString()

            // Validación de campos vacíos
            if (nuevoEmail.isEmpty()) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("El email no puede estar vacío")
                    .show()
                return@setOnClickListener
            }

            if (nuevoTelefono.isEmpty()) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("El teléfono no puede estar vacío")
                    .show()
                return@setOnClickListener
            }

            // Validación de formato de correo
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(nuevoEmail).matches()) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Email inválido")
                    .setContentText("Por favor ingresa un email con formato válido")
                    .show()
                return@setOnClickListener
            }

            // Validación de formato de teléfono (debe comenzar con 9 y ser chileno)
            if (!nuevoTelefono.matches("^9\\d{8}$".toRegex())) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Teléfono inválido")
                    .setContentText("El teléfono debe tener 9 dígitos y comenzar con 9")
                    .show()
                return@setOnClickListener
            }

            // Confirmación
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("¿Guardar cambios?")
                .setContentText("Vas a actualizar el usuario")
                .setConfirmText("Sí, guardar")
                .setCancelText("Cancelar")
                .setConfirmClickListener { dialog ->

                    dialog.dismissWithAnimation()

                    val encodeEmail = URLEncoder.encode(nuevoEmail, "UTF-8")
                    val encodeTelefono = URLEncoder.encode(nuevoTelefono, "UTF-8")
                    val encodeEstado = URLEncoder.encode(nuevoEstado, "UTF-8")

                    val url =
                        "http://98.95.8.72/editar_usuario.php?id_usuario=$idUsuario&email=$encodeEmail&telefono=$encodeTelefono&estado=$encodeEstado"

                    val queue = Volley.newRequestQueue(this)

                    val request = StringRequest(Request.Method.GET, url,
                        { response ->
                            try {
                                val json = JSONObject(response)

                                // Verificar el éxito o error en la actualización
                                if (json.getString("success") == "false") {
                                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Error")
                                        .setContentText(json.getString("message"))
                                        .show()
                                } else {
                                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Actualizado")
                                        .setContentText("El usuario fue modificado")
                                        .setConfirmClickListener {
                                            it.dismissWithAnimation()
                                            finish()
                                        }
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
                        })

                    queue.add(request)
                }
                .show()
        }


        // ------------------------------
        //     ELIMINAR CON CONFIRMACIÓN
        // ------------------------------
        btnEliminar.setOnClickListener {

            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("¿Eliminar usuario?")
                .setContentText("Esta acción no se puede deshacer")
                .setConfirmText("Sí, eliminar")
                .setCancelText("Cancelar")
                .setConfirmClickListener { dialog ->

                    dialog.dismissWithAnimation()

                    val url = "http://98.95.8.72/eliminar_usuario.php?id_usuario=$idUsuario"

                    val queue = Volley.newRequestQueue(this)

                    val request = StringRequest(
                        Request.Method.GET, url,
                        {
                            SweetAlertDialog(
                                this,
                                SweetAlertDialog.SUCCESS_TYPE
                            )
                                .setTitleText("Eliminado")
                                .setContentText("El usuario fue eliminado")
                                .setConfirmClickListener {
                                    it.dismissWithAnimation()
                                    finish()
                                }
                                .show()
                        },
                        {
                            SweetAlertDialog(
                                this,
                                SweetAlertDialog.ERROR_TYPE
                            )
                                .setTitleText("Error")
                                .setContentText("No se pudo eliminar el usuario")
                                .show()
                        }
                    )

                    queue.add(request)
                }
                .show()

        }
    }
}

