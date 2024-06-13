package com.example.proyectoappsmoviles

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class Menu : ComponentActivity() {
    private lateinit var btnCategoria: Button
    private lateinit var btnCliente: Button
    private lateinit var btnEmpleado: Button
    private lateinit var btnProducto: Button
    private lateinit var btnProveedor: Button
    private lateinit var btnProveedorProducto: Button
    private lateinit var btnVenta: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        btnCategoria = findViewById(R.id.btnCategoria)
        btnCliente = findViewById(R.id.btnCliente)
        btnEmpleado = findViewById(R.id.btnEmpleado)
        btnProducto = findViewById(R.id.btnProducto)
        btnProveedor = findViewById(R.id.btnProveedor)
        btnProveedorProducto = findViewById(R.id.btnProveedorProducto)
        btnVenta = findViewById(R.id.btnVenta)

        btnCategoria.setOnClickListener {
            val intent = Intent(this@Menu, CategoriaActivity::class.java)
            startActivity(intent)
        }

        btnCliente.setOnClickListener {
            val intent = Intent(this@Menu, ClienteActivity::class.java)
            startActivity(intent)
        }
        btnEmpleado.setOnClickListener {
            val intent = Intent(this@Menu, EmpleadoActivity::class.java)
            startActivity(intent)
        }
        btnProducto.setOnClickListener {
            val intent = Intent(this@Menu, ProductoActivity::class.java)
            startActivity(intent)
        }
        btnProveedor.setOnClickListener {
            val intent = Intent(this@Menu, ProveedorActivity::class.java)
            startActivity(intent)
        }
        btnProveedorProducto.setOnClickListener {
            val intent = Intent(this@Menu, ProveedorProductoActivity::class.java)
            startActivity(intent)
        }
        btnVenta.setOnClickListener {
            val intent = Intent(this@Menu, VentaActivity::class.java)
            startActivity(intent)
        }
    }
}