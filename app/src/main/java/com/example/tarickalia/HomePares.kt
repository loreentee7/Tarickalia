package com.example.tarickalia

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.tarickalia.databinding.ActivityHomeParesBinding

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

        binding.btnPuntuacions.setOnClickListener {
            //val intent = Intent(this, PuntuacionsPares::class.java)
            //startActivity(intent)
        }

        binding.btnGestioFamilia.setOnClickListener {
            val intent = Intent(this, GestioFamilia::class.java)
            intent.putExtra("username", binding.nompares.text.toString())
            startActivity(intent)
        }

        binding.btnRecompenses.setOnClickListener {
            //val intent = Intent(this, RecompensesPares::class.java)
            //startActivity(intent)
        }

        binding.btnTasca.setOnClickListener {
            //val intent = Intent(this, CreacioTasques::class.java)
            //startActivity(intent)
        }

        binding.btnTasquesCompletes.setOnClickListener {
            //val intent = Intent(this, TasquesCompletesPares::class.java)
            //startActivity(intent)
        }

        val usernamerebut = intent.getStringExtra("username")
        binding.nompares.text = usernamerebut
    }
}