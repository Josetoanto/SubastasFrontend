package com.josetoanto.subastas.core.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

private val Context.localDataStore: DataStore<Preferences> by preferencesDataStore(name = "subastas_local_state")

data class ActivitySummary(
    val totalAperturas: Int = 0,
    val totalFavoritosAcciones: Int = 0,
    val ultimaApertura: String? = null,
    val recientesIds: List<Int> = emptyList()
)

@Singleton
class AppLocalStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val FAVORITE_IDS = stringSetPreferencesKey("favorite_product_ids")
        private val CREATED_AUCTION_IDS = stringSetPreferencesKey("created_auction_ids")
        private val TOTAL_OPENS = intPreferencesKey("activity_total_opens")
        private val TOTAL_FAVORITE_ACTIONS = intPreferencesKey("activity_total_favorite_actions")
        private val LAST_OPEN_AT = stringPreferencesKey("activity_last_open_at")
        private val RECENT_IDS = stringPreferencesKey("activity_recent_opened_ids")
    }

    val favoriteIdsFlow: Flow<Set<Int>> = context.localDataStore.data.map { prefs ->
        (prefs[FAVORITE_IDS] ?: emptySet()).mapNotNull { it.toIntOrNull() }.toSet()
    }

    val createdAuctionIdsFlow: Flow<Set<Int>> = context.localDataStore.data.map { prefs ->
        (prefs[CREATED_AUCTION_IDS] ?: emptySet()).mapNotNull { it.toIntOrNull() }.toSet()
    }

    val activitySummaryFlow: Flow<ActivitySummary> = context.localDataStore.data.map { prefs ->
        ActivitySummary(
            totalAperturas = prefs[TOTAL_OPENS] ?: 0,
            totalFavoritosAcciones = prefs[TOTAL_FAVORITE_ACTIONS] ?: 0,
            ultimaApertura = prefs[LAST_OPEN_AT],
            recientesIds = (prefs[RECENT_IDS] ?: "")
                .split(',')
                .mapNotNull { it.toIntOrNull() }
        )
    }

    suspend fun toggleFavorite(productId: Int): Boolean {
        var isFavoriteNow = false
        context.localDataStore.edit { prefs ->
            val current = (prefs[FAVORITE_IDS] ?: emptySet()).toMutableSet()
            val key = productId.toString()
            isFavoriteNow = if (current.contains(key)) {
                current.remove(key)
                false
            } else {
                current.add(key)
                true
            }
            prefs[FAVORITE_IDS] = current
        }
        return isFavoriteNow
    }

    suspend fun recordFavoriteAction() {
        context.localDataStore.edit { prefs ->
            prefs[TOTAL_FAVORITE_ACTIONS] = (prefs[TOTAL_FAVORITE_ACTIONS] ?: 0) + 1
        }
    }

    suspend fun recordProductOpened(productId: Int) {
        val now = formatNow()
        context.localDataStore.edit { prefs ->
            val currentRecent = (prefs[RECENT_IDS] ?: "")
                .split(',')
                .mapNotNull { it.toIntOrNull() }
                .toMutableList()
            currentRecent.remove(productId)
            currentRecent.add(0, productId)
            val trimmed = currentRecent.take(12)

            prefs[TOTAL_OPENS] = (prefs[TOTAL_OPENS] ?: 0) + 1
            prefs[LAST_OPEN_AT] = now
            prefs[RECENT_IDS] = trimmed.joinToString(",")
        }
    }

    suspend fun recordCreatedAuction(productId: Int) {
        context.localDataStore.edit { prefs ->
            val current = (prefs[CREATED_AUCTION_IDS] ?: emptySet()).toMutableSet()
            current.add(productId.toString())
            prefs[CREATED_AUCTION_IDS] = current
        }
    }

    suspend fun clearAll() {
        context.localDataStore.edit { prefs ->
            prefs.remove(FAVORITE_IDS)
            prefs.remove(CREATED_AUCTION_IDS)
            prefs.remove(TOTAL_OPENS)
            prefs.remove(TOTAL_FAVORITE_ACTIONS)
            prefs.remove(LAST_OPEN_AT)
            prefs.remove(RECENT_IDS)
        }
    }

    private fun formatNow(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.US)
        return LocalDateTime.now().format(formatter)
    }
}