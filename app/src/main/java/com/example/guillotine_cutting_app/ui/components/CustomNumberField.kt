package com.example.packingoptimizerapp.android.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun CustomNumberField(
    value: String,
    label: String,
    maxLength: Int = 5,
    onValueChange: (String) -> Unit,
    onClearClick: (() -> Unit)? = null,
    modifier: Modifier
) {
    val trailingIconComposable: (@Composable (() -> Unit))? =
        if (value.isNotEmpty() && onClearClick != null) {
            {
                IconButton(onClick = onClearClick) {
                    Icon(Icons.Filled.Close, contentDescription = "Törlés")
                }
            }
        } else {
            null
        }

    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            val filtered = newValue.filter { it.isDigit() }
            if (filtered.length <= maxLength && filtered.toIntOrNull() != null && filtered.toInt() >= 0) {
                onValueChange(filtered)
            }
        },
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        modifier = modifier,
        trailingIcon = trailingIconComposable
    )
}