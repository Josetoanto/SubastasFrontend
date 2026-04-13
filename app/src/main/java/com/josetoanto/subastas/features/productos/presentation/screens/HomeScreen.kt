package com.josetoanto.subastas.features.productos.presentation.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.josetoanto.subastas.features.productos.presentation.components.ProductoCard
import com.josetoanto.subastas.features.productos.presentation.viewmodels.HomeViewModel
import kotlin.math.roundToInt

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
                    Text(if (state.isUsingNearMe) "Subastas cercanas" else "Subastas")
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
                state.isLoading -> CircularProgressIndicator()
                state.errorMessage != null -> Text(
                    text = state.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error
                )
                state.productos.isEmpty() -> Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("No hay subastas disponibles")
                    Button(onClick = onNavigateToCreateProduct) {
                        Text("Agregar subasta")
                    }
                }
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = onNavigateToFavorites,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Filled.Favorite, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Favoritos",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                            OutlinedButton(
                                onClick = onNavigateToMyAuctions,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Filled.List, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Mis subastas",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                            OutlinedButton(
                                onClick = onNavigateToFlashAuctions,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Filled.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Flash",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }

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

            if (state.locationPermissionDenied) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = viewModel::onDismissLocationDenied) {
                            Text("OK")
                        }
                    }
                ) {
                    Text("Permiso de ubicación denegado")
                }
            }
        }
    }

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

                Text("Radio de búsqueda: ${state.filterRadioKm.roundToInt()} km")
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
                    Text("Solo entrega en persona")
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
                    Text("Aplicar filtros")
                }
            }
        }
    }
}
