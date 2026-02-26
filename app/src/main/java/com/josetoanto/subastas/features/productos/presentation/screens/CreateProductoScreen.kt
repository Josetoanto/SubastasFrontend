package com.josetoanto.subastas.features.productos.presentation.screens

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.josetoanto.subastas.features.productos.presentation.viewmodels.CreateProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProductoScreen(
    onProductCreated: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: CreateProductoViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

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
                label = { Text("Nombre del producto") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            OutlinedTextField(value = state.descripcion, onValueChange = viewModel::onDescripcionChange,
                label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), minLines = 3, maxLines = 5)

            OutlinedTextField(value = state.precioInicial, onValueChange = viewModel::onPrecioInicialChange,
                label = { Text("Precio inicial") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))

            OutlinedTextField(value = state.imagenUrl, onValueChange = viewModel::onImagenUrlChange,
                label = { Text("URL de imagen (opcional)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            OutlinedTextField(value = state.fechaInicio, onValueChange = viewModel::onFechaInicioChange,
                label = { Text("Fecha inicio (YYYY-MM-DDTHH:MM:SS)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            OutlinedTextField(value = state.fechaFin, onValueChange = viewModel::onFechaFinChange,
                label = { Text("Fecha fin (YYYY-MM-DDTHH:MM:SS)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            state.errorMessage?.let { error ->
                Text(text = error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Button(
                onClick = viewModel::createProducto,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) CircularProgressIndicator(modifier = Modifier.height(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                else Text("Publicar Subasta")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
