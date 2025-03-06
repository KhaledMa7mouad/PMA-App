package com.example.pmaapp.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CategoryCard(
    backgroundColor: Color,
    teamLogoRes: Int,
    teamName: String,
    descriptionName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Material 3 Card with rounded corners and a clickable modifier
    Card(
        modifier = modifier
            .clickable { onClick() }
            .width(160.dp)         // Adjust width/height to your layout needs
            .height(200.dp)
            .padding(all = 4.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        // Content of the Card
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Team Logo & Name
            Column {
                Image(
                    painter = painterResource(id = teamLogoRes),
                    contentDescription = "$teamName logo",
                    modifier = Modifier
                        .size(84.dp)
                        .align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = teamName,
                    fontSize = 24.sp,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )
            }

            // Opponent & Date/Time
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Example: you can replace with an Icon or any flight/arrow image

                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = descriptionName,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold

                    )
                }

            }
        }
    }
}
