package com.josetoanto.subastas.features.productos.presentation.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.josetoanto.subastas.features.productos.presentation.components.ProductoCard
import com.josetoanto.subastas.features.productos.presentation.viewmodels.HomeViewModel
import kotlin.math.roundToInt

// ─────────────────────────────────────────────────────────────
// Composables privados reutilizables
// ─────────────────────────────────────────────────────────────

@Composable
private fun AiCategoryChips(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val aiCategories = listOf("Todos", "Tecnología", "Hogar", "Comida", "Bebida", "Otros")
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        items(aiCategories, key = { it }) { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = { Text(category) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}

@Composable
private fun VolverButton(onBack: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onBack,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text("Volver a Todas", fontWeight = FontWeight.SemiBold)
    }
}

// ─────────────────────────────────────────────────────────────
// Pantalla principal
// ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToCreateProduct: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToMyAuctions: () -> Unit,
    onNavigateToFlashAuctions: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted -> viewModel.onLocationPermissionResult(granted) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (state.isUsingNearMe) "Subastas Cercanas" else "Subastas")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = { locationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }) {
                        Icon(Icons.Filled.MyLocation, contentDescription = "Cerca de mí")
                    }
                    IconButton(onClick = viewModel::onToggleFilterSheet) {
                        Icon(Icons.Filled.FilterList, contentDescription = "Filtros")
                    }
                    IconButton(onClick = viewModel::loadProductos) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Actualizar")
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Filled.Person, contentDescription = "Perfil")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreateProduct) {
                Icon(Icons.Filled.Add, contentDescription = "Crear subasta")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                // 1. Cargando
                state.isLoading -> CircularProgressIndicator()

                // 2. Error
                state.errorMessage != null -> Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(
                        text = state.errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Button(onClick = viewModel::loadProductos) {
                        Text("Reintentar", fontWeight = FontWeight.SemiBold)
                    }
                }

                // 3. Sin resultados con filtro IA activo → pantalla vacía + botón volver
                state.productos.isEmpty() && state.aiCategory != "Todos" && !state.isCategorizingByAi -> Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    AiCategoryChips(
                        selectedCategory = state.aiCategory,
                        onCategorySelected = { viewModel.onAiCategorySelected(it) }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Icon(
                        imageVector = Icons.Filled.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sin resultados en \"${state.aiCategory}\"",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "No se encontraron subastas en esta categoría.\nPrueba con otra o vuelve a ver todas.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    VolverButton(
                        onBack = { viewModel.onAiCategorySelected("Todos") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // 4. Sin productos en absoluto (plataforma vacía)
                state.productos.isEmpty() -> Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No Hay Subastas Disponibles",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Crea la primera subasta y empieza a vender",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onNavigateToCreateProduct) {
                        Text("Crear Subasta")
                    }
                }

                // 5. Lista de productos
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Chips de categorías IA
                    item {
                        AiCategoryChips(
                            selectedCategory = state.aiCategory,
                            onCategorySelected = { viewModel.onAiCategorySelected(it) }
                        )
                    }

                    // Botón "Volver a Todas" cuando hay filtro activo con resultados y búsqueda finalizada
                    if (state.aiCategory != "Todos" && !state.isCategorizingByAi) {
                        item {
                            VolverButton(
                                onBack = { viewModel.onAiCategorySelected("Todos") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 4.dp)
                            )
                        }
                    }

                    // Indicador de progreso mientras clasifica la IA
                    if (state.isCategorizingByAi) {
                        item {
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Botones de navegación rápida
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = onNavigateToFavorites,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Filled.Favorite, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Favoritos",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            OutlinedButton(
                                onClick = onNavigateToMyAuctions,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Filled.List, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Mis Subastas",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            OutlinedButton(
                                onClick = onNavigateToFlashAuctions,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Filled.Bolt, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Flash",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Tarjetas de subastas
                    items(state.productos, key = { it.id }) { producto ->
                        ProductoCard(
                            producto = producto,
                            isFavorite = state.favoriteIds.contains(producto.id),
                            onToggleFavorite = { viewModel.toggleFavorite(producto.id) },
                            primaryActionText = "Pujar",
                            onPrimaryAction = {
                                viewModel.onProductOpened(producto.id)
                                onNavigateToDetail(producto.id)
                            },
                            onClick = {
                                viewModel.onProductOpened(producto.id)
                                onNavigateToDetail(producto.id)
                            }
                        )
                    }
                }
            }

            // Diálogo de permiso de ubicación
            if (state.locationPermissionDenied) {
                androidx.compose.material3.AlertDialog(
                    onDismissRequest = viewModel::onDismissLocationDenied,
                    title = { Text("Permiso de Ubicación") },
                    text = { Text("Para buscar subastas cerca de ti, necesitamos usar tu ubicación. Autoriza el permiso en la configuración.") },
                    confirmButton = {
                        Button(onClick = viewModel::onDismissLocationDenied) {
                            Text("Aceptar")
                        }
                    }
                )
            }
        }
    }

    // Bottom sheet de filtros adicionales
    if (state.showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = viewModel::onDismissFilterSheet,
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Filtros", style = MaterialTheme.typography.titleLarge)

                Text("Radio de Búsqueda: ${state.filterRadioKm.roundToInt()} km")
                Slider(
                    value = state.filterRadioKm.toFloat(),
                    onValueChange = { viewModel.onFilterRadioKmChange(it.toDouble()) },
                    valueRange = 10f..200f,
                    steps = 18
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Solo Entrega en Persona")
                    Switch(
                        checked = state.filterSoloEntregaPersona,
                        onCheckedChange = viewModel::onFilterSoloEntregaPersonaChange
                    )
                }

                Button(
                    onClick = {
                        viewModel.onDismissFilterSheet()
                        viewModel.loadProductos()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Aplicar Filtros")
                }
            }
        }
    }
}
