package com.example.pmaapp.SensorsLayer

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.PlayArrow

import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Warning
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import java.util.Locale
import com.example.pmaapp.components.*
import com.example.pmaapp.ui.theme.FotGreen

@Composable
fun SensorScreen(
    viewModel: SensorViewModel = viewModel()
) {
    val sensorData by viewModel.sensorData.collectAsState()
    val ecgDataPoints by viewModel.ecgDataPoints.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isMonitoring by viewModel.isMonitoring

    // Start monitoring when screen is first composed
    LaunchedEffect(Unit) {
        if (!isMonitoring) {
            viewModel.startMonitoring()
        }
    }

    // Stop monitoring when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopMonitoring()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with connection status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SENSOR MONITORING",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                letterSpacing = 2.sp
            )

            ConnectionStatusIndicator(
                isConnected = isConnected,
                isLoading = isLoading
            )
        }

        // Error message card
        errorMessage?.let { error ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error",
                        tint = Color.Red,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Connection Error",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = error,
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(
                        onClick = {
                            viewModel.clearError()
                            viewModel.testConnection()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Retry",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        // Health alert card
        SensorAlertCard(statusData = sensorData)

        // Real-time ECG monitor
        RealTimeECGCard(
            ecgData = ecgDataPoints,
            currentValue = sensorData?.ecg ?: 0.0f,
            prediction = sensorData?.pred ?: "Unknown"
        )

        // Quick stats row
        QuickStatsRow(statusData = sensorData)

        // Live ECG Graph Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "LIVE ECG WAVEFORM",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                LineGraph(
                    data = ecgDataPoints,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black)
                )
            }
        }

        // Vital Signs Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SensorCard(
                title = "ECG VALUE",
                value = String.format(Locale.getDefault(), "%.2f", sensorData?.ecg ?: 0.0f),
                unit = "mV",
                color = FotGreen,
                modifier = Modifier.weight(1f)
            )
            SensorCard(
                title = "TEMPERATURE",
                value = String.format(Locale.getDefault(), "%.1f", sensorData?.temp ?: 0.0f),
                unit = "°C",
                color = Color(0xFFFF6B35),
                modifier = Modifier.weight(1f)
            )
        }

        // Motion Data Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SensorCard(
                title = "SPEED",
                value = String.format(Locale.getDefault(), "%.2f", sensorData?.speed ?: 0.0f),
                unit = "m/s",
                color = Color(0xFF4ECDC4),
                modifier = Modifier.weight(1f)
            )
            SensorCard(
                title = "ACCELERATION",
                value = String.format(Locale.getDefault(), "%.2f", sensorData?.accel ?: 0.0f),
                unit = "m/s²",
                color = Color(0xFF45B7D1),
                modifier = Modifier.weight(1f)
            )
        }

        // Status Indicators Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SensorCard(
                title = "COLLISION",
                value = sensorData?.collision ?: "N/A",
                color = when (sensorData?.collision) {
                    "Detected" -> Color.Red
                    "None" -> FotGreen
                    else -> Color.Gray
                },
                isStatus = true,
                modifier = Modifier.weight(1f)
            )
            SensorCard(
                title = "ACTIVITY",
                value = sensorData?.activity ?: "N/A",
                color = Color(0xFFFFD93D),
                isStatus = true,
                modifier = Modifier.weight(1f)
            )
        }

        // Control buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    if (isMonitoring) {
                        viewModel.stopMonitoring()
                    } else {
                        viewModel.startMonitoring()
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isMonitoring) Color.Red else FotGreen
                )
            ) {
                Icon(
                    imageVector = if (isMonitoring) Icons.Default.Warning else Icons.Default.PlayArrow,
                    contentDescription = if (isMonitoring) "Stop" else "Start"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isMonitoring) "STOP" else "START",
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = { viewModel.clearHistory() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF666666))
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("CLEAR", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SensorCard(
    title: String,
    value: String,
    unit: String = "",
    color: Color,
    isStatus: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = Color.Gray,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (isStatus) {
                // Status indicator with colored background
                Box(
                    modifier = Modifier
                        .background(
                            color = color.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = value,
                        color = color,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // Numeric value with large text
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = value,
                        color = color,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                    if (unit.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = unit,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LineGraph(data: List<Float>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        if (data.isEmpty()) return@Canvas

        val widthStep = size.width / (data.size - 1).coerceAtLeast(1)
        val maxY = data.maxOrNull() ?: 1f
        val minY = data.minOrNull() ?: 0f
        val range = (maxY - minY).takeIf { it != 0f } ?: 1f

        val points = data.mapIndexed { index, value ->
            val x = index * widthStep
            val y = size.height - ((value - minY) / range * size.height)
            Offset(x, y)
        }

        // Grid lines with ECG-style appearance
        val numHorizontalLines = 4
        val numVerticalLines = 8

        // Horizontal grid lines
        repeat(numHorizontalLines + 1) { i ->
            val y = i * size.height / numHorizontalLines
            drawLine(
                color = Color.Green.copy(alpha = 0.3f),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1f
            )
        }

        // Vertical grid lines
        repeat(numVerticalLines + 1) { i ->
            val x = i * size.width / numVerticalLines
            drawLine(
                color = Color.Green.copy(alpha = 0.3f),
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 1f
            )
        }

        // ECG waveform
        if (points.size > 1) {
            val path = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    lineTo(points[i].x, points[i].y)
                }
            }

            // Glow effect
            drawPath(
                path = path,
                color = FotGreen.copy(alpha = 0.3f),
                style = Stroke(width = 6f)
            )

            // Main line
            drawPath(
                path = path,
                color = FotGreen,
                style = Stroke(width = 2f)
            )
        }
    }
}