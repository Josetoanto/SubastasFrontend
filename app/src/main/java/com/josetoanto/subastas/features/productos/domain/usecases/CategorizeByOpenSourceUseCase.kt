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

            // PASO 1: Filtrado por palabras clave (instantáneo, sin llamadas de red)
            val keywordMatched = CategoryKeywordLibrary.filterByKeywords(productos, categoriaDestino)
            val keywordMatchedIds = keywordMatched.map { it.id }.toSet()

            // PASO 2: Solo los productos NO encontrados por keywords van a la IA
            // Esto reduce drásticamente las llamadas a HuggingFace
            val candidateLabels = listOf("Tecnología", "Hogar", "Comida", "Bebida", "Otros")
            val authHeader = hfToken?.let { "Bearer $it" }
            val aiMatched = mutableListOf<Producto>()

            val toClassify = productos
                .filter { it.id !in keywordMatchedIds }
                .take(20) // Protege contra timeouts en HuggingFace

            for (producto in toClassify) {
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
                        // El modelo ordena labels de mayor a menor probabilidad
                        val topMatch = res.labels.first()
                        if (topMatch.equals(categoriaDestino, ignoreCase = true)) {
                            aiMatched.add(producto)
                        }
                    }
                } catch (e: Exception) {
                    // Ignoramos el timeout o la falla de cuota para ese producto específico
                    e.printStackTrace()
                }
            }

            // PASO 3: Unión — keywords primero (orden de prioridad), luego IA, sin duplicados
            (keywordMatched + aiMatched).distinctBy { it.id }
        }
    }
}
