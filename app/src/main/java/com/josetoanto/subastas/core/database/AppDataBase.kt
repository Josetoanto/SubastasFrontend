package com.josetoanto.subastas.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.josetoanto.subastas.core.database.dao.ProductoDao
import com.josetoanto.subastas.core.database.dao.PujaDao
import com.josetoanto.subastas.core.database.entities.ProductoEntity
import com.josetoanto.subastas.core.database.entities.PujaEntity

@Database(
    entities = [PujaEntity::class, ProductoEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun pujaDao(): PujaDao
    abstract fun productoDao(): ProductoDao
}
