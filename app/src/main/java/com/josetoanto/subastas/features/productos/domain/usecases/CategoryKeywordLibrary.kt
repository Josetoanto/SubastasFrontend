package com.josetoanto.subastas.features.productos.domain.usecases

import com.josetoanto.subastas.features.productos.domain.entities.Producto

object CategoryKeywordLibrary {

    val keywords: Map<String, List<String>> = mapOf(
        "Tecnología" to listOf(
            "teléfono", "celular", "smartphone", "móvil", "tablet", "ipad",
            "computadora", "computador", "laptop", "notebook", "pc", "desktop",
            "monitor", "teclado", "mouse", "ratón", "impresora", "escáner",
            "procesador", "cpu", "gpu", "tarjeta gráfica", "ram", "memoria",
            "disco duro", "ssd", "hdd", "placa madre",
            "audífonos", "auriculares", "bocina", "altavoz", "parlante",
            "cámara", "lente", "drone", "proyector", "televisor", "tv",
            "smartwatch", "reloj inteligente", "router", "wifi",
            "cable", "cargador", "batería", "powerbank", "adaptador",
            "consola", "videojuego", "gaming", "joystick", "control",
            "phone", "computer", "headphones", "camera", "watch",
            "keyboard", "charger", "speaker", "screen", "display", "printer"
        ),
        "Hogar" to listOf(
            "silla", "sillón", "sofá", "sofa", "mesa", "escritorio",
            "cama", "colchón", "closet", "ropero", "armario", "estante",
            "librero", "vitrina", "aparador", "cajonera",
            "lavadora", "secadora", "refrigerador", "nevera", "microondas",
            "horno", "estufa", "licuadora", "batidora", "aspiradora",
            "ventilador", "aire acondicionado", "calentador", "plancha",
            "lámpara", "cortina", "alfombra", "cuadro", "espejo",
            "florero", "maceta", "planta", "cojín", "almohada",
            "sábana", "toalla", "edredón", "colcha",
            "taladro", "martillo", "destornillador", "pintura", "pincel",
            "jardinería", "pala", "manguera",
            "furniture", "sofa", "chair", "table", "bed", "mattress",
            "lamp", "appliance", "washer", "refrigerator", "microwave",
            "decoration", "curtain", "rug", "garden", "tool"
        ),
        "Comida" to listOf(
            "fruta", "verdura", "vegetal", "carne", "pollo", "cerdo",
            "pescado", "mariscos", "huevo", "queso", "yogur",
            "arroz", "frijol", "frijoles", "lenteja", "pasta", "harina",
            "pan", "galleta", "cereal", "azúcar", "sal", "aceite", "vinagre",
            "salsa", "condimento", "especia", "chile", "pimienta",
            "chocolate", "dulce", "caramelo", "helado", "postre", "pastel",
            "torta", "snack", "botana", "frituras", "papas fritas",
            "comida", "alimento", "comestible", "ingrediente",
            "conserva", "enlatado", "mermelada", "miel",
            "food", "fruit", "vegetable", "meat", "chicken", "fish",
            "rice", "bread", "sauce", "sugar", "spice"
        ),
        "Bebida" to listOf(
            "agua", "jugo", "zumo", "refresco", "soda", "gaseosa",
            "café", "té", "batido", "smoothie", "atole",
            "horchata", "jamaica", "tamarindo", "limonada",
            "cerveza", "vino", "licor", "tequila", "ron", "vodka",
            "whisky", "mezcal", "aguardiente", "cóctel", "cocktail",
            "champán", "champagne", "sidra",
            "energizante", "isotónica",
            "termo", "cantimplora", "botella", "copa",
            "water", "coffee", "juice", "beer", "wine",
            "drink", "beverage", "bottle", "tea"
        )
        // "Otros" omitido intencionalmente: sin keywords fijas, solo usa IA
    )

    /**
     * Filtra los productos cuyo nombre o descripción contengan
     * al menos una keyword de la categoría (case-insensitive).
     * Retorna lista vacía si la categoría no tiene keywords (ej. "Otros").
     */
    fun filterByKeywords(productos: List<Producto>, categoria: String): List<Producto> {
        val keys = keywords[categoria] ?: return emptyList()
        return productos.filter { producto ->
            val texto = "${producto.nombre} ${producto.descripcion}".lowercase()
            keys.any { keyword -> texto.contains(keyword.lowercase()) }
        }
    }
}
