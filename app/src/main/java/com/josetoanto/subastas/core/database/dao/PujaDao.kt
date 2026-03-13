package com.josetoanto.subastas.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.josetoanto.subastas.core.database.entities.PujaEntity

@Dao
interface PujaDao {

    @Query("SELECT * FROM pujas WHERE producto_id = :productoId ORDER BY cantidad DESC")
    suspend fun getPujasByProductoId(productoId: Int): List<PujaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pujas: List<PujaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(puja: PujaEntity)

    @Query("DELETE FROM pujas WHERE producto_id = :productoId")
    suspend fun clearByProductoId(productoId: Int)
}
