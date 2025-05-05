package com.example.guillotine_cutting_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.guillotine_cutting_app.navigation.AppNavGraph
import com.example.packingoptimizerapp.android.ui.layouts.TopBar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            Scaffold(topBar = { TopBar(title = "Optimalizálás", navController = navController) }) {
                innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    AppNavGraph(navController)
                }
            }
        }
    }
}