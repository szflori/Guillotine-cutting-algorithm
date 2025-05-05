package com.example.packingoptimizerapp.android.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.guillotine_cutting_app.logic.CuttingResult
import com.example.guillotine_cutting_app.logic.SheetResult
import kotlin.math.min

@Composable
fun CuttingResultScreen(result: CuttingResult, navController: NavController) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
                Text(
                    text = "Felhasznált készletek: ${result.sheets.size} db",
                    style = MaterialTheme.typography.titleMedium
                )

                result.sheets.forEach { sheetResult ->
                    Text(
                        "Készlet #${sheetResult.sheetNumber}: ${sheetResult.sheet.width} x ${sheetResult.sheet.height} cm",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Box( modifier = Modifier
                        .background(Color.White)
                        .padding(8.dp)
                        .align(Alignment.CenterHorizontally)){
                        SheetCanvas(sheetResult)
                    }
                    SheetInfo(sheetResult)

                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                }

            Button(
                onClick = {
                    navController.currentBackStackEntry?.savedStateHandle?.set("cutting_result", result)
                    navController.navigate("cutting_steps")
                }
            ) {
                Text("Lépések megtekintése")
            }
        }
}


@Composable
fun SheetCanvas(sheetResult: SheetResult) {
    val canvasSizeDp = 300.dp

    Canvas(
        modifier = Modifier.size(canvasSizeDp)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // A scale arányosan lekicsinyíti a tartalmat a canvas méretéhez
        val scaleX = canvasWidth / sheetResult.sheet.width
        val scaleY = canvasHeight / sheetResult.sheet.height
        val scale = min(scaleX, scaleY) // arányos, hogy minden beleférjen

        drawRect(
            color = Color.Black,
            size = Size(sheetResult.sheet.width * scale, sheetResult.sheet.height * scale),
            style = Stroke(width = 3.dp.toPx())
        )

        sheetResult.placedPieces.forEach { placedPiece ->
            val pieceWidth =
                (if (placedPiece.rotated) placedPiece.piece.height else placedPiece.piece.width) * scale
            val pieceHeight =
                (if (placedPiece.rotated) placedPiece.piece.width else placedPiece.piece.height) * scale

            drawRect(
                color = placedPiece.piece.color,
                topLeft = Offset(placedPiece.x * scale, placedPiece.y * scale),
                size = Size(pieceWidth, pieceHeight),
                style = Stroke(width = 2.dp.toPx())
            )

            drawContext.canvas.nativeCanvas.drawText(
                "${placedPiece.piece.width}x${placedPiece.piece.height}",
                placedPiece.x * scale + 4.dp.toPx(),
                placedPiece.y * scale + 16.dp.toPx(),
                android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 10.sp.toPx()
                }
            )
        }
    }
}

@Composable
fun SheetInfo(sheetResult: SheetResult) {
    Spacer(modifier = Modifier.height(8.dp))

    Text(
        "Kivágott lapok:",
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
    )

    sheetResult.placedPieces
        .groupingBy { "${it.piece.width} x ${it.piece.height} cm" }
        .eachCount()
        .forEach { (size, count) ->
            Text(
                text = "$size → $count db",
                style = MaterialTheme.typography.bodyMedium
            )
        }

    Text(
        text = "Maradék területe: ${sheetResult.wasteArea} cm²",
        style = MaterialTheme.typography.bodyMedium
    )
}
