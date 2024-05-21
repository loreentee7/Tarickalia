package com.example.tarickalia

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.example.tarickalia.api.ApiServices
import com.example.tarickalia.api.Models.Recompensa
import com.example.tarickalia.api.Models.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecompensaAdapter(
    private val recompensas: List<Recompensa>?,
    private val usuario: Usuario?,
    private val apiService: ApiServices,
    private val lifecycleScope: LifecycleCoroutineScope
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
            holder.nombreRecompensa.text = recompensa.Nombre
            holder.puntuacionRecompensa.text = recompensa.Puntuacion.toString()

            holder.botonReclamar.setOnClickListener {
                if ((usuario?.Monedas ?: 0) >= (recompensa.Puntuacion ?: 0)) {
                    usuario?.Monedas = (usuario?.Monedas ?: 0) - (recompensa.Puntuacion ?: 0)
                    recompensa.Reclamada = true
                    lifecycleScope.launch(Dispatchers.IO) {
                        usuario?.let { apiService.putUsuario(it.id ?: 0, it) }
                        recompensa.let { apiService.putRecompensa(it.Id ?: 0, it) }
                        withContext(Dispatchers.Main) {
                            notifyItemChanged(position)
                            Toast.makeText(holder.itemView.context, "Recompensa reclamada!", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(holder.itemView.context, "No tens suficients punts per reclamar aquesta recompensa", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return recompensas?.size ?: 0
    }
}