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


        loadAdmins()
        loadFamilies()
        loadFills()

        binding.crearfamilia.setOnClickListener {
            createFamilia()
        }


        binding.assignarfills.setOnClickListener {
            val nombreFamilia = binding.nomfamiliaspin.selectedItem.toString()
            val hijo = binding.nomfill1.selectedItem.toString()
        }

    }

    private fun loadAdmins() {

    }

    private fun loadFamilies() {

    }

    private fun loadFills() {

    }

    private fun createFamilia() {
        val familiaName = binding.nomfamilia.text?.toString()
        val selectedItem = binding.nomadmin.selectedItem

        val adminName = if (selectedItem != null) {
            selectedItem.toString()
        } else {
            Toast.makeText(this, "No admin selected", Toast.LENGTH_SHORT).show()
            return
        }

        if (familiaName.isNullOrEmpty()) {
            Toast.makeText(this, "Seleccioa tot", Toast.LENGTH_SHORT).show()
            return
        }

        // val familia = Familia(nombre = familiaName, admin = adminName)

    }
}
