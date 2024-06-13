package com.example.proyectoappsmoviles
import android.view.LayoutInflater
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.Responce.ApiService
import io.Responce.Categoria
import io.Responce.NuevaCategoria
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

class CategoriaActivity : AppCompatActivity(), CategoriaAdapter.OnCategoriaClickListener {


    private lateinit var recyclerView: RecyclerView
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categoria)

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

        obtenerCategorias()
    }

    private fun obtenerCategorias() {
        val call = apiService.obtenerCategorias()
        call.enqueue(object : Callback<List<Categoria>> {
            override fun onResponse(call: Call<List<Categoria>>, response: Response<List<Categoria>>) {
                if (response.isSuccessful) {
                    val categorias = response.body()
                    categorias?.let {
                        val categoriasFiltradas = it.filter { categoria -> categoria.status == 1 }
                        val adapter = CategoriaAdapter(categoriasFiltradas, this@CategoriaActivity)
                        recyclerView.adapter = adapter
                    } ?: run {
                        Toast.makeText(this@CategoriaActivity, "No se encontraron categorías", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Toast.makeText(this@CategoriaActivity, "Error al obtener datos: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Categoria>>, t: Throwable) {
                Toast.makeText(this@CategoriaActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onEliminarClick(categoria: Categoria) {
        mostrarDialogoConfirmacionEliminar(categoria)
    }

    override fun onModificarClick(categoria: Categoria) {
        mostrarDialogoModificar(categoria)
    }
    private fun mostrarDialogoConfirmacionEliminar(categoria: Categoria) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Está seguro de que desea eliminar esta categoría?")

        builder.setPositiveButton("Sí") { dialog, _ ->
            eliminarCategoria(categoria.idCategoria)
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun eliminarCategoria(idCategoria: Int) {
        val call = apiService.eliminarCategoria(idCategoria)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CategoriaActivity, "Categoría eliminada exitosamente", Toast.LENGTH_SHORT).show()
                    obtenerCategorias()
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Toast.makeText(this@CategoriaActivity, "Error al eliminar la categoría: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@CategoriaActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun insertarCategoria(nuevaCategoria: NuevaCategoria) {
        val call = apiService.insertarCategoria(
            nuevaCategoria.nombre,
            nuevaCategoria.descripcion,
            nuevaCategoria.estado,
            nuevaCategoria.status
        )
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CategoriaActivity, "Categoría insertada exitosamente", Toast.LENGTH_SHORT).show()
                    obtenerCategorias()
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Toast.makeText(this@CategoriaActivity, "Error al insertar la categoría: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@CategoriaActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostrarDialogoInsertar() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.activity_insertar_categoria, null)
        builder.setView(view)

        val nombreEditText: EditText = view.findViewById(R.id.nombreEditText)
        val descripcionEditText: EditText = view.findViewById(R.id.descripcionEditText)
        val estadoEditText: EditText = view.findViewById(R.id.estadoEditText)
        val statusEditText: EditText = view.findViewById(R.id.statusEditText)

        statusEditText.setText("true")
        statusEditText.isEnabled = false


        val aceptarButton: Button = view.findViewById(R.id.aceptarButton)
        val cancelarButton: Button = view.findViewById(R.id.regresarButton)

        val dialog = builder.create()

        aceptarButton.setOnClickListener {
            val nombre = nombreEditText.text.toString()
            val descripcion = descripcionEditText.text.toString()
            val estado = estadoEditText.text.toString()
            val status = statusEditText.text.toString()

            if (nombre.isNotEmpty() && descripcion.isNotEmpty()) {
                val nuevaCategoria = NuevaCategoria(nombre, descripcion, estado, status = true) // Estado predeterminado como 1 (true)
                insertarCategoria(nuevaCategoria)
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
    private fun mostrarDialogoModificar(categoria: Categoria) {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.activity_insertar_categoria, null)
        builder.setView(view)

        val nombreEditText: EditText = view.findViewById(R.id.nombreEditText)
        val descripcionEditText: EditText = view.findViewById(R.id.descripcionEditText)
        val estadoEditText: EditText = view.findViewById(R.id.estadoEditText)
        val statusEditText: EditText = view.findViewById(R.id.statusEditText)

        nombreEditText.setText(categoria.nombre)
        descripcionEditText.setText(categoria.descripcion)
        estadoEditText.setText(categoria.estado)
        statusEditText.setText(categoria.status.toString())
        statusEditText.isEnabled = false

        val aceptarButton: Button = view.findViewById(R.id.aceptarButton)
        val cancelarButton: Button = view.findViewById(R.id.regresarButton)

        val dialog = builder.create()

        aceptarButton.setOnClickListener {
            val nombre = nombreEditText.text.toString()
            val descripcion = descripcionEditText.text.toString()
            val estado = estadoEditText.text.toString()
            val status = statusEditText.text.toString()

            if (nombre.isNotEmpty() && descripcion.isNotEmpty() && estado.isNotEmpty() && status.isNotEmpty()) {
                val nuevaCategoria = Categoria(categoria.idCategoria, nombre, descripcion, estado, status.toInt())
                modificarCategoria(nuevaCategoria)
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

    private fun modificarCategoria(nuevaCategoria: Categoria) {
        val call = apiService.actualizarCategoria(
            nuevaCategoria.idCategoria,
            nombre = nuevaCategoria.nombre,
            descripcion = nuevaCategoria.descripcion,
            estado = nuevaCategoria.estado
        )
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CategoriaActivity, "Categoría modificada exitosamente", Toast.LENGTH_SHORT).show()
                    obtenerCategorias()
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Toast.makeText(this@CategoriaActivity, "Error al modificar la categoría: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@CategoriaActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
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
}
