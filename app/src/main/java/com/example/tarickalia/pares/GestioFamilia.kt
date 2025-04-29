package com.example.tarickalia.pares

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.example.tarickalia.R
import com.example.tarickalia.api.Models.Familium
import com.example.tarickalia.api.TarickaliaApi
import com.example.tarickalia.databinding.ActivityGestioFamiliaBinding
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GestioFamilia : AppCompatActivity() {

    private lateinit var binding: ActivityGestioFamiliaBinding
    private lateinit var drawerLayout: DrawerLayout
    private var idFamilia: Int? = null
    var isFamilyCreated = false
    private var familias: List<Familium>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGestioFamiliaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val usernamerebut = intent.getStringExtra("username")
        binding.nompares.text = usernamerebut

        drawerLayout = findViewById(R.id.drawer_layout)

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.GestioFills -> {
                    val intent = Intent(this, GestioFamilia::class.java)
                    intent.putExtra("username", binding.nompares.text.toString())
                    startActivity(intent)
                }
                R.id.CreacioTasca -> {
                    val intent = Intent(this, CreacioTasques::class.java)
                    intent.putExtra("username", binding.nompares.text.toString())
                    startActivity(intent)
                }
                R.id.CreacioRecompenses -> {
                    val intent = Intent(this, CreacioRecompenses::class.java)
                    intent.putExtra("username", binding.nompares.text.toString())
                    startActivity(intent)
                }
                R.id.Puntuacions -> {
                    val intent = Intent(this, PuntuacionsPares::class.java)
                    intent.putExtra("username", binding.nompares.text.toString())
                    startActivity(intent)
                }
                R.id.TasquesCompletes -> {
                    val intent = Intent(this, TasquesCompletesPares::class.java)
                    intent.putExtra("username", binding.nompares.text.toString())
                    startActivity(intent)
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        binding.gohome.setOnClickListener {
            val intent = Intent(this, HomePares::class.java)
            intent.putExtra("username", usernamerebut)
            startActivity(intent)
        }

        binding.menuopen.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }


        binding.crearfamilia.setOnClickListener {
            if (!isFamilyCreated) {
                lifecycleScope.launch {
                    createFamilia()
                    isFamilyCreated = true
                    val familiaName = binding.nomfamilia.text.toString()
                }
            } else {
                Toast.makeText(this, "Ya has creado una familia", Toast.LENGTH_SHORT).show()
            }
        }

        binding.crearfills.setOnClickListener {
            val intent = Intent(this, CreateChildActivity::class.java)
            intent.putExtra("username", binding.nompares.text.toString())
            intent.putExtra("idFamilia", idFamilia)
            startActivity(intent)
        }
    }

    // Funció per crear una nova família
    private fun createFamilia() {
        val familiaName = binding.nomfamilia.text?.toString()
        val nomusuari = binding.nompares.text.toString()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val apiService = TarickaliaApi().getApiService()
                val responseUsuarios = apiService.getUsuarios()
                if (responseUsuarios.isSuccessful) {
                    val usuarios = responseUsuarios.body()
                    val usuario = usuarios?.find { it.nombreUsuario == nomusuari }
                    if (usuario != null) {
                        val newFamilia = Familium(nombre = familiaName)
                        val responseFamilia = apiService.postFamilium(newFamilia)
                        if (responseFamilia.isSuccessful) {
                            val createdFamilia = responseFamilia.body()
                            idFamilia = createdFamilia?.id

                            // Actualiza el usuario para que su idFamilia sea el id de la nueva familia
                            val usuarioToUpdate = usuario.copy(idFamilia = idFamilia)
                            val responseUpdateUsuario = apiService.putUsuario(usuario.id!!, usuarioToUpdate)
                            if (responseUpdateUsuario.isSuccessful) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(this@GestioFamilia, "Usuario añadido a la familia con éxito", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(this@GestioFamilia, "Error al añadir el usuario a la familia", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        withContext(Dispatchers.Main) {
                            if (responseFamilia.isSuccessful) {
                                Toast.makeText(this@GestioFamilia, "Familia creada con éxito", Toast.LENGTH_SHORT).show()
                                binding.familyname.text = familiaName
                                val familiaName = binding.nomfamilia.text.toString()
                                val sharedPref = getSharedPreferences("MyPref", MODE_PRIVATE)
                                val editor = sharedPref.edit()
                                editor.putString("familiaName", familiaName)
                                editor.apply()
                            } else {
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@GestioFamilia, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@GestioFamilia, "Error al cargar los usuarios", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@GestioFamilia, "Error al crear la familia", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
