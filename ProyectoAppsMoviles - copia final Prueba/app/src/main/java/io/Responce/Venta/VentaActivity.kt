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
import io.Responce.Proveedor_Producto.ProveedorProducto
import io.Responce.Venta.NuevaVenta
import io.Responce.Venta.Venta
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

class VentaActivity : AppCompatActivity(), VentaAdapter.OnCategoriaClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venta)

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
    override fun onEliminarClick(venta: Venta) {
        mostrarDialogoConfirmacionEliminar(venta)
    }
    override fun onModificarClick(venta: Venta) {
        mostrarDialogoModificar(venta)
    }
    private fun obtenerCliente() {
        val call = apiService.obtenerVentas()
        call.enqueue(object : Callback<List<Venta>> {
            override fun onResponse(call: Call<List<Venta>>, response: Response<List<Venta>>) {
                if (response.isSuccessful) {
                    val ventas = response.body()
                    ventas?.let {
                        val proveedorproductoFiltrado = it.filter { venta -> venta.status == 1 }
                        val adapter = VentaAdapter(proveedorproductoFiltrado, this@VentaActivity)
                        recyclerView.adapter = adapter
                    } ?: run {
                        Toast.makeText(this@VentaActivity, "No se encontraron categorías", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Toast.makeText(this@VentaActivity, "Error al obtener datos: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(p0: Call<List<Venta>>, t: Throwable) {
                Toast.makeText(this@VentaActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostrarDialogoConfirmacionEliminar(venta: Venta) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Está seguro de que desea eliminar esta categoría?")

        builder.setPositiveButton("Sí") { dialog, _ ->
            eliminarEmpleado(venta.idVenta)
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
    private fun eliminarEmpleado(idVenta: Int) {
        val call = apiService.eliminarVenta(idVenta)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@VentaActivity, "Categoría eliminada exitosamente", Toast.LENGTH_SHORT).show()
                    obtenerCliente()
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Toast.makeText(this@VentaActivity, "Error al eliminar la categoría: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@VentaActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun insertarEmpleado(nuevaVenta: NuevaVenta) {
        val call = apiService.insertarVenta(
            nuevaVenta.fecha,
            nuevaVenta.total,
            nuevaVenta.status
        )
        call.enqueue(object : Callback<Void> {
            @SuppressLint("SuspiciousIndentation")
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@VentaActivity, "Categoría insertada exitosamente", Toast.LENGTH_SHORT).show()
                    obtenerCliente()
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Toast.makeText(this@VentaActivity, "Error al insertar la categoría: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@VentaActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun mostrarDialogoInsertar() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.activity_insertar_venta, null)
        builder.setView(view)

        val fechaEditText: EditText = view.findViewById(R.id.fechaEditText)
        val totalEditText: EditText = view.findViewById(R.id.totalEditText)
        val statusEditText: EditText = view.findViewById(R.id.statusEditText)
        statusEditText.setText("true")
        statusEditText.isEnabled = false


        val aceptarButton: Button = view.findViewById(R.id.aceptarButton)
        val cancelarButton: Button = view.findViewById(R.id.regresarButton)

        val dialog = builder.create()

        aceptarButton.setOnClickListener {
            val fecha = fechaEditText.text.toString()
            val total = totalEditText.text.toString()
            val status = statusEditText.text.toString()

            if (fecha.isNotEmpty() && total.isNotEmpty()) {
                val nuevoProveedor = NuevaVenta(fecha, total.toInt(), status = true) // Estado predeterminado como 1 (true)
                insertarEmpleado(nuevoProveedor)
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
    private fun modificarCliente(nuevaVenta: Venta) {
        val call = apiService.actualizarVenta(
            nuevaVenta.idVenta,
            fecha = nuevaVenta.fecha,
            total = nuevaVenta.total.toInt()
        )
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@VentaActivity, "Categoría modificada exitosamente", Toast.LENGTH_SHORT).show()
                    obtenerCliente()
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Toast.makeText(this@VentaActivity, "Error al modificar la categoría: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@VentaActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun mostrarDialogoModificar(venta: Venta) {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.activity_insertar_venta, null)
        builder.setView(view)

        val fechaEditText: EditText = view.findViewById(R.id.fechaEditText)
        val totalEditText: EditText = view.findViewById(R.id.totalEditText)
        val statusEditText: EditText = view.findViewById(R.id.statusEditText)

        fechaEditText.setText(venta.fecha)
        totalEditText.setText(venta.total.toString())
        statusEditText.setText(venta.status.toString())
        statusEditText.isEnabled = false

        val aceptarButton: Button = view.findViewById(R.id.aceptarButton)
        val cancelarButton: Button = view.findViewById(R.id.regresarButton)

        val dialog = builder.create()

        aceptarButton.setOnClickListener {
            val fecha = fechaEditText.text.toString()
            val total = totalEditText.text.toString()
            val status = statusEditText.text.toString()

            if (fecha.isNotEmpty() && total.isNotEmpty()  && status.isNotEmpty()) {
                val proveedorProducto = Venta(venta.idVenta, fecha, total.toInt(), status.toInt())
                modificarCliente(proveedorProducto)
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