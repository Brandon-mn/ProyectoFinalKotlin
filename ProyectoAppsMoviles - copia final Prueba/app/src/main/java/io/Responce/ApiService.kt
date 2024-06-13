package io.Responce

import io.Responce.Empleado.Empleado
import io.Responce.Producto.Producto
import io.Responce.Proveedor.Proveedor
import io.Responce.Proveedor_Producto.ProveedorProducto
import io.Responce.Venta.Venta
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import okhttp3.ResponseBody
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface ApiService {

    @GET("api/Login/AutenticarLogin/autenticar")
    fun autenticarUsuario(
        @Query("usuario") usuario: String,
        @Query("contraseña") contraseña: String
    ): Call<ResponseBody>
    @POST("/api/Login/CrearLogin/Create")
    fun insertarUsuario(
        @Query("usuario") usuario: String,
        @Query("contraseña") contraseña: String,
        @Query("status") status: Boolean
    ): Call<Void>
    //CATEGORIA
    @GET("api/Categoria/GetTodosLasCategorias")
    fun obtenerCategorias(): Call<List<Categoria>>

    @DELETE("api/Categoria/EliminarCategoria/DeleteCategoria/{id}")
    fun eliminarCategoria(@Path("id") idCategoria: Int): Call<Void>

    @POST("api/Categoria/CrearCategoria/CreateCategoria")
    fun insertarCategoria(
        @Query("nombre") nombre: String,
        @Query("descripcion") descripcion: String,
        @Query("estado") estado: String,
        @Query("status") status: Boolean
    ): Call<Void>
    @PUT("api/Categoria/ActualizarCategoria/UpdateCategoria/{id}")
    fun actualizarCategoria(
        @Path("id") id: Int,
        @Query("nombre") nombre: String,
        @Query("descripcion") descripcion: String,
        @Query("estado") estado: String
    ): Call<Void>

    //CLEINTE
    @GET("api/Cliente")
    fun obtenerClientes(): Call<List<Cliente>>
    @DELETE("api/Cliente/DeleteCliente/{id}")
    fun eliminarCliente(@Path("id") idCliente: Int): Call<Void>
    @POST("api/Cliente/CreateCliente")
    fun insertarCliente(
        @Query("nombre") nombre: String,
        @Query("direccion") direccion: String,
        @Query("telefono") telefono: String,
        @Query("status") status: Boolean
    ): Call<Void>
    @PUT("api/Cliente/UpdateCliente/{id}")
    fun actualizarCliente(
        @Path("id") id: Int,
        @Query("nombre") nombre: String,
        @Query("direccion") direccion: String,
        @Query("telefono") telefono: String
    ): Call<Void>

    //EMPLEADO
    @GET("api/Empleado/GetTodosLosEmpleados")
    fun obtenerEmpleados(): Call<List<Empleado>>
    @DELETE("api/Empleado/EliminarCategoria/Delete/{id}")
    fun eliminarEmleado(@Path("id") idEmpleado: Int): Call<Void>
    @POST("api/Empleado/CrearEmpleado/Create")
    fun insertarEmpleado(
        @Query("nombre") nombre: String,
        @Query("puesto") puesto: String,
        @Query("salario") salario: String,
        @Query("status") status: Boolean
    ): Call<Void>
    @PUT("api/Empleado/ActualizarEmpleado/Update/{id}")
    fun actualizarEmpleado(
        @Path("id") id: Int,
        @Query("nombre") nombre: String,
        @Query("puesto") puesto: String,
        @Query("salario") salario: Int
    ): Call<Void>

    //PRODUCTO
    @GET("api/Producto/GetTodasLasProducto")
    fun obtenerProducto(): Call<List<Producto>>
    @DELETE("api/Producto/EliminarProducto/Delete/{id}")
    fun eliminarProducto(@Path("id") idProducto: Int): Call<Void>
    @POST("api/Producto/CrearProduto/Create")
    fun insertarProducto(
        @Query("nombre") nombre: String,
        @Query("descripcion") descripcion: String,
        @Query("precio") precio: Int,
        @Query("status") status: Boolean
    ): Call<Void>
    @PUT("api/Producto/ActualizarProducto/Update/{id}")
    fun actualizarProducto(
        @Path("id") id: Int,
        @Query("nombre") nombre: String,
        @Query("descripcion") descripcion: String,
        @Query("precio") precio: Int
    ): Call<Void>

    //PROVEEDOR
    @GET("api/Proveedor/GetTodasLosProveedores")
    fun obtenerPorveedor(): Call<List<Proveedor>>
    @DELETE("api/Proveedor/EliminarProveedor/Delete/{id}")
    fun eliminarProveedor(@Path("id") idProveedor: Int): Call<Void>
    @POST("api/Proveedor/CrearProveedor/Create")
    fun insertarProveedor(
        @Query("nombre") nombre: String,
        @Query("direccion") direccion: String,
        @Query("telefono") telefono: String,
        @Query("status") status: Boolean
    ): Call<Void>
    @PUT("api/Proveedor/ActualizarProveedor/Update/{id}")
    fun actualizarProveedor(
        @Path("id") id: Int,
        @Query("nombre") nombre: String,
        @Query("direccion") direccion: String,
        @Query("telefono") telefono: String
    ): Call<Void>

    //PROVEEDOR_PRODUCTO
    @GET("api/Proveedor_Producto/GetTodasLasProveedorProducto")
    fun obtenerProveedorProducto(): Call<List<ProveedorProducto>>
    @DELETE("api/Proveedor_Producto/EliminarProveedor_Producto/Delete/{id}")
    fun eliminarProveedorProdcuto(@Path("id") idProveedorProducto: Int): Call<Void>
    @POST("api/Proveedor_Producto/CrearProveedor_producto/Create")
    fun insertarProveedorProducto(
        @Query("idProveedor") idProveedor: Int,
        @Query("idProducto") idProducto: Int,
        @Query("status") status: Boolean
    ): Call<Void>
    @PUT("api/Proveedor_Producto/Actualizarproveedor_product/Update/{id}")
    fun actualizarProveedorProducto(
        @Path("id") id: Int,
        @Query("idProveedor") idProveedor: Int,
        @Query("idProducto") idProducto: Int
    ): Call<Void>

   //VENTA
   @GET("api/Venta/GetTodasLasVentas")
   fun obtenerVentas(): Call<List<Venta>>
    @DELETE("api/Venta/EliminarVenta/Delete/{id}")
    fun eliminarVenta(@Path("id") idVenta: Int): Call<Void>
    @POST("api/Venta/CrearVenta/Create")
    fun insertarVenta(
        @Query("fecha") fecha: String,
        @Query("total") total: Int,
        @Query("status") status: Boolean
    ): Call<Void>
    @PUT("api/Venta/ActualizarVenta/Update/{id}")
    fun actualizarVenta(
        @Path("id") id: Int,
        @Query("fecha") fecha: String,
        @Query("total") total: Int
    ): Call<Void>
}