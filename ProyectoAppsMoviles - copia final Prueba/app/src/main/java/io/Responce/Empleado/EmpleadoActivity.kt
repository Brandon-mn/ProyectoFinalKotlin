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
import io.Responce.Cliente
import io.Responce.Empleado.Empleado
import io.Responce.Empleado.NuevoEmpleado
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*
class EmpleadoActivity : AppCompatActivity(), EmpleadoAdapter.OnCategoriaClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empleado)

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
        val call = apiService.obtenerEmpleados()
        call.enqueue(object : Callback<List<Empleado>> {
            override fun onResponse(call: Call<List<Empleado>>, response: Response<List<Empleado>>) {
                if (response.isSuccessful) {
                    val clientes = response.body()
                    clientes?.let {
                        val clientesFlitrados = it.filter { empleado -> empleado.status == 1 }
                        val adapter = EmpleadoAdapter(clientesFlitrados, this@EmpleadoActivity)
                        recyclerView.adapter = adapter
                    } ?: run {
                        Toast.makeText(this@EmpleadoActivity, "No se encontraron categorías", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Toast.makeText(this@EmpleadoActivity, "Error al obtener datos: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(p0: Call<List<Empleado>>, t: Throwable) {
                Toast.makeText(this@EmpleadoActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    override fun onEliminarClick(empleado: Empleado) {
        mostrarDialogoConfirmacionEliminar(empleado)
    }


    private fun mostrarDialogoConfirmacionEliminar(empleado: Empleado) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Está seguro de que desea eliminar esta categoría?")

        builder.setPositiveButton("Sí") { dialog, _ ->
            eliminarEmpleado(empleado.idEmpleado)
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
    override fun onModificarClick(empleado: Empleado) {
        mostrarDialogoModificar(empleado)
    }
    private fun eliminarEmpleado(idEmpleado: Int) {
        val call = apiService.eliminarEmleado(idEmpleado)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EmpleadoActivity, "Categoría eliminada exitosamente", Toast.LENGTH_SHORT).show()
                    obtenerCliente()
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Toast.makeText(this@EmpleadoActivity, "Error al eliminar la categoría: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@EmpleadoActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun insertarEmpleado(nuevoEmpleado: NuevoEmpleado) {
        val call = apiService.insertarEmpleado(
            nuevoEmpleado.nombre,
            nuevoEmpleado.puesto,
            nuevoEmpleado.salario.toString(),
            nuevoEmpleado.status
        )
        call.enqueue(object : Callback<Void> {
            @SuppressLint("SuspiciousIndentation")
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EmpleadoActivity, "Categoría insertada exitosamente", Toast.LENGTH_SHORT).show()
                    obtenerCliente()
                } else {
                    val errorMsg = response.errorBody()?.string()
                        Toast.makeText(this@EmpleadoActivity, "Error al insertar la categoría: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@EmpleadoActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun mostrarDialogoInsertar() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.activity_insertar_empleado, null)
        builder.setView(view)

        val nombreEditText: EditText = view.findViewById(R.id.nombreEditText)
        val puestoEditText: EditText = view.findViewById(R.id.puestoEditText)
        val salarioEditText: EditText = view.findViewById(R.id.salarioEditText)
        val statusEditText: EditText = view.findViewById(R.id.statusEditText)
        statusEditText.setText("true")
        statusEditText.isEnabled = false


        val aceptarButton: Button = view.findViewById(R.id.aceptarButton)
        val cancelarButton: Button = view.findViewById(R.id.regresarButton)

        val dialog = builder.create()

        aceptarButton.setOnClickListener {
            val nombre = nombreEditText.text.toString()
            val puesto = puestoEditText.text.toString()
            val salario = salarioEditText.text.toString()
            val status = statusEditText.text.toString()

            if (nombre.isNotEmpty() && puesto.isNotEmpty()) {
                val nuevoEmpleado = NuevoEmpleado(nombre, puesto, salario.toInt(), status = true) // Estado predeterminado como 1 (true)
                insertarEmpleado(nuevoEmpleado)
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
    private fun modificarCliente(nuevoEmpleado: Empleado) {
        val call = apiService.actualizarEmpleado(
            nuevoEmpleado.idEmpleado,
            nombre = nuevoEmpleado.nombre,
            puesto = nuevoEmpleado.puesto,
            salario = nuevoEmpleado.salario.toInt()
        )
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EmpleadoActivity, "Categoría modificada exitosamente", Toast.LENGTH_SHORT).show()
                    obtenerCliente()
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Toast.makeText(this@EmpleadoActivity, "Error al modificar la categoría: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@EmpleadoActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun mostrarDialogoModificar(empleado: Empleado) {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.activity_insertar_empleado, null)
        builder.setView(view)

        val nombreEditText: EditText = view.findViewById(R.id.nombreEditText)
        val puestoEditText: EditText = view.findViewById(R.id.puestoEditText)
        val salarioEditText: EditText = view.findViewById(R.id.salarioEditText)
        val statusEditText: EditText = view.findViewById(R.id.statusEditText)

        nombreEditText.setText(empleado.nombre)
        puestoEditText.setText(empleado.puesto)
        salarioEditText.setText(empleado.salario.toString())
        statusEditText.setText(empleado.status.toString())
        statusEditText.isEnabled = false

        val aceptarButton: Button = view.findViewById(R.id.aceptarButton)
        val cancelarButton: Button = view.findViewById(R.id.regresarButton)

        val dialog = builder.create()

        aceptarButton.setOnClickListener {
            val nombre = nombreEditText.text.toString()
            val puesto = puestoEditText.text.toString()
            val salario = salarioEditText.text.toString()
            val status = statusEditText.text.toString()

            if (nombre.isNotEmpty() && puesto.isNotEmpty() && salario.isNotEmpty() && status.isNotEmpty()) {
                val empleado = Empleado(empleado.idEmpleado, nombre, puesto, salario.toInt(), status.toInt())
                modificarCliente(empleado)
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