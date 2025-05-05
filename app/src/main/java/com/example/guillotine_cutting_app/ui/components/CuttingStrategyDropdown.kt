package com.example.packingoptimizerapp.android.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*;
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.guillotine_cutting_app.logic.CuttingStrategy
import com.example.guillotine_cutting_app.logic.getDescription


@Composable
fun CuttingStrategyDropdown (
    selectedStrategy: CuttingStrategy,
    onStrategySelected: (CuttingStrategy) -> Unit
){
    var expanded by remember { mutableStateOf(false) }

    Column {
        OutlinedButton(onClick = { expanded = true }) {
            Text(text = "Stratégia: ${selectedStrategy.name}")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            CuttingStrategy.values().forEach { strategy ->
                DropdownMenuItem(
                    text = { Text(strategy.name) },
                    onClick = {
                        onStrategySelected(strategy)
                        expanded = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = selectedStrategy.getDescription(),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}