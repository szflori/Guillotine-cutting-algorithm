package com.example.guillotine_cutting_app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.packingoptimizerapp.android.ui.screens.CuttingScreen
import com.example.packingoptimizerapp.android.ui.screens.CuttingStepScreen
import com.example.packingoptimizerapp.android.ui.screens.CuttingResultScreen
import com.example.packingoptimizerapp.android.ui.screens.DashboardScreen
import com.example.guillotine_cutting_app.logic.CuttingResult

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {
        composable("dashboard") { DashboardScreen(navController) }
        composable("cutting") { CuttingScreen(navController)  }
        composable("cutting_result") { backStackEntry ->
            val  result = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<CuttingResult>("cutting_result")

            result?.let { CuttingResultScreen(it, navController) }
        }
        composable("cutting_steps") {
            val result = navController.previousBackStackEntry
                ?.savedStateHandle?.get<CuttingResult>("cutting_result")

            result?.let {
                CuttingStepScreen(result = it, navController = navController)
            }
        }
    }
}
