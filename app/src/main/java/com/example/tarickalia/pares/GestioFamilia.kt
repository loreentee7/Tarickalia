package com.example.tarickalia.pares

import android.content.Intent
import android.os.Bundle
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

        // Càrrega de les famílies i els fills disponibles
        loadFamilies()
        loadFills()

        binding.crearfamilia.setOnClickListener {
            createFamilia()
        }

        binding.assignarfills.setOnClickListener {
            assignFillToFamilia()
        }
    }

    // Funció per carregar les famílies disponibles des del servidor
    private fun loadFamilies() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val apiService = TarickaliaApi().getApiService()
                val response = apiService.getFamiliums()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val familias = response.body()
                        val familiaNombres = familias?.map { it.nombre }

                        val spinner: Spinner = findViewById(R.id.nomfamiliaspin)
                        familiaNombres?.let {
                            val adapter = ArrayAdapter(this@GestioFamilia, android.R.layout.simple_spinner_item, it)
                                spinner.adapter = adapter
                        }

                    } else {
                        Toast.makeText(this@GestioFamilia, "Error al cargar las familias", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@GestioFamilia, "Error al cargar las familias", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Funció per carregar els fills disponibles des del servidor
    private fun loadFills() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val apiService = TarickaliaApi().getApiService()
                val response = apiService.getUsuarios()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val usuarios = response.body()
                        val noAdmins = usuarios?.filter { it.admin == false }
                        val noAdminUsernames = noAdmins?.map { it.nombreUsuario }


                        val spinner: Spinner = findViewById(R.id.nomfill1)
                        noAdminUsernames?.let {
                            val adapter = ArrayAdapter(this@GestioFamilia, android.R.layout.simple_spinner_item, it)
                            spinner.adapter = adapter
                        }

                    } else {
                        Toast.makeText(this@GestioFamilia, "Error al cargar los usuarios", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@GestioFamilia, "Error al cargar los usuarios", Toast.LENGTH_SHORT).show()
                }
            }
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
                        val newFamilia = Familium(nombre = familiaName, usuarios = listOf(usuario.copy(id = null)))
                        val responseFamilia = apiService.postFamilium(newFamilia)

                        withContext(Dispatchers.Main) {
                            if (responseFamilia.isSuccessful) {
                                Toast.makeText(this@GestioFamilia, "Error al crear la familia", Toast.LENGTH_SHORT).show()

                            } else {
                                Toast.makeText(this@GestioFamilia, "Familia creada con éxito", Toast.LENGTH_SHORT).show()
                                loadFamilies()
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

    // Funció per assignar un fill a una família
    private fun assignFillToFamilia() {
        val familiaName = binding.nomfamiliaspin.selectedItem.toString()
        val selectedChildName = binding.nomfill1.selectedItem.toString()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val apiService = TarickaliaApi().getApiService()

                val responseFamilias = apiService.getFamiliums()
                if (responseFamilias.isSuccessful) {
                    val familias = responseFamilias.body()
                    val selectedFamilia = familias?.find { it.nombre == familiaName }

                    val responseUsuarios = apiService.getUsuarios()
                    if (responseUsuarios.isSuccessful) {
                        val usuarios = responseUsuarios.body()
                        val selectedUsuario = usuarios?.find { it.nombreUsuario == selectedChildName }

                        if (selectedFamilia != null && selectedUsuario != null) {
                            selectedUsuario.idFamilia = selectedFamilia.id

                            val responseUpdateUsuario = apiService.putUsuario(selectedUsuario.id!!, selectedUsuario)
                            withContext(Dispatchers.Main) {
                                if (responseUpdateUsuario.isSuccessful) {
                                    Toast.makeText(this@GestioFamilia, "Usuario asignado a la familia con éxito", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this@GestioFamilia, "Error al asignar el usuario a la familia", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@GestioFamilia, "Error al asignar el usuario a la familia", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
