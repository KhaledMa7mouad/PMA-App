package com.example.pmaapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pmaapp.SensorsLayer.StatusData
import com.example.pmaapp.ui.theme.FotGreen

@Composable
fun ConnectionStatusIndicator(
    isConnected: Boolean,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(
                    color = when {
                        isLoading -> Color.Yellow
                        isConnected -> FotGreen
                        else -> Color.Red
                    },
                    shape = CircleShape
                )
        )
        Text(
            text = when {
                isLoading -> "CONNECTING..."
                isConnected -> "CONNECTED"
                else -> "DISCONNECTED"
            },
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun SensorAlertCard(
    statusData: StatusData?,
    modifier: Modifier = Modifier
) {
    val alertColor = when {
        statusData?.isCollisionDetected == true -> Color.Red
        statusData?.isECGNormal == false -> Color(0xFFFF6B35)
        statusData?.temp ?: 0f > 38f -> Color(0xFFFF6B35)
        else -> FotGreen
    }

    val alertText = when {
        statusData?.isCollisionDetected == true -> "COLLISION DETECTED!"
        statusData?.isECGNormal == false -> "ABNORMAL ECG DETECTED"
        statusData?.temp ?: 0f > 38f -> "HIGH TEMPERATURE ALERT"
        else -> "ALL SYSTEMS NORMAL"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = alertColor.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Alert",
                tint = alertColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = alertText,
                color = alertColor,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun RealTimeECGCard(
    ecgData: List<Float>,
    currentValue: Float,
    prediction: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ECG MONITOR",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Text(
                    text = prediction,
                    color = when (prediction.lowercase()) {
                        "normal" -> FotGreen
                        "abnormal" -> Color.Red
                        else -> Color.Gray
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Current: ${String.format("%.2f", currentValue)} mV",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun QuickStatsRow(
    statusData: StatusData?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickStatCard(
            title = "BPM",
            value = String.format("%.0f", (statusData?.ecg ?: 0f) * 10), // Rough conversion
            color = FotGreen,
            modifier = Modifier.weight(1f)
        )
        QuickStatCard(
            title = "STATUS",
            value = statusData?.pred ?: "Unknown",
            color = when (statusData?.pred?.lowercase()) {
                "normal" -> FotGreen
                "abnormal" -> Color.Red
                else -> Color.Gray
            },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun QuickStatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E).copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = Color.Gray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = color,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}