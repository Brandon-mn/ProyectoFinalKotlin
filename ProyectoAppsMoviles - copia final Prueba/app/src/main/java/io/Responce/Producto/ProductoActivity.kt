package com.example.proyectoappsmoviles
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.Responce.ApiService
import io.Responce.Empleado.Empleado

import io.Responce.Producto.Producto
import io.Responce.Producto.NuevoProducto
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

class ProductoActivity : AppCompatActivity(), ProductoAdapter.OnCategoriaClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_producto)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val insertarButton: Button = findViewById(R.id.btnInsertar)
        insertarButton.setOnClickListener {
            mostrarDialogoInsertar()
        }
        val retrofit = Retrofit.Builder()
            .baseUrl("https://10.0.2.2:7267/")
            .client(unsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        obtenerCliente()
    }
    private fun obtenerCliente() {
        val call = apiService.obtenerProducto()
        call.enqueue(object : Callback<List<Producto>> {
            override fun onResponse(call: Call<List<Producto>>, response: Response<List<Producto>>) {
                if (response.isSuccessful) {
                    val productos = response.body()
                    productos?.let {
                        val productosFlitrados = it.filter { producto -> producto.status == 1 }
                        val adapter = ProductoAdapter(productosFlitrados, this@ProductoActivity)
                        recyclerView.adapter = adapter
                    } ?: run {
                        Toast.makeText(this@ProductoActivity, "No se encontraron categorías", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Toast.makeText(this@ProductoActivity, "Error al obtener datos: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(p0: Call<List<Producto>>, t: Throwable) {
                Toast.makeText(this@ProductoActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    override fun onEliminarClick(producto: Producto) {
        mostrarDialogoConfirmacionEliminar(producto)    }

    override fun onModificarClick(producto: Producto) {
    mostrarDialogoModificar(producto)
    }
    private fun unsafeOkHttpClient(): OkHttpClient {
        return try {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}

                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}

                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            val sslSocketFactory = sslContext.socketFactory

            OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier { _, _ -> true }
                .build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
    private fun mostrarDialogoConfirmacionEliminar(producto: Producto) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Está seguro de que desea eliminar esta categoría?")

        builder.setPositiveButton("Sí") { dialog, _ ->
            eliminarEmpleado(producto.idProducto)
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
    private fun eliminarEmpleado(idProducto: Int) {
        val call = apiService.eliminarProducto(idProducto)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProductoActivity, "Categoría eliminada exitosamente", Toast.LENGTH_SHORT).show()
                    obtenerCliente()
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Toast.makeText(this@ProductoActivity, "Error al eliminar la categoría: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ProductoActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun insertarEmpleado(nuevoProducto: NuevoProducto) {
        val call = apiService.insertarProducto(
            nuevoProducto.nombre,
            nuevoProducto.descripcion,
            nuevoProducto.precio.toInt(),
            nuevoProducto.status
        )
        call.enqueue(object : Callback<Void> {
            @SuppressLint("SuspiciousIndentation")
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProductoActivity, "Categoría insertada exitosamente", Toast.LENGTH_SHORT).show()
                    obtenerCliente()
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Toast.makeText(this@ProductoActivity, "Error al insertar la categoría: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ProductoActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun mostrarDialogoInsertar() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.activity_insertar_producto, null)
        builder.setView(view)

        val nombreEditText: EditText = view.findViewById(R.id.nombreEditText)
        val descripcionEditText: EditText = view.findViewById(R.id.descripcionEditText)
        val precioEditText: EditText = view.findViewById(R.id.precioEditText)
        val statusEditText: EditText = view.findViewById(R.id.statusEditText)
        statusEditText.setText("true")
        statusEditText.isEnabled = false


        val aceptarButton: Button = view.findViewById(R.id.aceptarButton)
        val cancelarButton: Button = view.findViewById(R.id.regresarButton)

        val dialog = builder.create()

        aceptarButton.setOnClickListener {
            val nombre = nombreEditText.text.toString()
            val descripcion = descripcionEditText.text.toString()
            val precio = precioEditText.text.toString()
            val status = statusEditText.text.toString()

            if (nombre.isNotEmpty() && descripcion.isNotEmpty()) {
                val nuevoProducto = NuevoProducto(nombre, descripcion, precio.toInt(), status = true) // Estado predeterminado como 1 (true)
                insertarEmpleado(nuevoProducto)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        cancelarButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
    private fun modificarCliente(nuevoProducto: Producto) {
        val call = apiService.actualizarProducto(
            nuevoProducto.idProducto,
            nombre = nuevoProducto.nombre,
            descripcion = nuevoProducto.descripcion,
            precio = nuevoProducto.precio.toInt()
        )
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProductoActivity, "Categoría modificada exitosamente", Toast.LENGTH_SHORT).show()
                    obtenerCliente()
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Toast.makeText(this@ProductoActivity, "Error al modificar la categoría: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ProductoActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun mostrarDialogoModificar(producto: Producto) {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.activity_insertar_producto, null)
        builder.setView(view)

        val nombreEditText: EditText = view.findViewById(R.id.nombreEditText)
        val descripcionEditText: EditText = view.findViewById(R.id.descripcionEditText)
        val precioEditText: EditText = view.findViewById(R.id.precioEditText)
        val statusEditText: EditText = view.findViewById(R.id.statusEditText)

        nombreEditText.setText(producto.nombre)
        descripcionEditText.setText(producto.descripcion)
        precioEditText.setText(producto.precio.toString())
        statusEditText.setText(producto.status.toString())
        statusEditText.isEnabled = false

        val aceptarButton: Button = view.findViewById(R.id.aceptarButton)
        val cancelarButton: Button = view.findViewById(R.id.regresarButton)

        val dialog = builder.create()

        aceptarButton.setOnClickListener {
            val nombre = nombreEditText.text.toString()
            val puesto = descripcionEditText.text.toString()
            val salario = precioEditText.text.toString()
            val status = statusEditText.text.toString()

            if (nombre.isNotEmpty() && puesto.isNotEmpty() && salario.isNotEmpty() && status.isNotEmpty()) {
                val producto = Producto(producto.idProducto, nombre, puesto, salario.toInt(), status.toInt())
                modificarCliente(producto)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        cancelarButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}