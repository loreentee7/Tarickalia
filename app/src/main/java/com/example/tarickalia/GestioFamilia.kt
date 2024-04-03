package com.example.tarickalia

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.example.tarickalia.bd.usuaris.AppDatabase
import com.example.tarickalia.bd.usuaris.UsuarisDao
import com.example.tarickalia.databinding.ActivityGestioFamiliaBinding
import com.example.tarickalia.familia.Familia
import com.example.tarickalia.familia.FamiliaDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GestioFamilia : AppCompatActivity() {

    private lateinit var binding: ActivityGestioFamiliaBinding
    private lateinit var usuarisDao: UsuarisDao
    private lateinit var familiaDao: FamiliaDao
    private lateinit var drawerLayout: DrawerLayout



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGestioFamiliaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val usernamerebut = intent.getStringExtra("username")
        binding.nompares.text = usernamerebut

        drawerLayout = findViewById(R.id.drawer_layout)


        binding.gohome.setOnClickListener {
            val intent = Intent(this, HomePares::class.java)
            intent.putExtra("username", usernamerebut)
            startActivity(intent)
        }

        binding.menuopen.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        usuarisDao = AppDatabase.getDatabase(this).usuarisDao()
        familiaDao = AppDatabase.getDatabase(this).familiaDao()


        loadAdmins()
        loadFills()


        binding.creafamilia.setOnClickListener {
            val nombreFamilia = binding.nomfamilia.text.toString()
            val admin = binding.admins.selectedItem.toString()

            val familia = Familia(nombre = nombreFamilia, admin = admin, hijos = listOf())

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    familiaDao.insert(familia)
                }

                Toast.makeText(this@GestioFamilia, "Familia creada", Toast.LENGTH_SHORT).show()
                loadFamilias()
            }
        }

        binding.assignarfill.setOnClickListener {
            val nombreFamilia = binding.nomfamiliaspin.selectedItem.toString()
            val hijo = binding.nomfill1.selectedItem.toString()

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val familia = familiaDao.findByName(nombreFamilia)
                    if (familia != null) {
                        familia.hijos = familia.hijos.toMutableList().apply { add(hijo) }
                        familiaDao.update(familia)
                        Toast.makeText(this@GestioFamilia, "Familia actualitzada", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    private fun loadAdmins() {
        lifecycleScope.launch {
            val admins = withContext(Dispatchers.IO) {
                usuarisDao.getAll().filter { it.admin }.map { it.usuari }
            }

            val adapter = ArrayAdapter(this@GestioFamilia, R.layout.spinner_item, admins)
            val spinner: Spinner = findViewById(R.id.admins)
            spinner.adapter = adapter
        }
    }

    private fun loadFills() {
        lifecycleScope.launch {
            val noAdmins = withContext(Dispatchers.IO) {
                usuarisDao.getAll().filter { !it.admin }.map { it.usuari }
            }

            val adapter = ArrayAdapter(this@GestioFamilia, R.layout.spinner_item, noAdmins)
            val spinner: Spinner = findViewById(R.id.nomfill1)
            spinner.adapter = adapter
        }

    }

    private fun loadFamilias() {
        lifecycleScope.launch {
            val familias = withContext(Dispatchers.IO) {
                familiaDao.getAll().map { it.nombre }
            }

            val adapter = ArrayAdapter(this@GestioFamilia, R.layout.spinner_item, familias)
            val spinner: Spinner = findViewById(R.id.nomfamiliaspin)
            spinner.adapter = adapter
        }
    }

}