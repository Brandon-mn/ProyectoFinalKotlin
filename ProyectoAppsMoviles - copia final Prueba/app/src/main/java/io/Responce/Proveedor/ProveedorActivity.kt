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
import com.example.proyectoappsmoviles.ProductoAdapter
import com.example.proyectoappsmoviles.ProveedorAdapter
import com.example.proyectoappsmoviles.R
import io.Responce.ApiService
import io.Responce.Empleado.Empleado
import io.Responce.Proveedor.NuevoProveedor
import io.Responce.Proveedor.Proveedor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

class ProveedorActivity : AppCompatActivity(), ProveedorAdapter.OnCategoriaClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proveedor)

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
    override fun onEliminarClick(proveedor: Proveedor) {
        mostrarDialogoConfirmacionEliminar(proveedor)
    }

    override fun onModificarClick(proveedor: Proveedor) {
      mostrarDialogoModificar(proveedor)
    }
    private fun obtenerCliente() {
        val call = apiService.obtenerPorveedor()
        call.enqueue(object : Callback<List<Proveedor>> {
            override fun onResponse(call: Call<List<Proveedor>>, response: Response<List<Proveedor>>) {
                if (response.isSuccessful) {
                    val proveeodores = response.body()
                    proveeodores?.let {
                        val proveedorFiltrado = it.filter { proveedor -> proveedor.status == 1 }
                        val adapter = ProveedorAdapter(proveedorFiltrado, this@ProveedorActivity)
                        recyclerView.adapter = adapter
                    } ?: run {
                        Toast.makeText(this@ProveedorActivity, "No se encontraron categorías", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Toast.makeText(this@ProveedorActivity, "Error al obtener datos: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(p0: Call<List<Proveedor>>, t: Throwable) {
                Toast.makeText(this@ProveedorActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun mostrarDialogoConfirmacionEliminar(proveedor: Proveedor) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Está seguro de que desea eliminar esta categoría?")

        builder.setPositiveButton("Sí") { dialog, _ ->
            eliminarEmpleado(proveedor.idProveedor)
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
    private fun eliminarEmpleado(idProveedor: Int) {
        val call = apiService.eliminarProveedor(idProveedor)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProveedorActivity, "Categoría eliminada exitosamente", Toast.LENGTH_SHORT).show()
                    obtenerCliente()
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Toast.makeText(this@ProveedorActivity, "Error al eliminar la categoría: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ProveedorActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun insertarEmpleado(nuevoProveedor: NuevoProveedor) {
        val call = apiService.insertarProveedor(
            nuevoProveedor.nombre,
            nuevoProveedor.direccion,
            nuevoProveedor.telefono,
            nuevoProveedor.status
        )
        call.enqueue(object : Callback<Void> {
            @SuppressLint("SuspiciousIndentation")
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProveedorActivity, "Categoría insertada exitosamente", Toast.LENGTH_SHORT).show()
                    obtenerCliente()
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Toast.makeText(this@ProveedorActivity, "Error al insertar la categoría: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ProveedorActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun mostrarDialogoInsertar() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.activity_insertar_proveedor, null)
        builder.setView(view)

        val nombreEditText: EditText = view.findViewById(R.id.nombreEditText)
        val direccionEditText: EditText = view.findViewById(R.id.direccionEditText)
        val telefonoEditText: EditText = view.findViewById(R.id.telefonoEditText)
        val statusEditText: EditText = view.findViewById(R.id.statusEditText)
        statusEditText.setText("true")
        statusEditText.isEnabled = false


        val aceptarButton: Button = view.findViewById(R.id.aceptarButton)
        val cancelarButton: Button = view.findViewById(R.id.regresarButton)

        val dialog = builder.create()

        aceptarButton.setOnClickListener {
            val nombre = nombreEditText.text.toString()
            val direccion = direccionEditText.text.toString()
            val telefono = telefonoEditText.text.toString()
            val status = statusEditText.text.toString()

            if (nombre.isNotEmpty() && direccion.isNotEmpty()) {
                val nuevoProveedor = NuevoProveedor(nombre, direccion, telefono, status = true) // Estado predeterminado como 1 (true)
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
    private fun modificarCliente(nuevoProveedor: Proveedor) {
        val call = apiService.actualizarProveedor(
            nuevoProveedor.idProveedor,
            nombre = nuevoProveedor.nombre,
            direccion = nuevoProveedor.direccion,
            telefono = nuevoProveedor.telefono
        )
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProveedorActivity, "Categoría modificada exitosamente", Toast.LENGTH_SHORT).show()
                    obtenerCliente()
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Toast.makeText(this@ProveedorActivity, "Error al modificar la categoría: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ProveedorActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun mostrarDialogoModificar(proveedor: Proveedor) {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.activity_insertar_proveedor, null)
        builder.setView(view)

        val nombreEditText: EditText = view.findViewById(R.id.nombreEditText)
        val direccionEditText: EditText = view.findViewById(R.id.direccionEditText)
        val telefonoEditText: EditText = view.findViewById(R.id.telefonoEditText)
        val statusEditText: EditText = view.findViewById(R.id.statusEditText)

        nombreEditText.setText(proveedor.nombre)
        direccionEditText.setText(proveedor.direccion)
        telefonoEditText.setText(proveedor.telefono)
        statusEditText.setText(proveedor.status.toString())
        statusEditText.isEnabled = false

        val aceptarButton: Button = view.findViewById(R.id.aceptarButton)
        val cancelarButton: Button = view.findViewById(R.id.regresarButton)

        val dialog = builder.create()

        aceptarButton.setOnClickListener {
            val nombre = nombreEditText.text.toString()
            val direccion = direccionEditText.text.toString()
            val telefono = telefonoEditText.text.toString()
            val status = statusEditText.text.toString()

            if (nombre.isNotEmpty() && direccion.isNotEmpty() && telefono.isNotEmpty() && status.isNotEmpty()) {
                val proveedor = Proveedor(proveedor.idProveedor, nombre, direccion, telefono, status.toInt())
                modificarCliente(proveedor)
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