package com.example.packingoptimizerapp.android.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.guillotine_cutting_app.logic.CuttingStrategy
import com.example.guillotine_cutting_app.logic.Piece
import com.example.guillotine_cutting_app.logic.Sheet
import com.example.guillotine_cutting_app.logic.guillotineCut
import com.example.packingoptimizerapp.android.ui.components.ColorPickerButton
import com.example.packingoptimizerapp.android.ui.components.CustomNumberField
import com.example.packingoptimizerapp.android.ui.components.CuttingStrategyDropdown

data class LapItem(
    var width: String,
    var height: String,
    var quantity: String,
    var color: Color
)

@Composable
fun CuttingScreen(navController: NavController) {
    var sheetWidth by remember { mutableStateOf("200") }
    var sheetHeight by remember { mutableStateOf("200") }
    var lapList = remember { mutableStateListOf<LapItem>() }
    var selectedStrategy by remember { mutableStateOf(CuttingStrategy.FirstFit) }

    val isOptimizationEnabled by remember { derivedStateOf {
        // Készlet szélessége helyes szám (> 0)
        val widthValid = sheetWidth.toIntOrNull()?.let { it > 0 } == true
        val heightValid = sheetHeight.toIntOrNull()?.let { it > 0 } == true

        // Legalább két lap, és minden lap érvényes számokat tartalmaz
        val validLapsCount = lapList.count { lap ->
            val width = lap.width.toIntOrNull()
            val height = lap.height.toIntOrNull()
            val quantity = lap.quantity.toIntOrNull()

            width != null && width > 0 && height != null && height > 0 &&  quantity != null && quantity > 0
        }

        widthValid && heightValid && validLapsCount >= 2
    } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("2D Vágás", style = MaterialTheme.typography.headlineSmall)

        Column(
            modifier = Modifier.fillMaxWidth()
        ){
            Text("Készlet", style = MaterialTheme.typography.titleMedium)

            CustomNumberField(
                value = sheetWidth,
                label = "Szélessége (cm)",
                onValueChange = {sheetWidth = it},
                onClearClick = { sheetWidth = "" },
                modifier = Modifier.fillMaxWidth()
            )

            CustomNumberField(
                value = sheetHeight,
                label = "Hosszúság (cm)",
                onValueChange = {sheetHeight = it},
                onClearClick = { sheetHeight = "" },
                modifier = Modifier.fillMaxWidth()
            )
        }

        CuttingStrategyDropdown(
            selectedStrategy = selectedStrategy,
            onStrategySelected = { selectedStrategy = it }
        )

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Lapok", style = MaterialTheme.typography.titleMedium)

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ){
                itemsIndexed(lapList) { index, lap ->
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ColorPickerButton(
                                selectedColor = lap.color,
                                onColorSelected = { newColor ->
                                    lapList[index] = lapList[index].copy(color = newColor)
                                }
                            )

                            Text("${index + 1}. Lap", style = MaterialTheme.typography.titleSmall)

                            IconButton(onClick = {
                                lapList.removeAt(index)
                            }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Törlés")
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ){
                            CustomNumberField(
                                value = lap.width,
                                label = "Szélesség (cm)",
                                onValueChange = { newValue ->
                                    lapList[index] = lapList[index].copy(width = newValue)
                                },
                                modifier = Modifier.weight(1f)
                            )

                            CustomNumberField(
                                value = lap.height,
                                label = "Hosszúság (cm)",
                                onValueChange = { newValue ->
                                    lapList[index] = lapList[index].copy(height = newValue)
                                },
                                modifier = Modifier.weight(1f)
                            )

                            CustomNumberField(
                                value = lap.quantity,
                                label = "Mennyiség",
                                onValueChange = { newValue ->
                                    lapList[index] = lapList[index].copy(quantity = newValue)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {  lapList.add(LapItem("", "", "", Color.Gray)) },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Hozzáadás")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Lap hozzáadása")
            }

            Button(
                onClick = {
                    val widthInt = sheetWidth.toIntOrNull() ?: return@Button
                    val heightInt = sheetHeight.toIntOrNull() ?: return@Button

                    val itemList = lapList.mapNotNull { lap ->
                        val width  = lap.width.toIntOrNull()
                        val height  = lap.height.toIntOrNull()
                        val quantity  = lap.quantity.toIntOrNull()
                        val colorLong = lap.color.value // <- Így konvertálunk Long-á
                        if (width != null && height != null && quantity != null) {
                              Piece(width, height, quantity, colorLong)
                        } else null
                    }.toMutableList()

                    val result = guillotineCut(
                        Sheet(widthInt, heightInt),
                        itemList,
                        strategy = selectedStrategy
                    )

                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        key = "cutting_result",
                        value = result
                    )

                    navController.navigate("cutting_result")
                },
                enabled = isOptimizationEnabled,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Optimalizálás")
            }
        }
    }
}