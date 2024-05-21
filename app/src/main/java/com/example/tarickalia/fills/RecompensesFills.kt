package com.example.tarickalia.fills

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.example.tarickalia.R
import com.example.tarickalia.RecompensaAdapter
import com.example.tarickalia.api.TarickaliaApi
import com.example.tarickalia.databinding.ActivityRecompensesFillsBinding
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecompensesFills : AppCompatActivity() {

    private lateinit var binding: ActivityRecompensesFillsBinding
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecompensesFillsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val usernamerebut = intent.getStringExtra("username")
        binding.nomfill.text = usernamerebut

        drawerLayout = findViewById(R.id.drawer_layout)

        binding.gohome.setOnClickListener {
            val intent = Intent(this, HomeFIlls::class.java)
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

        val apiService = TarickaliaApi().getApiService()

        lifecycleScope.launch(Dispatchers.IO) {
            val apiService = TarickaliaApi().getApiService()

            val usersResponse = apiService.getUsuarios()
            if (usersResponse.isSuccessful) {
                val users = usersResponse.body()
                val user = users?.find { it.nombreUsuario == usernamerebut }
                val familyId = user?.idFamilia

                if (familyId != null) {
                    val rewardsResponse = apiService.getRecompensasByFamilia(familyId)
                    if (rewardsResponse.isSuccessful) {
                        val rewards = rewardsResponse.body()

                        withContext(Dispatchers.Main) {
                            if (rewards != null && rewards.isNotEmpty()) {
                                val adapter = RecompensaAdapter(rewards, user, apiService, lifecycleScope)
                                binding.recyclerView.adapter = adapter
                            } else {
                                Toast.makeText(this@RecompensesFills, "No hay recompensas disponibles", Toast.LENGTH_SHORT).show()
                            }
                            binding.credit.text = user?.Monedas?.toString() ?: "0"
                        }
                    }
                }
            }
        }
    }
}