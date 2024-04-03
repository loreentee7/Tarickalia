package com.example.tarickalia

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.tarickalia.databinding.ActivityHomeFillsBinding

class HomeFIlls : AppCompatActivity() {

    private lateinit var binding: ActivityHomeFillsBinding
    private lateinit var drawerLayout: DrawerLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeFillsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = findViewById(R.id.drawer_layout)

        binding.menuopen.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.btnPuntuacions.setOnClickListener {
            //val intent = Intent(this, PuntuacionsFills::class.java)
            //startActivity(intent)
        }

        binding.btntasquescompletes.setOnClickListener {
            //val intent = Intent(this, TasquesCompletesFills::class.java)
            //startActivity(intent)
        }

        binding.btnRecompenses.setOnClickListener {
            //val intent = Intent(this, RecompensesFills::class.java)
            //startActivity(intent)
        }

        binding.btntasques.setOnClickListener {
            //val intent = Intent(this, TasquesFills::class.java)
            //startActivity(intent)
        }

        val usernamerebut = intent.getStringExtra("username")
        binding.nomfill.text = usernamerebut

    }
}