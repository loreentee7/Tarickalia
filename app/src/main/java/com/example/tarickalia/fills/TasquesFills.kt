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
import com.example.tarickalia.api.TarickaliaApi
import com.example.tarickalia.databinding.ActivityTasquesFillsBinding
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TasquesFills : AppCompatActivity() {

    private lateinit var binding: ActivityTasquesFillsBinding
    private lateinit var drawerLayout: DrawerLayout
    private var userId: Int? = null
    private var tasksAdapter = TasksAdapter(mutableListOf(), lifecycleScope, true, false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            binding = ActivityTasquesFillsBinding.inflate(layoutInflater)
            setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = tasksAdapter


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
        // consulta a la api per obtenir les tasques de l'usuari
        lifecycleScope.launch(Dispatchers.IO) {
            val api = TarickaliaApi()
            val usersResponse = api.getApiService().getUsuarios()
            if (usersResponse.isSuccessful) {
                val users = usersResponse.body()
                val user = users?.find { it.nombreUsuario == usernamerebut }
                userId = user?.id
                if (userId != null) {
                    val tasksResponse = api.getApiService().getTareasByUsuario(userId!!)
                    if (tasksResponse.isSuccessful) {
                        val tasks = tasksResponse.body()
                        val uncompletedTasks = tasks?.filter { it.completada?.let { !it } ?: false }?.toMutableList()
                        withContext(Dispatchers.Main) {
                            tasksAdapter = TasksAdapter(uncompletedTasks, lifecycleScope, true, false)
                            binding.recyclerView.layoutManager = LinearLayoutManager(this@TasquesFills)
                            binding.recyclerView.adapter = tasksAdapter
                        }
                    } else {
                        Log.e("TasquesFills", "Error obteniendo tareas: ${tasksResponse.errorBody()}")
                    }
                } else {
                    Log.e("TasquesFills", "Usuario no encontrado")
                }
            } else {
                Log.e("TasquesFills", "Error obteniendo usuarios: ${usersResponse.errorBody()}")
            }
        }
    }
}