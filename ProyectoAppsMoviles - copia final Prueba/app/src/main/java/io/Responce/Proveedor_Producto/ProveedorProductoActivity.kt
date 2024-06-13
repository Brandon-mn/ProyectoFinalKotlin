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
import io.Responce.Proveedor.Proveedor
import io.Responce.Proveedor_Producto.NuevoProveedorProducto
import io.Responce.Proveedor_Producto.ProveedorProducto
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

class ProveedorProductoActivity : AppCompatActivity(), ProveedorProductoAdapter.OnCategoriaClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proveedor_producto)

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
    override fun onEliminarClick(proveedorProducto: ProveedorProducto) {
        mostrarDialogoConfirmacionEliminar(proveedorProducto)
    }
    override fun onModificarClick(proveedorProducto: ProveedorProducto) {
       mostrarDialogoModificar(proveedorProducto)
    }
    private fun obtenerCliente() {
        val call = apiService.obtenerProveedorProducto()
        call.enqueue(object : Callback<List<ProveedorProducto>> {
            override fun onResponse(call: Call<List<ProveedorProducto>>, response: Response<List<ProveedorProducto>>) {
                if (response.isSuccessful) {
                    val proveeodores = response.body()
                    proveeodores?.let {
                        val proveedorproductoFiltrado = it.filter { proveedorporducto -> proveedorporducto.status == 1 }
                        val adapter = ProveedorProductoAdapter(proveedorproductoFiltrado, this@ProveedorProductoActivity)
                        recyclerView.adapter = adapter
                    } ?: run {
                        Toast.makeText(this@ProveedorProductoActivity, "No se encontraron categorías", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Toast.makeText(this@ProveedorProductoActivity, "Error al obtener datos: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(p0: Call<List<ProveedorProducto>>, t: Throwable) {
                Toast.makeText(this@ProveedorProductoActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun mostrarDialogoConfirmacionEliminar(proveedorProducto: ProveedorProducto) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Está seguro de que desea eliminar esta categoría?")

        builder.setPositiveButton("Sí") { dialog, _ ->
            eliminarEmpleado(proveedorProducto.id_proveedor_producto)
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
    private fun eliminarEmpleado(idProveedorProducto: Int) {
        val call = apiService.eliminarProveedorProdcuto(idProveedorProducto)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProveedorProductoActivity, "Categoría eliminada exitosamente", Toast.LENGTH_SHORT).show()
                    obtenerCliente()
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Toast.makeText(this@ProveedorProductoActivity, "Error al eliminar la categoría: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ProveedorProductoActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun insertarEmpleado(nuevoProveedorProducto: NuevoProveedorProducto) {
        val call = apiService.insertarProveedorProducto(
            nuevoProveedorProducto.idProveedor,
            nuevoProveedorProducto.idProducto,
            nuevoProveedorProducto.status
        )
        call.enqueue(object : Callback<Void> {
            @SuppressLint("SuspiciousIndentation")
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProveedorProductoActivity, "Categoría insertada exitosamente", Toast.LENGTH_SHORT).show()
                    obtenerCliente()
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Toast.makeText(this@ProveedorProductoActivity, "Error al insertar la categoría: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ProveedorProductoActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun mostrarDialogoInsertar() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.activity_insertar_proveedor_producto, null)
        builder.setView(view)

        val idProveedorEditText: EditText = view.findViewById(R.id.idProveedorEditText)
        val idProductoEditText: EditText = view.findViewById(R.id.idProductoEditText)
        val statusEditText: EditText = view.findViewById(R.id.statusEditText)
        statusEditText.setText("true")
        statusEditText.isEnabled = false


        val aceptarButton: Button = view.findViewById(R.id.aceptarButton)
        val cancelarButton: Button = view.findViewById(R.id.regresarButton)

        val dialog = builder.create()

        aceptarButton.setOnClickListener {
            val idProveedor = idProveedorEditText.text.toString()
            val idProducto = idProductoEditText.text.toString()
            val status = statusEditText.text.toString()

            if (idProveedor.isNotEmpty() && idProducto.isNotEmpty()) {
                val nuevoProveedor = NuevoProveedorProducto(idProveedor.toInt(), idProducto.toInt(), status = true) // Estado predeterminado como 1 (true)
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
    private fun modificarCliente(nuevoProveedorProducto: ProveedorProducto) {
        val call = apiService.actualizarProveedorProducto(
            nuevoProveedorProducto.id_proveedor_producto,
            idProveedor = nuevoProveedorProducto.idProveedor.toInt(),
            idProducto = nuevoProveedorProducto.idProducto.toInt()
            )
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProveedorProductoActivity, "Categoría modificada exitosamente", Toast.LENGTH_SHORT).show()
                    obtenerCliente()
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Toast.makeText(this@ProveedorProductoActivity, "Error al modificar la categoría: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ProveedorProductoActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun mostrarDialogoModificar(proveedorProducto: ProveedorProducto) {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.activity_insertar_proveedor_producto, null)
        builder.setView(view)

        val idProveedorEditText: EditText = view.findViewById(R.id.idProveedorEditText)
        val idProductoEditText: EditText = view.findViewById(R.id.idProductoEditText)
        val statusEditText: EditText = view.findViewById(R.id.statusEditText)

        idProveedorEditText.setText(proveedorProducto.idProducto.toString())
        idProductoEditText.setText(proveedorProducto.idProveedor.toString())
        statusEditText.setText(proveedorProducto.status.toString())
        statusEditText.isEnabled = false

        val aceptarButton: Button = view.findViewById(R.id.aceptarButton)
        val cancelarButton: Button = view.findViewById(R.id.regresarButton)

        val dialog = builder.create()

        aceptarButton.setOnClickListener {
            val idProveedor = idProveedorEditText.text.toString()
            val idProducto = idProductoEditText.text.toString()
            val status = statusEditText.text.toString()

            if (idProveedor.isNotEmpty() && idProducto.isNotEmpty()  && status.isNotEmpty()) {
                val proveedorProducto = ProveedorProducto(proveedorProducto.id_proveedor_producto, idProveedor.toInt(), idProducto.toInt(), status.toInt())
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





