package com.example.tarickalia

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tarickalia.api.Models.Usuario
import com.example.tarickalia.api.TarickaliaApi
import com.example.tarickalia.databinding.ActivityRegisterBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Register : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.register.setOnClickListener() {
            val nombreUsuario = binding.username.text.toString()
            val contrasena = binding.password.text.toString()
            val confirmarContrasena = binding.confirmarpassword.text.toString()
            val admin = binding.admin.isChecked
            val nom = binding.nom.text.toString()
            val cognoms = binding.cognom.text.toString()
            val genero = if (binding.genere.isChecked) "Mujer" else "Hombre"

            if (contrasena != confirmarContrasena) {
                Toast.makeText(this, "Les contrasenyes no son iguals", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val usuario = Usuario(
                nombreUsuario = nombreUsuario,
                contraseña = contrasena,
                admin = admin,
                Nombre = nom,
                Apellidos = cognoms,
                genero = genero
            )

            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val apiService = TarickaliaApi().getApiService()
                    val response = apiService.postUsuario(usuario)

                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@Register, "Usuario creat correctament", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@Register, MainActivity::class.java))
                        } else {
                            Toast.makeText(this@Register, "Error al crear l'usuari", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Register, "Error al crear l'usuari", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }
}