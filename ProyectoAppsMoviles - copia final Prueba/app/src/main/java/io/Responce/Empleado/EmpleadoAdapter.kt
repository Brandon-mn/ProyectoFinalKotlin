package com.example.proyectoappsmoviles
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.Responce.Empleado.Empleado

class EmpleadoAdapter(private val empleados: List<Empleado>, private val listener: OnCategoriaClickListener) : RecyclerView.Adapter<EmpleadoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_empleado, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val empleado = empleados[position]
        holder.bind(empleado)
        holder.btnEliminar.setOnClickListener { listener.onEliminarClick(empleado ) }
        holder.btnModificar.setOnClickListener { listener.onModificarClick(empleado) }
    }

    override fun getItemCount() = empleados.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreTextView: TextView = itemView.findViewById(R.id.nombreTextView)
        private val puestoTextView: TextView = itemView.findViewById(R.id.puestoTextView)
        private val salarioextView: TextView = itemView.findViewById(R.id.salarioTextView)
        val btnEliminar: Button = itemView.findViewById(R.id.btnEliminar)
        val btnModificar: Button = itemView.findViewById(R.id.btnModificar)

        fun bind(empleado: Empleado) {
            nombreTextView.text = empleado.nombre
            puestoTextView.text = empleado.puesto
            salarioextView.text = empleado.salario.toInt().toString()
        }
    }

    interface OnCategoriaClickListener {
        fun onEliminarClick(empleado: Empleado)
        fun onModificarClick(empleado: Empleado)
    }
}
