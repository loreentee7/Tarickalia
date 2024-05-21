package com.example.tarickalia.fills

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.tarickalia.R
import com.example.tarickalia.api.Models.Tarea
import com.example.tarickalia.api.TarickaliaApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TasksAdapter(private val tasks: MutableList<Tarea>?, private val lifecycleScope: CoroutineScope) : RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taskName: TextView = view.findViewById(R.id.nombreTarea)
        val taskScore: TextView = view.findViewById(R.id.Puntuacio)
        val taskDueDate: TextView = view.findViewById(R.id.data)
        val btnComplete: Button = view.findViewById(R.id.btnComplete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tarea_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks?.get(position)
        holder.taskName.text = task?.nombre
        holder.taskScore.text = task?.puntuacion.toString()
        holder.taskDueDate.text = task?.fechaCaducidad

        holder.btnComplete.setOnClickListener {
            task?.let {
                lifecycleScope.launch(Dispatchers.IO) {
                    val apiService = TarickaliaApi().getApiService()
                    val updatedTask = it.copy(completada = true)
                    val response = apiService.putTarea(it.id!!, updatedTask)

                    if (response.isSuccessful) {
                        withContext(Dispatchers.Main) {
                            removeTask(position)
                            Toast.makeText(holder.itemView.context, "Tarea completada", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(holder.itemView.context, "Error al completar la tarea", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount() = tasks?.size ?: 0

    fun removeTask(position: Int) {
        tasks?.removeAt(position)
        notifyItemRemoved(position)
    }


}