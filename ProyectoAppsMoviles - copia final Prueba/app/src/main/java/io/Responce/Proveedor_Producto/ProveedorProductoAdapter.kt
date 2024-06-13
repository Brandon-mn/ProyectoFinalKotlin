package com.example.proyectoappsmoviles
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.Responce.Proveedor_Producto.ProveedorProducto
class ProveedorProductoAdapter(private val proveedorproductos: List<ProveedorProducto>, private val listener: ProveedorProductoAdapter.OnCategoriaClickListener) : RecyclerView.Adapter<ProveedorProductoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_proveedor_producto, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val proveedorProducto = proveedorproductos[position]
        holder.bind(proveedorProducto)
        holder.btnEliminar.setOnClickListener { listener.onEliminarClick(proveedorProducto ) }
        holder.btnModificar.setOnClickListener { listener.onModificarClick(proveedorProducto) }
    }

    override fun getItemCount() = proveedorproductos.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val idProveedorEditText: TextView = itemView.findViewById(R.id.idProveedorEditText)
        private val idProductoEditText: TextView = itemView.findViewById(R.id.idProductoEditText)
        val btnEliminar: Button = itemView.findViewById(R.id.btnEliminar)
        val btnModificar: Button = itemView.findViewById(R.id.btnModificar)

        fun bind(proveedorProducto: ProveedorProducto) {
            idProveedorEditText.text = proveedorProducto.idProveedor.toString()
            idProductoEditText.text = proveedorProducto.idProducto.toString()
        }
    }

    interface OnCategoriaClickListener {
        fun onEliminarClick(proveedorProducto: ProveedorProducto)
        fun onModificarClick(proveedorProducto: ProveedorProducto)
    }
}