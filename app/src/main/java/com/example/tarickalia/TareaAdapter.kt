package com.example.tarickalia

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tarickalia.api.Models.Tarea

class TareaAdapter(private val tareas: List<Tarea>) : RecyclerView.Adapter<TareaAdapter.TareaViewHolder>() {

    class TareaViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview, parent, false)
        return TareaViewHolder(view)
    }

    override fun onBindViewHolder(holder: TareaViewHolder, position: Int) {
        val tarea = tareas[position]
        holder.view.findViewById<TextView>(R.id.task_name).text = tarea.Nombre
        holder.view.findViewById<TextView>(R.id.task_score).text = tarea.Puntuacion.toString()
        holder.view.findViewById<TextView>(R.id.child_name).text = tarea.IdUsuarioNavigation?.nombreUsuario
    }

    override fun getItemCount() = tareas.size
}