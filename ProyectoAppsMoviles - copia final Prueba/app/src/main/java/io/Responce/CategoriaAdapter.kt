package com.example.proyectoappsmoviles
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.Responce.Categoria

class CategoriaAdapter(private val categorias: List<Categoria>, private val listener: OnCategoriaClickListener) : RecyclerView.Adapter<CategoriaAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_categoria, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val categoria = categorias[position]
        holder.bind(categoria)
        holder.btnEliminar.setOnClickListener { listener.onEliminarClick(categoria) }
        holder.btnModificar.setOnClickListener { listener.onModificarClick(categoria) }
    }

    override fun getItemCount() = categorias.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreTextView: TextView = itemView.findViewById(R.id.nombreTextView)
        private val descripcionTextView: TextView = itemView.findViewById(R.id.descripcionTextView)
        private val estadoTextView: TextView = itemView.findViewById(R.id.estadoTextView)
        val btnEliminar: Button = itemView.findViewById(R.id.btnEliminar)
        val btnModificar: Button = itemView.findViewById(R.id.btnModificar)

        fun bind(categoria: Categoria) {
            nombreTextView.text = categoria.nombre
            descripcionTextView.text = categoria.descripcion
            estadoTextView.text = categoria.estado.toString()
        }
    }

    interface OnCategoriaClickListener {
        fun onEliminarClick(categoria: Categoria)
        fun onModificarClick(categoria: Categoria)
    }
}
