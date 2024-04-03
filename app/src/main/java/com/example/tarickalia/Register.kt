package com.example.tarickalia

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tarickalia.bd.usuaris.AppDatabase
import com.example.tarickalia.bd.usuaris.Usuaris
import com.example.tarickalia.databinding.ActivityRegisterBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Register : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.register.setOnClickListener() {
            val password = binding.password.text.toString()
            val confirmPassword = binding.confirmarpassword.text.toString()

            if (password == confirmPassword) {
                val usuari = binding.username.text.toString()
                val contrasenya = binding.password.text.toString()
                val nom = binding.nom.text.toString()
                val cognom = binding.cognom.text.toString()
                val genre = if (binding.genere.isChecked) true else false
                val admin = binding.admin.isChecked

                val user = Usuaris(usuari = usuari, contrasenya = contrasenya, nom = nom, cognom = cognom, genre = genre, admin = admin)

                val userDao = AppDatabase.getDatabase(this).usuarisDao()
                GlobalScope.launch(Dispatchers.IO) {
                    userDao.insert(user)
                }

                startActivity(Intent(this, MainActivity::class.java))
            } else {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            }
        }

    }
}