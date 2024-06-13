package com.example.proyectoappsmoviles
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.Responce.Cliente

class ClienteAdapter(private val clientes: List<Cliente>, private val listener: OnCategoriaClickListener) : RecyclerView.Adapter<ClienteAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cliente, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cliente = clientes[position]
        holder.bind(cliente)
        holder.btnEliminar.setOnClickListener { listener.onEliminarClick(cliente ) }
        holder.btnModificar.setOnClickListener { listener.onModificarClick(cliente) }
    }

    override fun getItemCount() = clientes.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreTextView: TextView = itemView.findViewById(R.id.nombreTextView)
        private val direccionTextView: TextView = itemView.findViewById(R.id.direccionTextView)
        private val telefonoTextView: TextView = itemView.findViewById(R.id.telefonoTextView)
        val btnEliminar: Button = itemView.findViewById(R.id.btnEliminar)
        val btnModificar: Button = itemView.findViewById(R.id.btnModificar)

        fun bind(cliente: Cliente) {
            nombreTextView.text = cliente.nombre
            direccionTextView.text = cliente.direccion
            telefonoTextView.text = cliente.telefono
        }
    }

    interface OnCategoriaClickListener {
        fun onEliminarClick(cliente: Cliente)
        fun onModificarClick(cliente: Cliente)
    }
}
