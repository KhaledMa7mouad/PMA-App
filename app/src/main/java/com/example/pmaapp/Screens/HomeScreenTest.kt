package com.example.pmaapp.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pmaapp.R
import com.example.pmaapp.components.CategoryCard
import com.example.pmaapp.ui.theme.FotGreen

@Composable
fun HomeScreen(modifier: Modifier = Modifier , coachName: String, teamName: String) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.Black),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Grouping text and image together
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PMA",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    letterSpacing = 4.sp
                )
                Image(
                    painter = painterResource(id = R.drawable.pulse),
                    contentDescription = "Pulse",
                    modifier = Modifier.size(100.dp)
                )
            }

           Row {
                IconButton(
                    onClick = {},
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = FotGreen
                    ),
                    modifier = Modifier.size(34.dp) // Adjust size as needed for your icons
                ) {
                    Icon(
                        painter = painterResource(R.drawable.noti),
                        contentDescription = "Notifications"
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {},
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = FotGreen
                    ),
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.settings),
                        contentDescription = "Settings"
                    )
                }
            }

        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Welcome, Coach ",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = 4.sp
            )
            Text(
                text = "$coachName!",
                color = FotGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,

                )

        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .height(72.dp),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = backgroundColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.teamlogo),
                    contentDescription = "Football",
                    contentScale = ContentScale.Crop, // Ensures the image scales properly
                    modifier = Modifier
                        .fillMaxHeight()        // Makes the image fill the card's height
                        .aspectRatio(1f)         // Keeps the image square
                )
                Text(
                    text = "$teamName",
                    color = FotGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    letterSpacing = 4.sp,
                    modifier = Modifier
                        .weight(2f)
                        .padding(horizontal = 8.dp)
                )
            }
        }




        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),

            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CategoryCard(
                FotGreen,
                R.drawable.myteam,
                "My Team",
                "Monitor Your Players",
                onClick = {},
                modifier = Modifier.weight(1f)

            )
            CategoryCard(
                FotGreen,
                R.drawable.newplayer,
                "Add Player",
                "Edit Player Details",
                onClick = {},
                modifier = Modifier.weight(1f)
            )

        }

        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),

            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CategoryCard(
                FotGreen,
                R.drawable.insights,
                "Insights",
                "Get Facts About Players",
                onClick = {},
                modifier = Modifier.weight(1f)
            )
            CategoryCard(
                FotGreen,
                R.drawable.aicoach,
                "AI Assistant",
                "Chat with AI",
                onClick = {},
                modifier = Modifier.weight(1f)
            )

        }


    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen(Modifier, "FC Barcelona" , "Lionel Messi")
}
