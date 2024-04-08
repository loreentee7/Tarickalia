package com.example.tarickalia.pares

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.tarickalia.R
import com.example.tarickalia.databinding.ActivityHomeParesBinding
import com.google.android.material.navigation.NavigationView

class HomePares : AppCompatActivity() {
    private lateinit var binding: ActivityHomeParesBinding
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeParesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = findViewById(R.id.drawer_layout)

        binding.menuopen.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
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

        binding.btnPuntuacions.setOnClickListener {
            val intent = Intent(this, PuntuacionsPares::class.java)
            intent.putExtra("username", binding.nompares.text.toString())
            startActivity(intent)
        }

        binding.btnGestioFamilia.setOnClickListener {
            val intent = Intent(this, GestioFamilia::class.java)
            intent.putExtra("username", binding.nompares.text.toString())
            startActivity(intent)
        }

        binding.btnRecompenses.setOnClickListener {
            val intent = Intent(this, CreacioRecompenses::class.java)
            intent.putExtra("username", binding.nompares.text.toString())
            startActivity(intent)
        }

        binding.btnTasca.setOnClickListener {
            val intent = Intent(this, CreacioTasques::class.java)
            intent.putExtra("username", binding.nompares.text.toString())
            startActivity(intent)
        }

        binding.btnTasquesCompletes.setOnClickListener {
            val intent = Intent(this, TasquesCompletesPares::class.java)
            intent.putExtra("username", binding.nompares.text.toString())
            startActivity(intent)
        }

        val usernamerebut = intent.getStringExtra("username")
        binding.nompares.text = usernamerebut
    }
}