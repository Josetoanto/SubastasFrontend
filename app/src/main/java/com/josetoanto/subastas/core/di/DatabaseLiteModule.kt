package com.josetoanto.subastas.core.di

import android.content.Context
import androidx.room.Room
import com.josetoanto.subastas.core.database.AppDataBase
import com.josetoanto.subastas.core.database.dao.PujaDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseLiteModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDataBase =
        Room.databaseBuilder(
            context,
            AppDataBase::class.java,
            "subastas_cache_db"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun providePujaDao(database: AppDataBase): PujaDao =
        database.pujaDao()

    @Provides
    @Singleton
    fun provideProductoDao(database: AppDataBase): com.josetoanto.subastas.core.database.dao.ProductoDao =
        database.productoDao()
}
