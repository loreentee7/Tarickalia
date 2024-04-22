package com.example.tarickalia.pares

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.example.tarickalia.R
import com.example.tarickalia.api.Models.Familium
import com.example.tarickalia.api.Models.Usuario
import com.example.tarickalia.api.TarickaliaApi
import com.example.tarickalia.databinding.ActivityPuntuacionsParesBinding
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.widget.Toast
import com.example.tarickalia.TareaAdapter
import kotlinx.coroutines.withContext



class PuntuacionsPares : AppCompatActivity() {

    private lateinit var binding: ActivityPuntuacionsParesBinding
    private lateinit var drawerLayout: DrawerLayout
    private var familias: List<Familium>? = null
    private var selectedFamilia: Familium? = null
    private var usuarios: List<Usuario>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPuntuacionsParesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val usernamerebut = intent.getStringExtra("username")
        binding.nompares.text = usernamerebut

        drawerLayout = findViewById(R.id.drawer_layout)

        cargarFamiliasEnSpinner()

        binding.nomfamilia.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedFamiliaName = parent.getItemAtPosition(position).toString()
                selectedFamilia = familias?.find { it.nombre == selectedFamiliaName }
                selectedFamilia?.id?.let { cargarHijosEnSpinner(it) }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        binding.nomfill.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedHijoName = parent.getItemAtPosition(position).toString()
                val selectedHijo = usuarios?.find { it.nombreUsuario == selectedHijoName }
                binding.puntuacio.text = selectedHijo?.Monedas.toString()

                selectedHijo?.id?.let { cargarTareas(it) }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

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


        binding.menuopen.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.gohome.setOnClickListener {
            val intent = Intent(this, HomePares::class.java)
            intent.putExtra("username", usernamerebut)
            startActivity(intent)
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
                        val adapter = ArrayAdapter(this@PuntuacionsPares, android.R.layout.simple_spinner_item, familiaNames)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.nomfamilia.adapter = adapter
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PuntuacionsPares, "Error al cargar las familias", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun cargarHijosEnSpinner(familiaId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val apiService = TarickaliaApi().getApiService()
                val responseUsuarios = apiService.getUsuariosByFamilia(familiaId)
                if (responseUsuarios.isSuccessful) {
                    usuarios = responseUsuarios.body()
                    val hijos = usuarios?.filter { !it.admin!! }
                    withContext(Dispatchers.Main) {
                        val hijoNames = hijos?.map { it.nombreUsuario }
                        if (hijoNames != null) {
                            val adapter = ArrayAdapter(this@PuntuacionsPares, android.R.layout.simple_spinner_item, hijoNames)
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            binding.nomfill.adapter = adapter
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PuntuacionsPares, "Error al cargar los hijos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun cargarTareas(usuarioId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val apiService = TarickaliaApi().getApiService()
                val responseTareas = apiService.getTareas()
                if (responseTareas.isSuccessful) {
                    val tareas = responseTareas.body()?.filter { it.IdUsuario == usuarioId }
                    withContext(Dispatchers.Main) {
                        if (tareas != null) {
                            val adapter = TareaAdapter(tareas)
                            binding.recyclerView.adapter = adapter
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PuntuacionsPares, "Error al cargar las tareas", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}