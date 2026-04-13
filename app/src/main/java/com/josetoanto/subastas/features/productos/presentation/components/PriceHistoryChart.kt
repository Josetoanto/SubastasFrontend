package com.josetoanto.subastas.features.productos.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.josetoanto.subastas.features.productos.domain.entities.PriceHistoryEntry

@Composable
fun PriceHistoryChart(
    entries: List<PriceHistoryEntry>,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    if (entries.size < 2) return

    val lineColor = MaterialTheme.colorScheme.primary
    val dotColor = MaterialTheme.colorScheme.secondary
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant
    val topTextColor = MaterialTheme.colorScheme.onSurface
    val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)

    val minPrice = entries.minOf { it.precio }
    val maxPrice = entries.maxOf { it.precio }
    val priceRange = if (maxPrice == minPrice) 1.0 else maxPrice - minPrice

    Canvas(modifier = modifier) {
        val leftPadding = 58.dp.toPx()
        val rightPadding = 16.dp.toPx()
        val topPadding = 18.dp.toPx()
        val bottomPadding = 30.dp.toPx()
        val chartWidth = size.width - leftPadding - rightPadding
        val chartHeight = size.height - topPadding - bottomPadding

        fun xOf(index: Int): Float =
            leftPadding + (index.toFloat() / (entries.size - 1)) * chartWidth

        fun yOf(price: Double): Float =
            topPadding + chartHeight - ((price - minPrice) / priceRange * chartHeight).toFloat()

        val labelPaint = android.graphics.Paint().apply {
            color = textColor.toArgb()
            textSize = 24f
            isAntiAlias = true
        }

        // Horizontal guides and Y-axis labels.
        repeat(4) { i ->
            val progress = i / 3f
            val y = topPadding + chartHeight * progress
            drawLine(
                color = gridColor,
                start = Offset(leftPadding, y),
                end = Offset(size.width - rightPadding, y),
                strokeWidth = 1.dp.toPx()
            )

            val value = maxPrice - (priceRange * progress)
            drawContext.canvas.nativeCanvas.drawText(
                "%.0f".format(value),
                8.dp.toPx(),
                y + 8f,
                labelPaint
            )
        }

        val path = Path()
        entries.forEachIndexed { i, entry ->
            val x = xOf(i)
            val y = yOf(entry.precio)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        entries.forEachIndexed { i, entry ->
            drawCircle(
                color = dotColor,
                radius = 5.dp.toPx(),
                center = Offset(xOf(i), yOf(entry.precio))
            )
        }

        val topValuePaint = android.graphics.Paint().apply {
            color = topTextColor.toArgb()
            textSize = 24f
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.RIGHT
        }
        drawContext.canvas.nativeCanvas.drawText(
            "Máx: ${"%.2f".format(maxPrice)}",
            size.width - rightPadding,
            topPadding - 4f,
            topValuePaint
        )

        if (entries.isNotEmpty()) {
            val fechaInicio = entries.first().fecha.take(10)
            val fechaFin = entries.last().fecha.take(10)
            drawContext.canvas.nativeCanvas.drawText(
                fechaInicio,
                leftPadding,
                size.height - 2f,
                labelPaint
            )
            val endPaint = android.graphics.Paint().apply {
                color = textColor.toArgb()
                textSize = 24f
                isAntiAlias = true
                textAlign = android.graphics.Paint.Align.RIGHT
            }
            drawContext.canvas.nativeCanvas.drawText(
                fechaFin,
                size.width - rightPadding,
                size.height - 2f,
                endPaint
            )
        }

        drawContext.canvas.nativeCanvas.drawText(
            "Mín: ${"%.2f".format(minPrice)}",
            leftPadding,
            size.height - bottomPadding - 6f,
            labelPaint
        )
    }
}
