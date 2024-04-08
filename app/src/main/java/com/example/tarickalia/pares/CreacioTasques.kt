package com.example.tarickalia.pares

import android.R
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
import com.example.tarickalia.bd.usuaris.AppDatabase
import com.example.tarickalia.databinding.ActivityCreacioTasquesBinding
import com.example.tarickalia.familia.Familia
import com.example.tarickalia.tasques.Tasques
import com.example.tarickalia.tasques.TasquesDao
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreacioTasques : AppCompatActivity() {
    private lateinit var binding: ActivityCreacioTasquesBinding
    private lateinit var tasquesDao: TasquesDao
    private lateinit var drawerLayout: DrawerLayout
    var fechaSeleccionada: String = ""

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

        tasquesDao = AppDatabase.getDatabase(this).tasquesDao()

        binding.gohome.setOnClickListener {
            val intent = Intent(this, HomePares::class.java)
            intent.putExtra("username", usernamerebut)
            startActivity(intent)
        }

        binding.menuopen.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        cargarFamiliasEnSpinner()

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
            val hijoSeleccionado = binding.nomfill.selectedItem.toString()
            val dificultadSeleccionada = binding.dificultat.selectedItem.toString()
            val puntuacion = binding.puntuacio.text.toString().toInt()

            lifecycleScope.launch {
                val familiaDao = AppDatabase.getDatabase(this@CreacioTasques).familiaDao()
                val familia = withContext(Dispatchers.IO) { familiaDao.findByName(hijoSeleccionado) }
                if (familia != null) {
                    val nuevaTasca = Tasques(
                        hijo = hijoSeleccionado,
                        nombreTarea = nombreTarea,
                        dificultad = dificultadSeleccionada,
                        puntuacion = puntuacion,
                        fechaVencimiento = fechaSeleccionada
                    )

                    withContext(Dispatchers.IO) {
                        tasquesDao.insert(nuevaTasca)
                    }
                    Toast.makeText(this@CreacioTasques, "Tasca creada correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@CreacioTasques, "Familia no encontrada", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun cargarFamiliasEnSpinner() {
        lifecycleScope.launch {
            val familias = obtenerFamilias()
            val nombresFamilias = obtenerNombresFamilias(familias)

            val adapter = ArrayAdapter(this@CreacioTasques, R.layout.simple_spinner_item, nombresFamilias)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.nomfamilia.adapter = adapter

            binding.nomfamilia.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    val nombreFamiliaSeleccionada = parent.getItemAtPosition(position).toString()
                    val familiaSeleccionada = familias.find { it.nombre == nombreFamiliaSeleccionada }
                    if (familiaSeleccionada != null) {
                        val nombresHijos = familiaSeleccionada.hijos
                        val adapterHijos = ArrayAdapter(this@CreacioTasques, android.R.layout.simple_spinner_item, nombresHijos)
                        adapterHijos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.nomfill.adapter = adapterHijos
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
        }
    }
    private suspend fun obtenerFamilias(): List<Familia> {
        val familiaDao = AppDatabase.getDatabase(this@CreacioTasques).familiaDao()
        return withContext(Dispatchers.IO) { familiaDao.getAll() }
    }
    private fun obtenerNombresFamilias(familias: List<Familia>): List<String> {
        return familias.map { it.nombre }
    }


}