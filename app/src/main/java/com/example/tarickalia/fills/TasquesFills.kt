package com.example.tarickalia.fills

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.tarickalia.R
import com.example.tarickalia.databinding.ActivityTasquesFillsBinding
import com.example.tarickalia.pares.CreacioRecompenses
import com.example.tarickalia.pares.CreacioTasques
import com.example.tarickalia.pares.GestioFamilia
import com.example.tarickalia.pares.HomePares
import com.example.tarickalia.pares.PuntuacionsPares
import com.example.tarickalia.pares.TasquesCompletesPares
import com.google.android.material.navigation.NavigationView

class TasquesFills : AppCompatActivity() {

    private lateinit var binding: ActivityTasquesFillsBinding
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTasquesFillsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val usernamerebut = intent.getStringExtra("username")
        binding.nomfill.text = usernamerebut

        binding.gohome.setOnClickListener {
            val intent = Intent(this, HomePares::class.java)
            intent.putExtra("username", usernamerebut)
            startActivity(intent)
        }

        binding.menuopen.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.Tasques -> {
                    val intent = Intent(this, TasquesFills::class.java)
                    intent.putExtra("username", binding.nomfill.text.toString())
                    startActivity(intent)
                }
                R.id.TasquesCompletes -> {
                    val intent = Intent(this, TasquesCompletadesFills::class.java)
                    intent.putExtra("username", binding.nomfill.text.toString())
                    startActivity(intent)
                }
                R.id.Recompenses -> {
                    val intent = Intent(this, RecompensesFills::class.java)
                    intent.putExtra("username", binding.nomfill.text.toString())
                    startActivity(intent)
                }
                R.id.Puntuacions -> {
                    val intent = Intent(this, PuntuacionsFills::class.java)
                    intent.putExtra("username", binding.nomfill.text.toString())
                    startActivity(intent)
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
}