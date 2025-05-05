package com.example.packingoptimizerapp.android.ui.screens

import android.graphics.Paint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.foundation.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guillotine_cutting_app.logic.CuttingStep

@Composable
fun CuttingStepVisualizer(step: CuttingStep) {
    val canvasSize = 300.dp

    Box(
        modifier = Modifier
            .background(Color.White)
    ) {
        Canvas(modifier = Modifier.size(canvasSize)) {
            val maxWidth = listOf(
                step.availableAreas.maxOfOrNull { it.x + it.sheet.width } ?: 0,
                step.placedPieces.maxOfOrNull { it.x + if (it.rotated) it.piece.height else it.piece.width } ?: 0
            ).max()

            val maxHeight = listOf(
                step.availableAreas.maxOfOrNull { it.y + it.sheet.height } ?: 0,
                step.placedPieces.maxOfOrNull { it.y + if (it.rotated) it.piece.width else it.piece.height } ?: 0
            ).max()

            val scaleX = size.width / maxWidth
            val scaleY = size.height / maxHeight
            val scale = minOf(scaleX, scaleY)

            // 1. Elérhető területek (halvány színnel)
            step.availableAreas.forEach { area ->
                drawRect(
                    color = Color.LightGray,
                    topLeft = Offset(area.x * scale, area.y * scale),
                    size = Size(area.sheet.width * scale, area.sheet.height * scale),
                    style = Stroke(width = 1.dp.toPx())
                )
            }

            // 2. Aktuálisan használt terület kiemelve
            step.currentArea?.let { area ->
                drawRect(
                    color = Color.Yellow.copy(alpha = 0.3f),
                    topLeft = Offset(area.x * scale, area.y * scale),
                    size = Size(area.sheet.width * scale, area.sheet.height * scale),
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            // Már elhelyezett darabok
            step.placedPieces.forEach { placedPiece ->
                val width = if (placedPiece.rotated) placedPiece.piece.height else placedPiece.piece.width
                val height = if (placedPiece.rotated) placedPiece.piece.width else placedPiece.piece.height

                drawRect(
                    color = placedPiece.piece.color,
                    topLeft = Offset(placedPiece.x * scale, placedPiece.y * scale),
                    size = Size(width * scale, height * scale),
                    style = Stroke(width = 2.dp.toPx())
                )

                drawContext.canvas.nativeCanvas.drawText(
                    "${placedPiece.piece.width}x${placedPiece.piece.height}",
                    placedPiece.x * scale + 4.dp.toPx(),
                    placedPiece.y * scale + 14.dp.toPx(),
                    Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 10.sp.toPx()
                    }
                )
            }

            // Aktuálisan elhelyezendő darab (ha van)
            step.currentPiece?.let { piece ->
                step.currentArea?.let { area ->
                    val rotated = !(piece.width <= area.sheet.width && piece.height <= area.sheet.height)
                    val width = if (rotated) piece.height else piece.width
                    val height = if (rotated) piece.width else piece.height

                    drawRect(
                        color = Color.Red.copy(alpha = 0.5f),
                        topLeft = Offset(area.x * scale, area.y * scale),
                        size = Size(width * scale, height * scale)
                    )
                }
            }
        }
    }
}