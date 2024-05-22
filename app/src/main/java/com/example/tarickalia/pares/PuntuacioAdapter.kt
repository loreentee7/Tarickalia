package com.example.tarickalia.pares

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tarickalia.R
import com.example.tarickalia.api.Models.Tarea
import com.example.tarickalia.api.Models.Usuario

class PuntuacioAdapter(
    private var tareas: MutableList<Tarea>?,
    private var usuarios: List<Usuario>?
) : RecyclerView.Adapter<PuntuacioAdapter.PuntuacioViewHolder>() {

    inner class PuntuacioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreTarea: TextView = itemView.findViewById(R.id.nombreTarea)
        val puntuacionTarea: TextView = itemView.findViewById(R.id.puntuacionTarea)
        val nombreHijo: TextView = itemView.findViewById(R.id.nombreHijo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PuntuacioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.puntuacio_item, parent, false)
        return PuntuacioViewHolder(view)
    }

    override fun onBindViewHolder(holder: PuntuacioViewHolder, position: Int) {
        val tarea = tareas?.get(position)
        if (tarea != null) {
            holder.nombreTarea.text = tarea.nombre
            holder.puntuacionTarea.text = tarea.puntuacion.toString()
            val hijo = usuarios?.find { it.id == tarea.idUsuario }
            holder.nombreHijo.text = hijo?.nombreUsuario
        }
    }

    override fun getItemCount(): Int {
        return tareas?.size ?: 0
    }

    fun updateTareas(newTareas: MutableList<Tarea>?) {
        tareas = newTareas
        notifyDataSetChanged()
    }
}