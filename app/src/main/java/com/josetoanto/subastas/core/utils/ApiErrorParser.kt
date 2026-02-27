package com.josetoanto.subastas.core.utils

import org.json.JSONObject
import retrofit2.HttpException

fun Throwable.toReadableMessage(): String {
    if (this is HttpException) {
        return try {
            val errorBody = response()?.errorBody()?.string()
            val json = JSONObject(errorBody ?: "")
            json.optString("detail", message ?: "Error desconocido")
        } catch (e: Exception) {
            message ?: "Error desconocido"
        }
    }
    return message ?: "Error de conexión"
}