package com.example.pmaapp.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pmaapp.ViewModels.AIPredictionViewModel
import com.example.pmaapp.ViewModels.PredictionType
import com.example.pmaapp.ViewModels.PredictionResult
import com.example.pmaapp.navigation.AppRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIResultScreen(
    navController: NavController,
    viewModel: AIPredictionViewModel
) {
    val result by viewModel.predictionResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val selectedModel by viewModel.selectedModel.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(selectedModel?.displayName ?: "Prediction Result")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(AppRoutes.AI_MODEL_SELECTION_ROUTE) {
                            popUpTo(AppRoutes.AI_MODEL_SELECTION_ROUTE) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                isLoading -> {
                    Box(Modifier.fillMaxSize()) {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }
                }
                error != null -> {
                    Box(Modifier.fillMaxSize()) {
                        Card(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Error",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = error!!,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        viewModel.clearError()
                                        navController.navigateUp()
                                    }
                                ) {
                                    Text("Try Again")
                                }
                            }
                        }
                    }
                }
                result != null -> {
                    ResultContent(result!!)
                }
                else -> {
                    Text(
                        "No prediction result available",
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    Button(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Go Back")
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultContent(prediction: PredictionResult) {
    when (prediction.type) {
        PredictionType.POSITION -> {
            val resp = prediction.data as com.example.pmaapp.APIs.PredictPositionResponse
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Best Position",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        resp.predictedPosition,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Confidence: ${(resp.confidence * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
        PredictionType.SUBSTITUTES -> {
            val resp = prediction.data as com.example.pmaapp.APIs.PredictSubsResponse
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Recommended Substitutes",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    LazyColumn {
                        items(resp.recommendations) { rec ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(Modifier.padding(16.dp)) {
                                    Text(rec.name, style = MaterialTheme.typography.titleMedium)
                                    Text("Score: ${rec.score}", style = MaterialTheme.typography.bodyMedium)
                                    Text("Compatibility: ${rec.compatibility}", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
        PredictionType.RATING -> {
            val resp = prediction.data as com.example.pmaapp.APIs.PredictRatingResponse
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Predicted Rating",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        resp.predictedRating.toString(),
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
        }
        PredictionType.VALUE -> {
            val resp = prediction.data as com.example.pmaapp.APIs.PredictValueResponse
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Predicted Market Value",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "€${resp.predictedValue}",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
        }
        PredictionType.WAGE -> {
            val resp = prediction.data as com.example.pmaapp.APIs.PredictWageResponse
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Predicted Wage",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "€${resp.predictedWage}/week",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
        }
    }
}