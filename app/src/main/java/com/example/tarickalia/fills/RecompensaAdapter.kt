package com.example.tarickalia.fills

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.example.tarickalia.R
import com.example.tarickalia.api.ApiServices
import com.example.tarickalia.api.Models.Recompensa
import com.example.tarickalia.api.Models.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecompensaAdapter(
    var recompensas: MutableList<Recompensa>?,
    private val username: String?,
    private val apiService: ApiServices,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val creditTextView: TextView
) : RecyclerView.Adapter<RecompensaAdapter.RecompensaViewHolder>() {

    inner class RecompensaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreRecompensa: TextView = itemView.findViewById(R.id.nombreRecompensa)
        val puntuacionRecompensa: TextView = itemView.findViewById(R.id.puntuacionRecompensa)
        val botonReclamar: Button = itemView.findViewById(R.id.botonReclamar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecompensaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recompensa_item, parent, false)
        return RecompensaViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecompensaViewHolder, position: Int) {
        val recompensa = recompensas?.get(position)
        if (recompensa != null) {
            holder.nombreRecompensa.text = recompensa.nombre
            holder.puntuacionRecompensa.text = recompensa.puntuacion.toString()

            holder.botonReclamar.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    val usersResponse = apiService.getUsuarios()
                    if (usersResponse.isSuccessful) {
                        val users = usersResponse.body()
                        val user = users?.find { it.nombreUsuario == username }
                        if ((user?.monedas ?: 0) >= (recompensa.puntuacion ?: 0)) {
                            val newMonedas = (user?.monedas ?: 0) - (recompensa.puntuacion ?: 0)
                            recompensa.reclamada = true
                            user?.let {
                                it.monedas = newMonedas
                                newMonedas + 0
                                val response = apiService.putUsuario(it.id ?: 0, it)
                                if (response.isSuccessful) {
                                    it.monedas = response.body()?.monedas
                                    withContext(Dispatchers.Main) {
                                        creditTextView.text = it.monedas.toString()
                                    }
                                    recompensas?.removeAt(position)
                                    withContext(Dispatchers.Main) {
                                        notifyItemRemoved(position)
                                        notifyItemRangeChanged(position, recompensas?.size ?: 0)
                                    }
                                } else {
                                    it.monedas = (user?.monedas ?: 0) + (recompensa.puntuacion ?: 0)
                                    recompensa.reclamada = false
                                }
                            }
                            recompensa.let { apiService.putRecompensa(it.id ?: 0, it) }
                            withContext(Dispatchers.Main) {
                                if (recompensa.reclamada == true) {
                                    Toast.makeText(holder.itemView.context, "Recompensa reclamada!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(holder.itemView.context, "No tens suficients punts per reclamar aquesta recompensa", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(holder.itemView.context, "No tens suficients punts per reclamar aquesta recompensa", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return recompensas?.size ?: 0
    }
}