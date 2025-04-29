package com.example.tarickalia

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tarickalia.api.TarickaliaApi
import com.example.tarickalia.databinding.ActivityMainBinding
import com.example.tarickalia.fills.HomeFIlls
import com.example.tarickalia.pares.HomePares
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigInteger
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura l'acció del botó de login
        binding.login.setOnClickListener() {
            // Obtenim el text dels camps
            val username = binding.editTextTextEmailAddress.text.toString()
            val password = binding.editTextTextPassword.text.toString().toSHA256()

            // Iniciem una nova tasca en segon pla
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    // Creem una nova instància del servei API
                    val apiService = TarickaliaApi().getApiService()

                    // Realitzem una petició GET per obtenir els usuaris
                    val response = apiService.getUsuarios()

                    // Canviem al fil principal per actualitzar la interfície d'usuari
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            // Si la resposta és exitosa, obtenim la llista d'usuaris
                            val usuarios = response.body()

                            // Busquem l'usuari amb el nom d'usuari i contrasenya proporcionats
                            val usuario = usuarios?.find { it.nombreUsuario == username && it.contraseña == password }

                            if (usuario != null) {
                                // Si l'usuari existeix, comprovem si és administrador
                                if (usuario.admin == true) {
                                    // Si és administrador, naveguem a la pantalla HomePares
                                    val intent = Intent(this@MainActivity, HomePares::class.java)
                                    intent.putExtra("username", username)
                                    startActivity(intent)
                                } else {
                                    // Si no és administrador, naveguem a la pantalla HomeFIlls
                                    val intent = Intent(this@MainActivity, HomeFIlls::class.java)
                                    intent.putExtra("username", username)
                                    startActivity(intent)
                                }
                            } else {
                                // Si l'usuari no existeix, mostrem un missatge d'error
                                Toast.makeText(this@MainActivity, "Usuari o contrasenya incorrectes", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            // Si la resposta no és exitosa, mostrem un missatge d'error
                            Toast.makeText(this@MainActivity, "Error en obtenir els usuaris", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    // Si es produeix una excepció, mostrem un missatge d'error
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Error en obtenir els usuaris", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Configura l'acció del botó de registre per navegar a la pantalla de registre
        binding.register.setOnClickListener() {
            startActivity(Intent(this, Register::class.java))
        }
    }

    // Funció d'extensió per convertir una cadena a SHA256
    fun String.toSHA256(): String {
        val md = MessageDigest.getInstance("SHA-256")
        val fullHash = BigInteger(1, md.digest(toByteArray())).toString(16).padStart(64, '0')
        return fullHash.substring(0, 32)
    }
}