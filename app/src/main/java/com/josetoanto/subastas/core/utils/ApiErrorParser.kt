package com.josetoanto.subastas.core.utils

import org.json.JSONObject
import retrofit2.HttpException

fun Throwable.toReadableMessage(): String {
    if (this is HttpException) {
        return try {
            val errorBody = response()?.errorBody()?.string()
            val json = JSONObject(errorBody ?: "")
            val detail = json.optString("detail").takeIf { it.isNotBlank() }
            val msg = json.optString("message").takeIf { it.isNotBlank() }
            val error = json.optString("error").takeIf { it.isNotBlank() }

            detail ?: msg ?: error ?: fallbackHttpMessage(code())
        } catch (e: Exception) {
            fallbackHttpMessage(code())
        }
    }
    return message ?: "Error de conexión"
}

private fun fallbackHttpMessage(code: Int): String {
    return when (code) {
        400 -> "Solicitud inválida. Revisa los datos de la puja."
        401 -> "Debes iniciar sesión para pujar."
        403 -> "No tienes permisos para realizar esta puja."
        404 -> "La subasta ya no existe o no está disponible."
        409 -> "No se pudo procesar la puja por conflicto de estado."
        422 -> "La puja no cumple las reglas mínimas de la subasta."
        500 -> "Error interno del servidor al procesar la puja."
        502, 503, 504 -> "El servidor está tardando en responder. Intenta de nuevo."
        else -> "No se pudo completar la operación (HTTP $code)."
    }
}