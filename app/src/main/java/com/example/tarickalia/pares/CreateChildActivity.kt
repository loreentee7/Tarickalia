package com.example.tarickalia.pares

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.tarickalia.MainActivity
import com.example.tarickalia.R
import com.example.tarickalia.api.Models.Usuario
import com.example.tarickalia.api.TarickaliaApi
import com.example.tarickalia.databinding.ActivityCreateChildBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigInteger
import java.security.MessageDigest

class CreateChildActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateChildBinding
    private var parentId: Int? = null
    var idFamilia: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateChildBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = intent.getStringExtra("username")

        lifecycleScope.launch(Dispatchers.IO) {
            val apiService = TarickaliaApi().getApiService()
            val response = apiService.getUsuarios()
            if (response.isSuccessful) {
                val usuarios = response.body()
                val usuario = usuarios?.find { it.nombreUsuario == username }
                parentId = usuario?.id
                idFamilia = usuario?.idFamilia
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val apiService = TarickaliaApi().getApiService()
            val response = apiService.getUsuarios()
            if (response.isSuccessful) {
                val usuarios = response.body()
                val usuario = usuarios?.find { it.nombreUsuario == username }
                parentId = usuario?.id
            }
        }

        binding.submitButton.setOnClickListener() {
            // Obtenim el text dels camps
            val nombreUsuario = binding.username.text.toString()
            val contrasena = binding.password.text.toString()
            val confirmarContrasena = binding.confirmPassword.text.toString()
            val nom = binding.nombre.text.toString()
            val correu = binding.email.text.toString()
            val cognoms = binding.apellidos.text.toString()
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
                admin = false,
                correo = correu,
                Nombre = nom,
                Apellidos = cognoms,
                genero = genero,
                idFamilia = idFamilia

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
                            Toast.makeText(this@CreateChildActivity, "Usuario creat correctament", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            // Si la resposta no és exitosa, mostrem un missatge d'error
                            Toast.makeText(this@CreateChildActivity, "Error al crear l'usuari", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    // Si es produeix una excepció, mostrem un missatge d'error
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@CreateChildActivity, "Error al crear l'usuari", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    fun String.toSHA256(): String {
        val md = MessageDigest.getInstance("SHA-256")
        val fullHash = BigInteger(1, md.digest(toByteArray())).toString(16).padStart(64, '0')
        return fullHash.substring(0, 32)
    }
}