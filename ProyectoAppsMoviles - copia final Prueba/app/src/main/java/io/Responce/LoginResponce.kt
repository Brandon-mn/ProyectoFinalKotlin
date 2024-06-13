package io.Responce

import model.User

data class LoginResponce(
    val usuario: String,
    val contraseña: String,
    val status: Boolean
)
