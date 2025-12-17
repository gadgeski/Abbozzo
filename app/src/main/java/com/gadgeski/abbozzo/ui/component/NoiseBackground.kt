package com.gadgeski.abbozzo.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.gadgeski.abbozzo.ui.theme.BlackBackground
import com.gadgeski.abbozzo.ui.theme.NeonCyan
import com.gadgeski.abbozzo.ui.theme.NeonPurple
import kotlin.random.Random

@Composable
fun NoiseBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        // Fill pure black
        drawRect(color = BlackBackground)

        // Draw random subtle noise/grain (simulated with small circles or lines to avoid heavy image assets)
        // For performance, we'll just do a few gradient blobs
        
        val width = size.width
        val height = size.height

        // Blob 1: Purple
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(NeonPurple.copy(alpha = 0.15f), Color.Transparent),
                center = Offset(width * 0.2f, height * 0.3f),
                radius = width * 0.5f
            ),
            center = Offset(width * 0.2f, height * 0.3f),
            radius = width * 0.5f
        )

        // Blob 2: Cyan
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(NeonCyan.copy(alpha = 0.1f), Color.Transparent),
                center = Offset(width * 0.8f, height * 0.7f),
                radius = width * 0.6f
            ),
            center = Offset(width * 0.8f, height * 0.7f),
            radius = width * 0.6f
        )
        
        // "Digital Rain" lines effect (abstract)
        for(i in 0..10) {
            val x = Random.nextFloat() * width
            drawLine(
                color = Color.White.copy(alpha = 0.03f),
                start = Offset(x, 0f),
                end = Offset(x, height),
                strokeWidth = 1f
            )
        }
    }
}
