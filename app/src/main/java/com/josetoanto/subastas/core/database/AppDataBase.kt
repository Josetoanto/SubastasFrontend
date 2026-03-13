package com.josetoanto.subastas.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.josetoanto.subastas.core.database.dao.PujaDao
import com.josetoanto.subastas.core.database.entities.PujaEntity

@Database(
    entities = [PujaEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun pujaDao(): PujaDao
}
