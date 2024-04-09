package com.example.tarickalia

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tarickalia.databinding.ActivityMainBinding
import com.example.tarickalia.fills.HomeFIlls
import com.example.tarickalia.pares.HomePares
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.debugf.setOnClickListener() {
            startActivity(Intent(this, HomeFIlls::class.java))
        }

        binding.debugp.setOnClickListener() {
            startActivity(Intent(this, HomePares::class.java))
        }

        binding.login.setOnClickListener() {
            val username = binding.editTextTextEmailAddress.text.toString()
            val password = binding.editTextTextPassword.text.toString()

        }

        binding.register.setOnClickListener() {
            startActivity(Intent(this, Register::class.java))
        }
    }

}