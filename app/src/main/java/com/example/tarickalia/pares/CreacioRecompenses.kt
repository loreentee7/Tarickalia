package com.example.tarickalia.pares

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.tarickalia.R
import com.example.tarickalia.databinding.ActivityCreacioRecompensesBinding
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.lifecycle.lifecycleScope
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.tarickalia.api.Models.Familium
import com.example.tarickalia.api.Models.Recompensa
import com.example.tarickalia.api.Models.Tarea
import com.example.tarickalia.api.Models.Usuario
import com.example.tarickalia.api.TarickaliaApi
import kotlinx.coroutines.launch

class CreacioRecompenses : AppCompatActivity() {

    private lateinit var binding: ActivityCreacioRecompensesBinding
    private lateinit var drawerLayout: DrawerLayout
    private var familias: List<Familium>? = null
    private var selectedFamilia: Familium? = null
    private var selectedChild: Usuario? = null
    private var usuarios: List<Usuario>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreacioRecompensesBinding.inflate(layoutInflater)
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

        cargarFamiliasEnSpinner()

        binding.nomfamilia.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedFamiliaName = parent.getItemAtPosition(position).toString()
                selectedFamilia = familias?.find { it.nombre == selectedFamiliaName }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        binding.btncrear.setOnClickListener {
            val nombreRecompensa = binding.nomrecompensa.text.toString()
            val puntuacion = binding.puntuacio.text.toString().toInt()
            val familiaId = selectedFamilia?.id

            if (familiaId != null) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val apiService = TarickaliaApi().getApiService()
                    val responseRecompensas = apiService.getRecompensas()
                    if (responseRecompensas.isSuccessful) {
                        val recompensas = responseRecompensas.body()
                        var nuevaRecompensa = Recompensa(
                            Nombre = nombreRecompensa,
                            Puntuacion = puntuacion,
                            Reclamada = false,
                            IdFamilia = familiaId
                        )
                        var responseRecompensa = apiService.postRecompensa(nuevaRecompensa)
                        while (!responseRecompensa.isSuccessful && responseRecompensa.code() == 409) {
                            nuevaRecompensa = Recompensa(
                                Nombre = nombreRecompensa,
                                Puntuacion = puntuacion,
                                Reclamada = false,
                                IdFamilia = familiaId
                            )
                            responseRecompensa = apiService.postRecompensa(nuevaRecompensa)
                        }
                        if (responseRecompensa.isSuccessful) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@CreacioRecompenses, "Recompensa creada con éxito", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@CreacioRecompenses, "Error al crear la recompensa", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Por favor, selecciona una familia", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun cargarFamiliasEnSpinner() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val apiService = TarickaliaApi().getApiService()
                val responseFamilias = apiService.getFamiliums()

                if (responseFamilias.isSuccessful) {
                    familias = responseFamilias.body()
                    withContext(Dispatchers.Main) {
                        val familiaNames = familias?.map { it.nombre } ?: listOf()
                        val adapter = ArrayAdapter(this@CreacioRecompenses, android.R.layout.simple_spinner_item, familiaNames)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.nomfamilia.adapter = adapter
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CreacioRecompenses, "Error al cargar las familias", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun crearRecompensa(recompensa: Recompensa) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val apiService = TarickaliaApi().getApiService()
                val responseRecompensa = apiService.postRecompensa(recompensa)

                if (responseRecompensa.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@CreacioRecompenses, "Recompensa creada con éxito", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@CreacioRecompenses, "Error al crear la recompensa", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CreacioRecompenses, "Error al crear la recompensa", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



}