package com.example.proyectoappsmoviles
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.Responce.Producto.Producto

class ProductoAdapter(private val productos: List<Producto>, private val listener: ProductoAdapter.OnCategoriaClickListener) : RecyclerView.Adapter<ProductoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_producto, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = productos[position]
        holder.bind(producto)
        holder.btnEliminar.setOnClickListener { listener.onEliminarClick(producto ) }
        holder.btnModificar.setOnClickListener { listener.onModificarClick(producto) }
    }

    override fun getItemCount() = productos.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreTextView: TextView = itemView.findViewById(R.id.nombreTextView)
        private val puestoTextView: TextView = itemView.findViewById(R.id.descripcionTextView)
        private val salarioextView: TextView = itemView.findViewById(R.id.precioTextView)
        val btnEliminar: Button = itemView.findViewById(R.id.btnEliminar)
        val btnModificar: Button = itemView.findViewById(R.id.btnModificar)

        fun bind(producto: Producto) {
            nombreTextView.text = producto.nombre
            puestoTextView.text = producto.descripcion
            salarioextView.text = producto.precio.toInt().toString()
        }
    }

    interface OnCategoriaClickListener {
        fun onEliminarClick(producto: Producto  )
        fun onModificarClick(producto: Producto)
    }
}