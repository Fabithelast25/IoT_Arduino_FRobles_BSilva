package com.example.movilarduino

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

private lateinit var btnsensors : Button
private lateinit var btnviewregisters: Button

class MenuAdministrador : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu_administrador)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        btnsensors = findViewById(R.id.buttonSensors)
        btnviewregisters = findViewById(R.id.buttonViewRegisters)

        btnsensors.setOnClickListener {
            val intent = Intent(this, MenuUsuarios::class.java)
            startActivity(intent)
        }
        btnviewregisters.setOnClickListener {
            val intent = Intent(this, EventosAcceso::class.java)
            startActivity(intent)
        }
    }
}