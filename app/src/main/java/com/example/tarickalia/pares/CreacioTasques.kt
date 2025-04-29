package com.example.tarickalia.pares

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.example.tarickalia.R
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
import java.util.Date
import java.util.Locale

class CreacioTasques : AppCompatActivity() {
    private lateinit var binding: ActivityCreacioTasquesBinding
    private lateinit var drawerLayout: DrawerLayout
    private var familias: List<Familium>? = null
    private var selectedFamilia: Familium? = null
    private var usuarios: List<Usuario>? = null
    private var selectedDate: Date? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreacioTasquesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val usernamerebut = intent.getStringExtra("username")
        binding.nompares.text = usernamerebut

        val sharedPref = getSharedPreferences("MyPref", MODE_PRIVATE)
        val familiaName = sharedPref.getString("familiaName", "")

        binding.nomfamilia.text = familiaName


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

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val apiService = TarickaliaApi().getApiService()

                // Obtén el nombre de la familia de las preferencias compartidas
                val sharedPref = getSharedPreferences("MyPref", MODE_PRIVATE)
                val familiaName = sharedPref.getString("familiaName", "")
                Log.d("CreacioTasques", "familiaName: $familiaName")

                // Busca la familia con ese nombre en la API
                val responseFamilias = apiService.getFamiliums()
                if (responseFamilias.isSuccessful) {
                    val familias = responseFamilias.body()
                    Log.d("CreacioTasques", "familias: $familias")
                    val familia = familias?.find { it.nombre == familiaName }
                    val idFamilia = familia?.id
                    Log.d("CreacioTasques", "idFamilia: $idFamilia")

                    if (idFamilia != null) {
                        // Obtén la lista de usuarios de esa familia
                        val responseUsuarios = apiService.getUsuariosByFamilia(idFamilia)
                        if (responseUsuarios.isSuccessful) {
                            usuarios = responseUsuarios.body()
                            Log.d("CreacioTasques", "usuarios cargados: $usuarios")

                            // Filtra la lista para obtener solo los usuarios que no son administradores
                            val hijos = usuarios?.filter { !it.admin!! }
                            withContext(Dispatchers.Main) {
                                if (!hijos.isNullOrEmpty()) {
                                    val hijosNoNulos = hijos.filterNotNull()
                                    val adapter = ArrayAdapter(this@CreacioTasques, android.R.layout.simple_spinner_item, hijosNoNulos.map { it.nombreUsuario })
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    binding.nomfill.adapter = adapter
                                } else {
                                    Log.d("CreacioTasques", "No hay usuarios para mostrar en el spinner")
                                    Toast.makeText(this@CreacioTasques, "No hay usuarios disponibles", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Log.d("CreacioTasques", "Error en la llamada a getUsuariosByFamilia: ${responseUsuarios.errorBody()}")
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@CreacioTasques, "Error al cargar los usuarios", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        // Maneja el caso en que no se encuentra la familia
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@CreacioTasques, "Familia no encontrada", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // Maneja el caso en que la llamada a la API de familias falla
                    Log.d("CreacioTasques", "Error en la llamada a getFamiliums: ${responseFamilias.errorBody()}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@CreacioTasques, "Error al cargar las familias", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.d("CreacioTasques", "Exception: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CreacioTasques, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDate = calendar.time
        }

        // Configurem el spinner amb les dificultats
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

        // Configura el botó per crear una nova tasca
        binding.btncrear.setOnClickListener {
            val nombreTarea = binding.nomtasca.text.toString()
            val familiaId = selectedFamilia?.id
            val hijoSeleccionado = binding.nomfill.selectedItem.toString()
            Log.d("CreacioTasques", "hijoSeleccionado: $hijoSeleccionado")
            Log.d("CreacioTasques", "usuarios: $usuarios")
            val dificultadSeleccionada = binding.dificultat.selectedItem.toString()
            val puntuacion = binding.puntuacio.text.toString().toInt()
            val usuarioSeleccionado = usuarios?.find { it.nombreUsuario?.toLowerCase() == hijoSeleccionado.toLowerCase() }
            Log.d("CreacioTasques", "usuarioSeleccionado: $usuarioSeleccionado")
            val usuarioId = usuarioSeleccionado?.id
            Log.d("CreacioTasques", "usuarioId: $usuarioId")



            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val fechaCaducidadString = selectedDate?.let { dateFormat.format(it) }

            if (usuarioId != null) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val apiService = TarickaliaApi().getApiService()
                    val responseTareas = apiService.getTareas()
                    if (responseTareas.isSuccessful) {
                        val tareas = responseTareas.body()
                        var nuevaTarea = Tarea(
                            nombre = nombreTarea,
                            dificultad = dificultadSeleccionada,
                            completada = false,
                            puntuacion = puntuacion,
                            idFamilia = familiaId,
                            idUsuario = usuarioId,
                            fechaCaducidad = fechaCaducidadString
                        )
                        var responseTarea = apiService.createTask(nuevaTarea)
                        while (!responseTarea.isSuccessful && responseTarea.code() == 409) {
                            nuevaTarea = Tarea(
                                nombre = nombreTarea,
                                dificultad = dificultadSeleccionada,
                                completada = false,
                                puntuacion = puntuacion,
                                idFamilia = familiaId,
                                idUsuario = usuarioId,
                                fechaCaducidad = fechaCaducidadString
                            )
                            responseTarea = apiService.createTask(nuevaTarea)
                        }
                        if (responseTarea.isSuccessful) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@CreacioTasques, "Tarea creada con éxito", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@CreacioTasques, "Error al crear la tarea", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Por favor, selecciona una familia y un usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Funció per crear una nova tasca
    private fun crearTarea(tarea: Tarea) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val apiService = TarickaliaApi().getApiService()
                val responseTareas = apiService.getTareas()
                if (responseTareas.isSuccessful) {
                    val tareas = responseTareas.body()

                    val response = apiService.createTask(tarea)



                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@CreacioTasques, "Tarea creada con éxito", Toast.LENGTH_SHORT).show()
                    } else {
                        if (response.code() == 409) {
                            Toast.makeText(this@CreacioTasques, "Ya existe una tarea con los mismos detalles", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@CreacioTasques, "Error al crear la tarea", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@CreacioTasques, "Error al obtener las tareas", Toast.LENGTH_SHORT).show()
            }
            }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CreacioTasques, "Error al crear la tarea", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}