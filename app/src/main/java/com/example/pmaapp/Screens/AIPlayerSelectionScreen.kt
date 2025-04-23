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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pmaapp.ViewModels.AIPredictionViewModel
import com.example.pmaapp.ViewModels.ApiModel
import com.example.pmaapp.navigation.AppRoutes
import com.example.pmaapp.ui.theme.PMAAppTheme

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

    PMAAppTheme(darkTheme = true, dynamicColor = false) {

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                "Select Player(s)",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            selectedModel?.let {
                                Text(
                                    text = "For ${it.displayName}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            bottomBar = {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    Button(
                        onClick = { viewModel.makePrediction() },
                        enabled = selectedPlayers.isNotEmpty() && !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(
                                "Predict",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Box(Modifier
                .fillMaxSize()
                .padding(padding)) {
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
                            val instructionText = when (model) {
                                ApiModel.PREDICT_SUBSTITUTES -> "Select at least 2 players to find substitutes"
                                else -> "Select one player for prediction"
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .shadow(
                                        elevation = 4.dp,
                                        shape = MaterialTheme.shapes.medium
                                    ),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                ),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Text(
                                    text = instructionText,
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }

                        if (allPlayers.isEmpty()) {
                            Text(
                                "No players available in the database. Add players first.",
                                modifier = Modifier
                                    .padding(16.dp)
                                    .align(Alignment.CenterHorizontally),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        } else {
                            LazyColumn {
                                items(allPlayers) { player ->
                                    val isSelected = selectedPlayers.contains(player)
                                    ListItem(
                                        headlineContent = {
                                            Text(
                                                player.name,
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                )
                                            )
                                        },
                                        supportingContent = {
                                            Text(
                                                "Position: ${player.position}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface.copy(
                                                    alpha = 0.7f
                                                )
                                            )
                                        },
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
                                            .padding(horizontal = 16.dp),
                                        colors = ListItemDefaults.colors(
                                            containerColor = if (isSelected)
                                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                            else
                                                MaterialTheme.colorScheme.surface
                                        )
                                    )
                                    Divider(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }


}

