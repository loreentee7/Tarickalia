package com.example.tarickalia.pares

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tarickalia.api.Models.Tarea
import com.example.tarickalia.R
import com.example.tarickalia.api.Models.Usuario
import com.example.tarickalia.api.TarickaliaApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TareaAdapter(private var tasks: MutableList<Tarea>?, private val usuario: Usuario, private val showButton: Boolean) : RecyclerView.Adapter<TareaAdapter.TaskViewHolder>() {

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taskName: TextView = view.findViewById(R.id.nombreTarea)
        val taskScore: TextView = view.findViewById(R.id.Puntuacio)
        val taskDueDate: TextView = view.findViewById(R.id.data)
        val btnComplete: Button = view.findViewById(R.id.btnComplete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tarea_item2, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks?.get(position)
        holder.taskName.text = task?.nombre
        holder.taskScore.text = task?.puntuacion.toString()
        holder.taskDueDate.text = task?.fechaCaducidad
        holder.btnComplete.visibility = if (showButton) View.VISIBLE else View.GONE

        holder.btnComplete.setOnClickListener {
            task?.aprobada = true
            updateTask(task)
            tasks?.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, tasks?.size ?: 0)
            usuario.monedas = (usuario.monedas ?: 0) + (task?.puntuacion ?: 0)
            updateUser(usuario)
        }
    }

    override fun getItemCount() = tasks?.size ?: 0

    private fun updateTask(task: Tarea?) {
        task?.let {
            GlobalScope.launch(Dispatchers.IO) {
                val apiService = TarickaliaApi().getApiService()
                apiService.putTarea(it.id ?: 0, it)
            }
        }
    }

    private fun updateUser(user: Usuario) {
        GlobalScope.launch(Dispatchers.IO) {
            val apiService = TarickaliaApi().getApiService()
            apiService.putUsuario(user.id ?: 0, user)
        }
    }

    fun updateTareas(newTareas: MutableList<Tarea>?) {
        tasks = newTareas
        notifyDataSetChanged()
    }
}