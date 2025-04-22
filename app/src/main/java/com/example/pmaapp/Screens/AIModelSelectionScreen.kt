package com.example.pmaapp.Screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pmaapp.ViewModels.AIPredictionViewModel
import com.example.pmaapp.ViewModels.ApiModel
import com.example.pmaapp.navigation.AppRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIModelSelectionScreen(
    navController: NavController,
    viewModel: AIPredictionViewModel
) {
    val selectedModel by viewModel.selectedModel.collectAsState()

    // When entering this screen, clear previous selections
    LaunchedEffect(Unit) {
        viewModel.clearSelectedPlayers()
        viewModel.clearError()
        viewModel.clearResult()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Select AI Model") }) },
        bottomBar = {
            BottomAppBar {
                Button(
                    onClick = {
                        // Debug log to verify button is clicked
                        Log.d("Navigation", "Continue button clicked, navigating to ${AppRoutes.AI_PLAYER_SELECTION_ROUTE}")
                        navController.navigate(AppRoutes.AI_PLAYER_SELECTION_ROUTE)
                    },
                    enabled = selectedModel != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Continue")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(ApiModel.values()) { model ->
                val isSelected = selectedModel == model
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { viewModel.selectModel(model) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = model.displayName,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = model.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}