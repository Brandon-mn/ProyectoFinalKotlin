package com.example.proyectoappsmoviles
import io.Responce.ApiService
import android.os.Bundle
import androidx.activity.ComponentActivity

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import okhttp3.OkHttpClient
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Toast
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import io.Responce.LoginResponce

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class LoginActivity : ComponentActivity() {

    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextPasswordConfirm: EditText

    private lateinit var buttonLogin: Button
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextPasswordConfirm = findViewById(R.id.editTextPasswordConfirm)

        buttonLogin = findViewById(R.id.buttonLogin)
        val insertarButton: Button = findViewById(R.id.buttonRegister)
        insertarButton.setOnClickListener {
            mostrarDialogoInsertar()
        }
        val retrofit = Retrofit.Builder()
            .baseUrl("https://10.0.2.2:7267/")
            .client(unsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)
        buttonLogin.setOnClickListener {
            val correo = editTextEmail.text.toString()
            val contrasena = editTextPassword.text.toString()
            val contrasenaconfirmada = editTextPasswordConfirm.text.toString()

            if (correo.isNotEmpty() && contrasena.isNotEmpty()) {
                if (!isValidEmail(correo)) {
                    Toast.makeText(this, "Por favor, ingresa un correo válido (@gmail.com)", Toast.LENGTH_SHORT).show()
                } else if (contrasena == contrasenaconfirmada) {
                    autenticarUsuario(correo, contrasena)
                } else {
                    Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun isValidEmail(email: String): Boolean {
        val regex = "^[A-Za-z0-9._%+-]+@gmail\\.com$".toRegex()
        return email.matches(regex)
    }

    private fun insertarCategoria(loginResponce: LoginResponce) {
        val call = apiService.insertarUsuario(
            loginResponce.usuario,
            loginResponce.contraseña,
            loginResponce.status
        )
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@LoginActivity, "Categoría insertada exitosamente", Toast.LENGTH_SHORT).show()
                    reiniciarActividad()
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Toast.makeText(this@LoginActivity, "Error al insertar la categoría: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun reiniciarActividad() {
        val intent = Intent(this, LoginActivity::class.java)
        finish()
        startActivity(intent)
    }
    private fun mostrarDialogoInsertar() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.activity_insertar_usuario, null)
        builder.setView(view)

        val nombreEditText: EditText = view.findViewById(R.id.nombreEditText)
        val ContraseñaEditText: EditText = view.findViewById(R.id.ContraseñaEditText)
        val ContraseñaConfirmarEditText: EditText = view.findViewById(R.id.ContraseñaConfirmarEditText)
        val statusEditText: EditText = view.findViewById(R.id.statusEditText)

        statusEditText.setText("true")
        statusEditText.isEnabled = false

        val aceptarButton: Button = view.findViewById(R.id.aceptarButton)
        val cancelarButton: Button = view.findViewById(R.id.regresarButton)

        val dialog = builder.create()

        aceptarButton.setOnClickListener {
            val nombre = nombreEditText.text.toString()
            val descripcion = ContraseñaEditText.text.toString()
            val confirmar = ContraseñaConfirmarEditText.text.toString()

            if (!isValidEmail(nombre)) {
                Toast.makeText(this, "Por favor, ingresa un correo válido (@gmail.com)", Toast.LENGTH_SHORT).show()
            } else if (descripcion == confirmar) {
                if (nombre.isNotEmpty() && descripcion.isNotEmpty()) {
                    val nuevaCategoria = LoginResponce(nombre, descripcion, status = true) // Estado predeterminado como 1 (true)
                    insertarCategoria(nuevaCategoria)
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            }
        }


        cancelarButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun autenticarUsuario(usuario: String, contraseña: String) {
        val call = apiService.autenticarUsuario(usuario,contraseña)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@LoginActivity, "Credenciales válidas", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity, Menu::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun unsafeOkHttpClient(): OkHttpClient {
        try {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}

                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}

                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                    return arrayOf()
                }
            })

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())

            val hostnameVerifier = HostnameVerifier { _, _ -> true }

            return OkHttpClient.Builder()
                .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier(hostnameVerifier)
                .build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}
