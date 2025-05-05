package com.example.guillotine_cutting_app.logic

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import kotlinx.parcelize.Parcelize
import kotlin.math.min

enum class CuttingStrategy {
    FirstFit,       // első hely, ahol elfér
    LargestFirst,   // legnagyobb darab először
    BestAreaFit     // legkisebb maradék terület
}

fun CuttingStrategy.getDescription(): String {
    return when (this) {
        CuttingStrategy.FirstFit -> "Első szabad helyre helyezi a darabot, gyors, de nem feltétlenül hatékony."
        CuttingStrategy.LargestFirst -> "Először a legnagyobb területű darabot helyezi el, csökkentve a helypazarlást."
        CuttingStrategy.BestAreaFit -> "Oda helyezi a darabot, ahol a legkisebb hulladék marad a vágás után."
    }
}

@Parcelize
data class Sheet(val width: Int, val height: Int):Parcelable

@Parcelize
data class Piece(val width: Int, val height: Int,  val quantity: Int, val colorLong: ULong): Parcelable {
    val color: Color get() = Color(colorLong)
}

@Parcelize
data class PlacedPiece(val piece: Piece, val x: Int, val y: Int, val rotated: Boolean) : Parcelable

@Parcelize
data class SheetResult(
    val sheetNumber: Int,
    val sheet: Sheet,
    val placedPieces: List<PlacedPiece>,
    val wasteArea: Int,
) : Parcelable

@Parcelize
data class CuttingStep(
    val sheetNumber: Int,
    val placedPieces: List<PlacedPiece>,
    val availableAreas: List<SheetArea>,
    val currentPiece: Piece?,
    val currentArea: SheetArea?
) : Parcelable

@Parcelize
data class SheetArea(val sheet: Sheet, val x: Int, val y: Int): Parcelable

@Parcelize
data class CuttingResult(
    val sheets: List<SheetResult>,
    val steps: List<CuttingStep>
) : Parcelable

fun findBestFitArea(piece: Piece, availableAreas: List<SheetArea>): Int {
    return availableAreas.withIndex()
        .filter { (_, area) ->
            (piece.width <= area.sheet.width && piece.height <= area.sheet.height) ||
                    (piece.height <= area.sheet.width && piece.width <= area.sheet.height)
        }
        .minByOrNull { (_, area) ->
            val remainingWidth = area.sheet.width - min(piece.width, piece.height)
            val remainingHeight = area.sheet.height - min(piece.width, piece.height)
            remainingWidth * remainingHeight
        }
        ?.index ?: -1
}


fun guillotineCut(
    sheet: Sheet,
    pieces: MutableList<Piece>,
    strategy: CuttingStrategy = CuttingStrategy.FirstFit
): CuttingResult {
    val expandedPieces = pieces.flatMap { piece -> List(piece.quantity) { piece.copy(quantity = 1) } }.toMutableList()
    val sheetsResults = mutableListOf<SheetResult>()
    val steps = mutableListOf<CuttingStep>()
    var sheetCount = 0

    while (expandedPieces.isNotEmpty()){
        val placedPieces = mutableListOf<PlacedPiece>()
        val availableArea = mutableListOf(SheetArea(sheet, 0, 0))

        fun cut(): Boolean {
            if (expandedPieces.isEmpty()) return false

            // 1. Darab kiválasztása a stratégia szerint
            val pieceIndex = when (strategy) {
                CuttingStrategy.FirstFit -> expandedPieces.indexOfFirst { true } // bármi elsőként
                CuttingStrategy.LargestFirst -> expandedPieces.withIndex()
                    .maxByOrNull { (_, p) -> p.width * p.height }
                    ?.index ?: -1
                CuttingStrategy.BestAreaFit -> expandedPieces.withIndex()
                    .minByOrNull { (_, p) -> p.width * p.height }
                    ?.index ?: -1
            }

            if (pieceIndex == -1) return false

            val piece = expandedPieces[pieceIndex]

            // 2. Hely kiválasztása a maradék területek közül
            val areaIndex = findBestFitArea(piece, availableArea)

            if (areaIndex == -1) return false

            val area = availableArea[areaIndex]
            expandedPieces.removeAt(pieceIndex)

            // 3. Eldöntjük, hogy forgatni kell-e
            val rotated = !(piece.width <= area.sheet.width && piece.height <= area.sheet.height)
            val placedWidth = if (rotated) piece.height else piece.width
            val placedHeight = if (rotated) piece.width else piece.height

            // 4. Helyezd el a darabot
            placedPieces.add(PlacedPiece(piece, area.x, area.y, rotated))

            // 5. Frissítsd az availableArea listát
            val right = Sheet(area.sheet.width - placedWidth, placedHeight)
            if (right.width > 0 && right.height > 0) {
                availableArea.add(SheetArea(right, area.x + placedWidth, area.y))
            }

            val bottom = Sheet(area.sheet.width, area.sheet.height - placedHeight)
            if (bottom.width > 0 && bottom.height > 0) {
                availableArea.add(SheetArea(bottom, area.x, area.y + placedHeight))
            }

            availableArea.removeAt(areaIndex)

            steps.add(
                CuttingStep(
                    sheetNumber = sheetCount + 1,
                    placedPieces = placedPieces.toList(),
                    availableAreas = availableArea.toList(),
                    currentPiece = piece,
                    currentArea = area
                )
            )
            return true
        }

        while (cut()) {}

        val wasteArea = availableArea.sumOf { it.sheet.width * it.sheet.height }

        sheetCount++
        sheetsResults.add(SheetResult(sheetCount, sheet, placedPieces, wasteArea))
    }

    return CuttingResult(sheetsResults, steps)
}
