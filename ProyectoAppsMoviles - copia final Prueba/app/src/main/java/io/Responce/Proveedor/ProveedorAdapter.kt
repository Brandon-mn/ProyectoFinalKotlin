package com.example.proyectoappsmoviles
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.Responce.Proveedor.Proveedor

class ProveedorAdapter(private val proveedores: List<Proveedor>, private val listener: ProveedorAdapter.OnCategoriaClickListener) : RecyclerView.Adapter<ProveedorAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_proveedor, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val proveedor = proveedores[position]
        holder.bind(proveedor)
        holder.btnEliminar.setOnClickListener { listener.onEliminarClick(proveedor ) }
        holder.btnModificar.setOnClickListener { listener.onModificarClick(proveedor) }
    }

    override fun getItemCount() = proveedores.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreTextView: TextView = itemView.findViewById(R.id.nombreTextView)
        private val direccionTextView: TextView = itemView.findViewById(R.id.direccionTextView)
        private val telefonoTextView: TextView = itemView.findViewById(R.id.telefonoTextView)
        val btnEliminar: Button = itemView.findViewById(R.id.btnEliminar)
        val btnModificar: Button = itemView.findViewById(R.id.btnModificar)

        fun bind(proveedor: Proveedor) {
            nombreTextView.text = proveedor.nombre
            direccionTextView.text = proveedor.direccion
            telefonoTextView.text = proveedor.telefono
        }
    }

    interface OnCategoriaClickListener {
        fun onEliminarClick(proveedor: Proveedor)
        fun onModificarClick(proveedor: Proveedor)
    }
}