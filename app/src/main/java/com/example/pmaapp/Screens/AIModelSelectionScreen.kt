package com.example.pmaapp.Screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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
    PMAAppTheme(darkTheme = true, dynamicColor = false){

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Select AI Model",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
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
                        onClick = {
                            // Debug log to verify button is clicked
                            Log.d("Navigation", "Continue button clicked, navigating to ${AppRoutes.AI_PLAYER_SELECTION_ROUTE}")
                            navController.navigate(AppRoutes.AI_PLAYER_SELECTION_ROUTE)
                        },
                        enabled = selectedModel != null,
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
                        Text(
                            "Continue",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.background
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
                            .padding(12.dp)
                            .shadow(
                                elevation = if (isSelected) 8.dp else 4.dp,
                                shape = MaterialTheme.shapes.medium
                            )
                            .clickable { viewModel.selectModel(model) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Text(
                                text = model.displayName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (isSelected)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = model.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isSelected)
                                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }


}

