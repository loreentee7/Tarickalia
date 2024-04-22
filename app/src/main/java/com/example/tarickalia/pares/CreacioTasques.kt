package com.example.tarickalia.pares

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.example.tarickalia.api.Models.Familium
import com.example.tarickalia.api.Models.Tarea
import com.example.tarickalia.api.Models.Usuario
import com.example.tarickalia.api.TarickaliaApi
import com.example.tarickalia.databinding.ActivityCreacioTasquesBinding
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreacioTasques : AppCompatActivity() {
    private lateinit var binding: ActivityCreacioTasquesBinding
    private lateinit var drawerLayout: DrawerLayout
    var fechaSeleccionada: String = ""
    private var familias: List<Familium>? = null
    private var selectedFamilia: Familium? = null
    private var usuarios: List<Usuario>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreacioTasquesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val usernamerebut = intent.getStringExtra("username")
        binding.nompares.text = usernamerebut

        val navigationView: NavigationView = findViewById(com.example.tarickalia.R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                com.example.tarickalia.R.id.GestioFills -> {
                    val intent = Intent(this, GestioFamilia::class.java)
                    intent.putExtra("username", binding.nompares.text.toString())
                    startActivity(intent)
                }
                com.example.tarickalia.R.id.CreacioTasca -> {
                    val intent = Intent(this, CreacioTasques::class.java)
                    intent.putExtra("username", binding.nompares.text.toString())
                    startActivity(intent)
                }
                com.example.tarickalia.R.id.CreacioRecompenses -> {
                    val intent = Intent(this, CreacioRecompenses::class.java)
                    intent.putExtra("username", binding.nompares.text.toString())
                    startActivity(intent)
                }
                com.example.tarickalia.R.id.Puntuacions -> {
                    val intent = Intent(this, PuntuacionsPares::class.java)
                    intent.putExtra("username", binding.nompares.text.toString())
                    startActivity(intent)
                }
                com.example.tarickalia.R.id.TasquesCompletes -> {
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
                selectedFamilia?.id?.let { cargarHijosEnSpinner(it) }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            fechaSeleccionada = dateFormat.format(calendar.time)
        }



        val dificultades = arrayOf("Fàcil", "Normal", "Dificíl")
        val adapterDificultades = ArrayAdapter(this, android.R.layout.simple_spinner_item, dificultades)
        adapterDificultades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.dificultat.adapter = adapterDificultades

        binding.dificultat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val dificultadSeleccionada = parent.getItemAtPosition(position).toString()
                val puntuacion = when (dificultadSeleccionada) {
                    "Fàcil" -> 200
                    "Normal" -> 600
                    "Dificíl" -> 1200
                    else -> 0
                }
                binding.puntuacio.text = puntuacion.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }

        }

        binding.btncrear.setOnClickListener {
            val nombreTarea = binding.nomtasca.text.toString()
            val familiaId = selectedFamilia?.id
            val hijoSeleccionado = binding.nomfill.selectedItem.toString()
            val dificultadSeleccionada = binding.dificultat.selectedItem.toString()
            val puntuacion = binding.puntuacio.text.toString().toInt()

            val usuarioSeleccionado = usuarios?.find { it.nombreUsuario == hijoSeleccionado }
            val usuarioId = usuarioSeleccionado?.id

            if (familiaId != null && usuarioId != null) {
                val nuevaTarea = Tarea(
                    Nombre = nombreTarea,
                    Dificultad = dificultadSeleccionada,
                    Puntuacion = puntuacion,
                    IdFamilia = familiaId,
                    IdUsuario = usuarioId
                )

                crearTarea(nuevaTarea)
            } else {
                Toast.makeText(this, "Por favor, selecciona una familia y un usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun crearTarea(tarea: Tarea) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val apiService = TarickaliaApi().getApiService()
                val response = apiService.createTask(tarea)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@CreacioTasques, "Tarea creada con éxito", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@CreacioTasques, "Error al crear la tarea", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CreacioTasques, "Error al crear la tarea", Toast.LENGTH_SHORT).show()
                }
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
                        val adapter = ArrayAdapter(this@CreacioTasques, android.R.layout.simple_spinner_item, familiaNames)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.nomfamilia.adapter = adapter
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CreacioTasques, "Error al cargar las familias", Toast.LENGTH_SHORT).show()
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
                    val usuarios = responseUsuarios.body()
                    val hijos = usuarios?.filter { !it.admin!! }
                    withContext(Dispatchers.Main) {
                        val hijoNames = hijos?.map { it.nombreUsuario }
                        if (hijoNames != null) {
                            val adapter = ArrayAdapter(this@CreacioTasques, android.R.layout.simple_spinner_item, hijoNames)
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            binding.nomfill.adapter = adapter
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CreacioTasques, "Error al cargar los hijos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}