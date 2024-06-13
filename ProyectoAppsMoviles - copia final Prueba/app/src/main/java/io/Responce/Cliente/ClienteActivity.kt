package com.example.proyectoappsmoviles
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
import io.Responce.Categoria
import io.Responce.Cliente
import io.Responce.NuevoCliente
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*
 class ClienteActivity : AppCompatActivity(), ClienteAdapter.OnCategoriaClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cliente)

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
    private fun obtenerCliente() {
        val call = apiService.obtenerClientes()
        call.enqueue(object : Callback<List<Cliente>> {
            override fun onResponse(call: Call<List<Cliente>>, response: Response<List<Cliente>>) {
                if (response.isSuccessful) {
                    val clientes = response.body()
                    clientes?.let {
                        val clientesFlitrados = it.filter { cliente -> cliente.status == 1 }
                        val adapter = ClienteAdapter(clientesFlitrados, this@ClienteActivity)
                        recyclerView.adapter = adapter
                    } ?: run {
                        Toast.makeText(this@ClienteActivity, "No se encontraron categorías", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Toast.makeText(this@ClienteActivity, "Error al obtener datos: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(p0: Call<List<Cliente>>, t: Throwable) {
                Toast.makeText(this@ClienteActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

     override fun onEliminarClick(cliente: Cliente) {
         mostrarDialogoConfirmacionEliminar(cliente)

     }

     override fun onModificarClick(cliente: Cliente) {
         mostrarDialogoModificar(cliente)

     }
     private fun mostrarDialogoConfirmacionEliminar(cliente: Cliente) {
         val builder = AlertDialog.Builder(this)
         builder.setTitle("Confirmar eliminación")
         builder.setMessage("¿Está seguro de que desea eliminar esta categoría?")

         builder.setPositiveButton("Sí") { dialog, _ ->
             eliminarCliente(cliente.idCliente)
             dialog.dismiss()
         }

         builder.setNegativeButton("No") { dialog, _ ->
             dialog.dismiss()
         }

         val dialog = builder.create()
         dialog.show()
     }
     private fun eliminarCliente(idCliente: Int) {
         val call = apiService.eliminarCliente(idCliente)
         call.enqueue(object : Callback<Void> {
             override fun onResponse(call: Call<Void>, response: Response<Void>) {
                 if (response.isSuccessful) {
                     Toast.makeText(this@ClienteActivity, "Categoría eliminada exitosamente", Toast.LENGTH_SHORT).show()
                     obtenerCliente()
                 } else {
                     val errorMsg = response.errorBody()?.string()
                     Toast.makeText(this@ClienteActivity, "Error al eliminar la categoría: $errorMsg", Toast.LENGTH_SHORT).show()
                 }
             }

             override fun onFailure(call: Call<Void>, t: Throwable) {
                 Toast.makeText(this@ClienteActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
             }
         })
     }
     private fun insertarCliente(nuevoCliente: NuevoCliente) {
         val call = apiService.insertarCliente(
             nuevoCliente.nombre,
             nuevoCliente.direccion,
             nuevoCliente.telefono,
             nuevoCliente.status
         )
         call.enqueue(object : Callback<Void> {
             override fun onResponse(call: Call<Void>, response: Response<Void>) {
                 if (response.isSuccessful) {
                     Toast.makeText(this@ClienteActivity, "Categoría insertada exitosamente", Toast.LENGTH_SHORT).show()
                     obtenerCliente()
                 } else {
                     val errorMsg = response.errorBody()?.string()
                     Toast.makeText(this@ClienteActivity, "Error al insertar la categoría: $errorMsg", Toast.LENGTH_SHORT).show()
                 }
             }

             override fun onFailure(call: Call<Void>, t: Throwable) {
                 Toast.makeText(this@ClienteActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
             }
         })
     }
     private fun mostrarDialogoInsertar() {
         val builder = AlertDialog.Builder(this)
         val inflater = LayoutInflater.from(this)
         val view = inflater.inflate(R.layout.activity_insertar_cliente, null)
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
                 val nuevoCliente = NuevoCliente(nombre, direccion, telefono, status = true) // Estado predeterminado como 1 (true)
                 insertarCliente(nuevoCliente)
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
     private fun modificarCliente(nuevoCliente: Cliente) {
         val call = apiService.actualizarCliente(
             nuevoCliente.idCliente,
             nombre = nuevoCliente.nombre,
             direccion = nuevoCliente.direccion,
             telefono = nuevoCliente.telefono
         )
         call.enqueue(object : Callback<Void> {
             override fun onResponse(call: Call<Void>, response: Response<Void>) {
                 if (response.isSuccessful) {
                     Toast.makeText(this@ClienteActivity, "Categoría modificada exitosamente", Toast.LENGTH_SHORT).show()
                     obtenerCliente()
                 } else {
                     val errorMsg = response.errorBody()?.string()
                     Toast.makeText(this@ClienteActivity, "Error al modificar la categoría: $errorMsg", Toast.LENGTH_SHORT).show()
                 }
             }

             override fun onFailure(call: Call<Void>, t: Throwable) {
                 Toast.makeText(this@ClienteActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
             }
         })
     }
     private fun mostrarDialogoModificar(cliente: Cliente) {
         val builder = AlertDialog.Builder(this)
         val inflater = LayoutInflater.from(this)
         val view = inflater.inflate(R.layout.activity_insertar_cliente, null)
         builder.setView(view)

         val nombreEditText: EditText = view.findViewById(R.id.nombreEditText)
         val direccionEditText: EditText = view.findViewById(R.id.direccionEditText)
         val telefonoEditText: EditText = view.findViewById(R.id.telefonoEditText)
         val statusEditText: EditText = view.findViewById(R.id.statusEditText)

         nombreEditText.setText(cliente.nombre)
         direccionEditText.setText(cliente.direccion)
         telefonoEditText.setText(cliente.telefono)
         statusEditText.setText(cliente.status.toString())
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
                 val cliente = Cliente(cliente.idCliente, nombre, direccion, telefono, status.toInt())
                 modificarCliente(cliente)
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