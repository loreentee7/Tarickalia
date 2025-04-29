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

        // Obtenim el nom d'usuari passat com a extra
        val usernamerebut = intent.getStringExtra("username")
        binding.nompares.text = usernamerebut

        val sharedPref = getSharedPreferences("MyPref", MODE_PRIVATE)
        val familiaName = sharedPref.getString("familiaName", "")

        binding.familiacarregada.text = familiaName

        drawerLayout = findViewById(R.id.drawer_layout)

        // Configura el comportament del menú de navegació
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

        // Configura el botó per tornar a la pantalla principal
        binding.gohome.setOnClickListener {
            val intent = Intent(this, HomePares::class.java)
            intent.putExtra("username", usernamerebut)
            startActivity(intent)
        }

        // Configura el botó per obrir el menú de navegació
        binding.menuopen.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Carrega les famílies disponibles al Spinner

        // Configura el botó per crear una nova recompensa
        binding.btncrear.setOnClickListener {
            val nombreRecompensa = binding.nomrecompensa.text.toString()
            val puntuacion = binding.puntuacio.text.toString().toInt()

            // Obtén el nombre de la familia de las preferencias compartidas
            val sharedPref = getSharedPreferences("MyPref", MODE_PRIVATE)
            val familiaName = sharedPref.getString("familiaName", "")

            lifecycleScope.launch(Dispatchers.IO) {
                val apiService = TarickaliaApi().getApiService()
                val responseFamilias = apiService.getFamiliums()
                if (responseFamilias.isSuccessful) {
                    val familias = responseFamilias.body()
                    // Busca la familia con el nombre correspondiente para obtener su ID
                    val familiaId = familias?.find { it.nombre == familiaName }?.id

                    if (familiaId != null) {
                        var nuevaRecompensa = Recompensa(
                            nombre = nombreRecompensa,
                            puntuacion = puntuacion,
                            reclamada = false,
                            idFamilia = familiaId
                        )
                        var responseRecompensa = apiService.postRecompensa(nuevaRecompensa)
                        while (!responseRecompensa.isSuccessful && responseRecompensa.code() == 409) {
                            nuevaRecompensa = Recompensa(
                                nombre = nombreRecompensa,
                                puntuacion = puntuacion,
                                reclamada = false,
                                idFamilia = familiaId
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
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@CreacioRecompenses, "Familia no encontrada", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@CreacioRecompenses, "Error al cargar las familias", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // Funció per crear una nova recompensa
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