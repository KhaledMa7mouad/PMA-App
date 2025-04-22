package com.example.pmaapp.Screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
fun AIPlayerSelectionScreen(
    navController: NavController,
    viewModel: AIPredictionViewModel
) {
    val allPlayers by viewModel.allPlayers.collectAsState()
    val selectedPlayers by viewModel.selectedPlayers.collectAsState()
    val selectedModel by viewModel.selectedModel.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Redirect to model selection if no model is selected
    LaunchedEffect(selectedModel) {
        if (selectedModel == null) {
            navController.navigate(AppRoutes.AI_MODEL_SELECTION_ROUTE) {
                popUpTo(AppRoutes.AI_MODEL_SELECTION_ROUTE) { inclusive = false }
            }
        }
    }

    // Handle navigation to result screen
    LaunchedEffect(Unit) {
        viewModel.navigateToResult.collect {
            navController.navigate(AppRoutes.AI_RESULT_ROUTE)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Select Player(s)")
                        selectedModel?.let {
                            Text(
                                text = "For ${it.displayName}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Button(
                    onClick = { viewModel.makePrediction() },
                    enabled = selectedPlayers.isNotEmpty() && !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Predict")
                    }
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            } else {
                Column {
                    // Show selection instructions
                    selectedModel?.let { model ->
                        val instructionText = when(model) {
                            ApiModel.PREDICT_SUBSTITUTES -> "Select at least 2 players to find substitutes"
                            else -> "Select one player for prediction"
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Text(
                                text = instructionText,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    if (allPlayers.isEmpty()) {
                        Text(
                            "No players available in the database. Add players first.",
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    } else {
                        LazyColumn {
                            items(allPlayers) { player ->
                                val isSelected = selectedPlayers.contains(player)
                                ListItem(
                                    headlineContent = { Text(player.name) },
                                    supportingContent = { Text("Position: ${player.position}") },
                                    trailingContent = {
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    },
                                    modifier = Modifier
                                        .clickable { viewModel.selectPlayer(player) }
                                        .padding(horizontal = 16.dp)
                                )
                                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}