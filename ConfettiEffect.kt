package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.ThemePalette
import kotlin.random.Random

private data class Particle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val color: Color,
    val size: Float,
    val rotationSpeed: Float
)

@Composable
fun ConfettiEffect(
    palette: ThemePalette,
    modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(0f) }

    val particles = remember {
        val colors = listOf(palette.xColor, palette.oColor, palette.accent, Color.Yellow, Color.White)
        List(60) {
            Particle(
                x = Random.nextFloat(),
                y = 0.3f + Random.nextFloat() * 0.2f,
                vx = (Random.nextFloat() - 0.5f) * 1.2f,
                vy = -0.8f - Random.nextFloat() * 0.8f,
                color = colors.random(),
                size = 12f + Random.nextFloat() * 16f,
                rotationSpeed = (Random.nextFloat() - 0.5f) * 10f
            )
        }
    }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
        )
    }

    val currentProgress = progress.value

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        particles.forEach { p ->
            val px = (p.x + p.vx * currentProgress) * w
            val py = (p.y + p.vy * currentProgress + 1.2f * currentProgress * currentProgress) * h
            val alpha = (1f - currentProgress).coerceIn(0f, 1f)

            drawRect(
                color = p.color.copy(alpha = alpha),
                topLeft = Offset(px, py),
                size = Size(p.size, p.size)
            )
        }
    }
}
