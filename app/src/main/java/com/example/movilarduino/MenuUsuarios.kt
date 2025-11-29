package com.example.movilarduino

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

private lateinit var btnadduser : Button
private lateinit var btnviewusers : Button

private lateinit var btnlistarSensores : Button

class MenuUsuarios : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu_usuarios)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        btnadduser = findViewById(R.id.buttonAddUser)
        btnviewusers =findViewById(R.id.buttonViewUsers)
        btnlistarSensores =findViewById(R.id.listarSensor)

        btnadduser.setOnClickListener {
            val intent = Intent(this, AgregarUsuario::class.java)
            startActivity(intent)
        }
        btnviewusers.setOnClickListener {
            val intent = Intent(this, ListarUsuarios::class.java)
            startActivity(intent)
        }
        btnlistarSensores.setOnClickListener {
            val intent = Intent(this, ListarSensores::class.java)
            startActivity(intent)
        }

    }
}