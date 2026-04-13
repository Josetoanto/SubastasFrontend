package com.josetoanto.subastas.features.productos.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.josetoanto.subastas.features.productos.domain.entities.Producto

@Composable
fun ProductoCard(
    producto: Producto,
    onClick: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: (() -> Unit)? = null,
    primaryActionText: String? = null,
    onPrimaryAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val endMillis = com.josetoanto.subastas.core.utils.parseIsoToEpochMillisOrNull(producto.fechaFin)
    val isFinished = endMillis != null && endMillis <= System.currentTimeMillis()
    val finalStatus = if (isFinished) "Finalizado" else producto.status.replaceFirstChar { it.uppercase() }
    val isActivo = finalStatus.lowercase() == "activo"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.padding(
                    start = 12.dp, 
                    top = 12.dp, 
                    end = 12.dp, 
                    bottom = if (primaryActionText != null && onPrimaryAction != null) 0.dp else 12.dp
                ),
                verticalAlignment = Alignment.Top
            ) {
                AsyncImage(
                    model = producto.imagenUrl.ifBlank { null },
                    contentDescription = producto.nombre,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(MaterialTheme.shapes.medium)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Title row with favorite icon aligned to the right
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = producto.nombre,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        if (onToggleFavorite != null) {
                            IconButton(
                                onClick = onToggleFavorite,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                    contentDescription = if (isFavorite) "Quitar Favorito" else "Agregar Favorito",
                                    tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Description
                    Text(
                        text = producto.descripcion,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Price and Badges row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Actual: $${producto.precioActual}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (producto.esRelampago) {
                                Badge(containerColor = MaterialTheme.colorScheme.error) {
                                    Icon(
                                        imageVector = Icons.Filled.Bolt,
                                        contentDescription = "Relámpago",
                                        modifier = Modifier.size(10.dp),
                                        tint = MaterialTheme.colorScheme.onError
                                    )
                                    Text(
                                        text = "FLASH",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onError
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Badge(containerColor = if (isActivo) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant) {
                                Text(
                                    text = finalStatus,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }

            if (primaryActionText != null && onPrimaryAction != null) {
                // Remove top padding, add smaller spacing between content above
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onPrimaryAction,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp)
                ) {
                    Text(primaryActionText)
                }
            }
        }
    }
}
