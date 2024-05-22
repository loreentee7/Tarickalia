package com.example.tarickalia.fills

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tarickalia.R
import com.example.tarickalia.api.ApiServices
import com.example.tarickalia.api.Models.Usuario
import com.example.tarickalia.api.TarickaliaApi
import com.example.tarickalia.databinding.ActivityRecompensesFillsBinding
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecompensesFills : AppCompatActivity() {

    private lateinit var binding: ActivityRecompensesFillsBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var recompensaAdapter: RecompensaAdapter
    private lateinit var apiService: ApiServices

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecompensesFillsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = TarickaliaApi().getApiService()

        val usernamerebut = intent.getStringExtra("username")
        binding.nomfill.text = usernamerebut

        val creditTextView = binding.credit
        recompensaAdapter = RecompensaAdapter(mutableListOf(), usernamerebut, apiService, lifecycleScope, creditTextView)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = recompensaAdapter

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


        // consulta a la api per obtenir les recompenses no reclamades
        lifecycleScope.launch(Dispatchers.IO) {
            val api = TarickaliaApi()
            val usersResponse = api.getApiService().getUsuarios()
            if (usersResponse.isSuccessful) {
                val users = usersResponse.body()
                val user = users?.find { it.nombreUsuario == usernamerebut }
                val familyId = user?.idFamilia
                if (familyId != null) {
                    val rewardsResponse = api.getApiService().getRecompensasByFamilia(familyId)
                    if (rewardsResponse.isSuccessful) {
                        val rewards = rewardsResponse.body()
                        val unclaimedRewards = rewards?.filter { it.reclamada == false }
                        withContext(Dispatchers.Main) {
                            recompensaAdapter.recompensas = unclaimedRewards?.toMutableList()
                            recompensaAdapter.notifyDataSetChanged()
                        }
                    } else {
                        Log.e("RecompensesFills", "Error obteniendo recompensas: ${rewardsResponse.errorBody()}")
                    }
                } else {
                    Log.e("RecompensesFills", "Usuario no encontrado")
                }
            } else {
                Log.e("RecompensesFills", "Error obteniendo usuarios: ${usersResponse.errorBody()}")
            }
        }
    }

    // funcio per carregar les families en el spinner
    override fun onResume() {
        super.onResume()

        val usernamerebut = intent.getStringExtra("username")
        binding.nomfill.text = usernamerebut

        lifecycleScope.launch(Dispatchers.IO) {
            val apiService = TarickaliaApi().getApiService()

            val usersResponse = apiService.getUsuarios()
            if (usersResponse.isSuccessful) {
                val users = usersResponse.body()
                val user = users?.find { it.nombreUsuario == usernamerebut }

                withContext(Dispatchers.Main) {
                    binding.credit.text = user?.monedas?.toString() ?: "0"
                }
            }
        }
    }
}