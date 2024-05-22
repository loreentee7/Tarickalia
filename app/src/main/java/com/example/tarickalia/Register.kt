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
import java.math.BigInteger
import java.security.MessageDigest

class Register : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura l'acció del botó de registre
        binding.register.setOnClickListener() {
            // Obtenim el text dels camps
            val nombreUsuario = binding.username.text.toString()
            val contrasena = binding.password.text.toString()
            val confirmarContrasena = binding.confirmarpassword.text.toString()
            val admin = binding.admin.isChecked
            val nom = binding.nom.text.toString()
            val correu = binding.email.text.toString()
            val cognoms = binding.cognom.text.toString()
            val genero = if (binding.genere.isChecked) "Mujer" else "Hombre"

            // Comprovem si les contrasenyes coincideixen
            if (contrasena != confirmarContrasena) {
                Toast.makeText(this, "Les contrasenyes no son iguals", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Cifrem la contrasenya
            val contrasenaCifrada = contrasena.toSHA256()

            // Creem un nou usuari amb les dades introduïdes
            val usuario = Usuario(
                nombreUsuario = nombreUsuario,
                contraseña = contrasenaCifrada,
                admin = admin,
                correo = correu,
                Nombre = nom,
                Apellidos = cognoms,
                genero = genero
            )

            // Iniciem una nova tasca en segon pla
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    // Creem una nova instància del servei API
                    val apiService = TarickaliaApi().getApiService()

                    // Realitzem una petició POST per crear l'usuari
                    val response = apiService.postUsuario(usuario)

                    // Canviem al fil principal per actualitzar la interfície d'usuari
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            // Si la resposta és exitosa, mostrem un missatge i naveguem a la pantalla principal
                            Toast.makeText(this@Register, "Usuario creat correctament", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@Register, MainActivity::class.java))
                        } else {
                            // Si la resposta no és exitosa, mostrem un missatge d'error
                            Toast.makeText(this@Register, "Error al crear l'usuari", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    // Si es produeix una excepció, mostrem un missatge d'error
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Register, "Error al crear l'usuari", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // Funció d'extensió per convertir una cadena a SHA256
    fun String.toSHA256(): String {
        val md = MessageDigest.getInstance("SHA-256")
        val fullHash = BigInteger(1, md.digest(toByteArray())).toString(16).padStart(64, '0')
        return fullHash.substring(0, 32)
    }
}