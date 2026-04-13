package com.josetoanto.subastas.features.productos.presentation.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.josetoanto.subastas.features.productos.presentation.components.DateTimePickerField
import com.josetoanto.subastas.features.productos.presentation.viewmodels.CreateProductoViewModel
import android.net.Uri
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import com.josetoanto.subastas.features.productos.presentation.components.ImagePickerField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProductoScreen(
    onProductCreated: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: CreateProductoViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.fetchCurrentLocation()
        }
    }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            viewModel.resetSuccess()
            onProductCreated()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Subasta") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(value = state.nombre, onValueChange = viewModel::onNombreChange,
                label = { Text("Nombre del Producto") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            OutlinedTextField(value = state.descripcion, onValueChange = viewModel::onDescripcionChange,
                label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), minLines = 3, maxLines = 5)

            OutlinedTextField(value = state.precioInicial, onValueChange = viewModel::onPrecioInicialChange,
                label = { Text("Precio Inicial") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))

            if (state.esRelampago) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Bolt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "FLASH ACTIVADA: duración automática 2 minutos",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            ImagePickerField(
                imageUri = state.imageUri,
                onImageSelected = viewModel::onImageSelected,
                hasCameraFeature = viewModel.hasCameraFeature
            )

            if (!state.esRelampago) {
                DateTimePickerField(
                    value = state.fechaInicio,
                    onValueChange = viewModel::onFechaInicioChange,
                    label = "Fecha Inicio",
                    enabled = true
                )

                DateTimePickerField(
                    value = state.fechaFin,
                    onValueChange = viewModel::onFechaFinChange,
                    label = "Fecha Fin",
                    enabled = true
                )
            }

            OutlinedButton(
                onClick = { locationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoadingLocation
            ) {
                if (state.isLoadingLocation) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Icon(Icons.Filled.MyLocation, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        if (state.latitud != null) "Ubicación: %.4f, %.4f".format(state.latitud, state.longitud)
                        else "Usar Mi Ubicación"
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Entrega en Persona")
                Switch(checked = state.entregaEnPersona, onCheckedChange = viewModel::onEntregaEnPersonaChange)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Subasta Relámpago")
                Switch(checked = state.esRelampago, onCheckedChange = viewModel::onEsRelampagoChange)
            }

            if (state.esRelampago) {
                Text(
                    text = "Las fechas se configuran automáticamente y no se muestran durante flash. La duración fija es de 2 minutos.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            state.errorMessage?.let { error ->
                Text(text = error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Button(
                onClick = viewModel::createProducto,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                else Text("Publicar Subasta")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
