package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.ui.theme.ScanlineColor
import com.example.ui.theme.TerminalBackground
import com.example.ui.theme.TerminalGreen
import kotlin.random.Random

@Composable
fun MatrixBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "matrix_rain")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val randomOffsets = remember {
        List(24) { Random.nextFloat() }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TerminalBackground)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val colCount = 20
            val colWidth = width / colCount

            for (i in 0 until colCount) {
                val seedOffset = randomOffsets[i % randomOffsets.size]
                val currentY = ((phase + seedOffset) % 1f) * (height + 200f) - 100f
                val startX = i * colWidth + colWidth / 2

                drawLine(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            TerminalGreen.copy(alpha = 0.15f),
                            TerminalGreen.copy(alpha = 0.45f),
                            Color.White.copy(alpha = 0.8f)
                        ),
                        startY = currentY - 140f,
                        endY = currentY
                    ),
                    start = Offset(startX, (currentY - 140f).coerceAtLeast(0f)),
                    end = Offset(startX, currentY.coerceAtMost(height)),
                    strokeWidth = 2.5f
                )
            }

            drawScanlines(size.width, size.height)
        }

        content()
    }
}

private fun DrawScope.drawScanlines(width: Float, height: Float) {
    var y = 0f
    val step = 6f
    while (y < height) {
        drawLine(
            color = ScanlineColor,
            start = Offset(0f, y),
            end = Offset(width, y),
            strokeWidth = 1f
        )
        y += step
    }
}
