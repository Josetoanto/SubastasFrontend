package com.josetoanto.subastas.features.auth.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.josetoanto.subastas.features.auth.presentation.components.AuthTextField
import com.josetoanto.subastas.features.auth.presentation.viewmodels.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.registerState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            viewModel.resetRegisterSuccess()
            onRegisterSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Crear cuenta") })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Únete a Subastas",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                AuthTextField(
                    value = state.nombre,
                    onValueChange = viewModel::onRegisterNombreChange,
                    label = "Nombre completo",
                    leadingIcon = Icons.Filled.Person,
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.nombreError != null,
                    supportingText = state.nombreError
                )

                AuthTextField(
                    value = state.email,
                    onValueChange = viewModel::onRegisterEmailChange,
                    label = "Correo electronico",
                    leadingIcon = Icons.Filled.Email,
                    keyboardType = KeyboardType.Email,
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.emailError != null,
                    supportingText = state.emailError
                )

                AuthTextField(
                    value = state.password,
                    onValueChange = viewModel::onRegisterPasswordChange,
                    label = "Contrasena",
                    leadingIcon = Icons.Filled.Lock,
                    isPassword = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.passwordError != null,
                    supportingText = state.passwordError
                )

                AuthTextField(
                    value = state.confirmPassword,
                    onValueChange = viewModel::onRegisterConfirmPasswordChange,
                    label = "Confirmar contrasena",
                    leadingIcon = Icons.Filled.Lock,
                    isPassword = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.confirmPasswordError != null,
                    supportingText = state.confirmPasswordError
                )

                state.errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Button(
                    onClick = viewModel::register,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.height(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Registrarse")
                    }
                }

                TextButton(onClick = onNavigateToLogin) {
                    Text("¿Ya tienes cuenta? Inicia sesión")
                }
            }
        }
    }
}
