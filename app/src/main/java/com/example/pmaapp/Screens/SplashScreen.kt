package com.example.pmaapp.Screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pmaapp.R
import com.example.pmaapp.navigation.AppRoutes

import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    // Animation controller for alpha (opacity)
    val alphaAnim = remember { Animatable(0f) }

    // Launch animation when composable is first created
    LaunchedEffect(Unit) {
        // Start with 0 opacity and animate to 1 (fade-in)
        alphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )

        // Keep the splash screen visible for 1.2 seconds
        delay(1200)

        // Animate back to 0 opacity (fade-out)
        alphaAnim.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 1000)
        )

        // Navigate to next screen after animation completes
        navController.navigate(AppRoutes.ONBOARDING_ROUTE) {
            popUpTo(AppRoutes.SPLASH_ROUTE) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .alpha(alphaAnim.value)
        ) {

            Image(
                painter = painterResource(id = R.drawable.appicon),
                contentDescription = "App Logo",
                modifier = Modifier.size(300.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))


            Text(
                text = "Player Monitoring App",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

fun Modifier.alpha(alpha: Float) = this.then(
    Modifier.graphicsLayer(alpha = alpha)
)
