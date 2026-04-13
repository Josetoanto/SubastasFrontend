package com.josetoanto.subastas.features.productos.domain.usecases

import com.josetoanto.subastas.features.productos.data.datasources.remote.api.HuggingFaceApi
import com.josetoanto.subastas.features.productos.data.datasources.remote.models.HfZeroShotParameters
import com.josetoanto.subastas.features.productos.data.datasources.remote.models.HfZeroShotRequest
import com.josetoanto.subastas.features.productos.domain.entities.Producto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CategorizeByOpenSourceUseCase @Inject constructor(
    private val huggingFaceApi: HuggingFaceApi
) {
    // Es recomendable insertar un HF token en el Auth Header en Producción si las API fallan por cuota libre
    private val hfToken: String? = null 

    suspend operator fun invoke(
        productos: List<Producto>,
        categoriaDestino: String
    ): Result<List<Producto>> = withContext(Dispatchers.IO) {
        runCatching {
            if (categoriaDestino == "Todos" || categoriaDestino.isBlank()) {
                return@runCatching productos
            }

            val candidateLabels = listOf("Tecnología", "Hogar", "Comida", "Bebida", "Otros")
            val authHeader = hfToken?.let { "Bearer $it" }
            val filtrados = mutableListOf<Producto>()
            
            // En una App de producción masiva el filtrado IA debe ir en el BackEnd (Node/Python) para no bloquear la UI HTTP.
            // Para fines de esta arquitectura, filtramos hasta 20 localmente para proteger los Timeouts.
            for (producto in productos.take(20)) {
                try {
                    val req = HfZeroShotRequest(
                        inputs = "${producto.nombre}. ${producto.descripcion}",
                        parameters = HfZeroShotParameters(candidate_labels = candidateLabels)
                    )
                    
                    val res = huggingFaceApi.classifyText(
                        authorization = authHeader, 
                        body = req
                    )
                    
                    if (res.error == null && res.labels.isNotEmpty()) {
                        // El modelo asigna scores de probabilidad y ordena las labels de mayor a menor.
                        val topMatch = res.labels.first()
                        if (topMatch.equals(categoriaDestino, ignoreCase = true)) {
                            filtrados.add(producto)
                        }
                    }
                } catch (e: Exception) {
                    // Ignoramos el timeout o la falla de cuota para ese específico producto
                    e.printStackTrace()
                }
            }
            
            filtrados
        }
    }
}
