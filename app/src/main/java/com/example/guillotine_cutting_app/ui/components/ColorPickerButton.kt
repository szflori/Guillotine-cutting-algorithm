package com.example.packingoptimizerapp.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ColorPickerButton(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit
) {
    val colors = listOf(
        Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Cyan, Color.Magenta, Color.Gray
    )

    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(
            onClick = { expanded = true },
            modifier = Modifier
                .size(40.dp),
            colors = ButtonDefaults.buttonColors(containerColor = selectedColor),
            shape = RoundedCornerShape(4.dp)
        ) {}

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            colors.forEach { color ->
                DropdownMenuItem(
                    text = { Box(modifier = Modifier.size(20.dp).background(color)) },
                    onClick = {
                        onColorSelected(color)
                        expanded = false
                    }
                )
            }
        }
    }
}
