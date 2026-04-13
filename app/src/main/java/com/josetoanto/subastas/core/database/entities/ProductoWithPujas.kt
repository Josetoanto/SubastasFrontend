package com.josetoanto.subastas.core.database.entities

import androidx.room.Embedded
import androidx.room.Relation

data class ProductoWithPujas(
    @Embedded val producto: ProductoEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "producto_id"
    )
    val pujas: List<PujaEntity>
)
