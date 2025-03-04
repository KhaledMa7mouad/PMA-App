package com.example.pmaapp.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pmaapp.R
import com.example.pmaapp.ui.theme.FotGreen

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.Black)
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

            Row(
            ) {
                Box(
                    modifier = Modifier.size(48.dp) // Adjust size as needed for your icons
                )
                {
                    IconButton(
                        onClick = {},
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = FotGreen
                        )
                    ) {
                        painterResource(R.drawable.noti)
                    }

                }
                Spacer(modifier =  modifier.width(8.dp))


                Box(
                    modifier = Modifier.size(48.dp)
                ) {
                    IconButton(
                        onClick = {},
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = FotGreen
                        )
                    ) {
                        painterResource(R.drawable.settings)
                    }

                }
            }

        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Hello, Coach ",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = 4.sp
            )
            Text(
                text = "Khaled!",
                color = FotGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,

            )

        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen()
}
