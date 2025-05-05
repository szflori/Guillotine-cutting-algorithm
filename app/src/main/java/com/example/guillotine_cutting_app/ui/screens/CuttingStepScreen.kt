package com.example.packingoptimizerapp.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.guillotine_cutting_app.logic.CuttingResult

@Composable
fun CuttingStepScreen(result: CuttingResult, navController: NavController) {
    var currentStepIndex by remember { mutableStateOf(0) }
    val totalSteps = result.steps.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Vágási folyamat lépésenként",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "Lépés: ${currentStepIndex + 1} / $totalSteps",
            style = MaterialTheme.typography.bodyLarge
        )

        if (result.steps.isNotEmpty()) {
            CuttingStepVisualizer(step = result.steps[currentStepIndex])

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { if (currentStepIndex > 0) currentStepIndex-- },
                    enabled = currentStepIndex > 0
                ) {
                    Text("Előző")
                }

                Button(
                    onClick = { if (currentStepIndex < totalSteps - 1) currentStepIndex++ },
                    enabled = currentStepIndex < totalSteps - 1
                ) {
                    Text("Következő")
                }
            }
        } else {
            Text("Nincs elérhető lépés.")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Vissza az eredményekhez")
        }
    }
}