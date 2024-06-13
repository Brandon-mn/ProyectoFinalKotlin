package com.example.proyectoappsmoviles
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.Responce.Venta.Venta

class VentaAdapter(private val ventas: List<Venta>, private val listener: VentaAdapter.OnCategoriaClickListener) : RecyclerView.Adapter<VentaAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_venta, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val venta = ventas[position]
        holder.bind(venta)
        holder.btnEliminar.setOnClickListener { listener.onEliminarClick(venta ) }
        holder.btnModificar.setOnClickListener { listener.onModificarClick(venta) }
    }

    override fun getItemCount() = ventas.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val fechaEditText: TextView = itemView.findViewById(R.id.fechaEditText)
        private val totalEditText: TextView = itemView.findViewById(R.id.totalEditText)
        val btnEliminar: Button = itemView.findViewById(R.id.btnEliminar)
        val btnModificar: Button = itemView.findViewById(R.id.btnModificar)

        fun bind(venta: Venta) {
            fechaEditText.text = venta.fecha
            totalEditText.text = venta.fecha.toString()
        }
    }

    interface OnCategoriaClickListener {
        fun onEliminarClick(venta: Venta)
        fun onModificarClick(venta: Venta)
    }
}