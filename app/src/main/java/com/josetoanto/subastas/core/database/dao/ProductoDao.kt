package com.josetoanto.subastas.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.josetoanto.subastas.core.database.entities.ProductoEntity
import com.josetoanto.subastas.core.database.entities.ProductoWithPujas

@Dao
interface ProductoDao {

    @Query("SELECT * FROM productos")
    suspend fun getAllProductos(): List<ProductoEntity>

    @Query("SELECT * FROM productos WHERE id = :productoId")
    suspend fun getProductoById(productoId: Int): ProductoEntity?

    @Transaction
    @Query("SELECT * FROM productos WHERE id = :productoId")
    suspend fun getProductoConPujas(productoId: Int): ProductoWithPujas?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(productos: List<ProductoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(producto: ProductoEntity)

    @Query("DELETE FROM productos")
    suspend fun clearAll()
}
